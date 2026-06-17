import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart' as p;
import '../models/models.dart';

class AppDatabase {
  static final AppDatabase _instance = AppDatabase._();
  factory AppDatabase() => _instance;
  AppDatabase._();

  Database? _db;

  Future<Database> get db async {
    if (_db != null) return _db!;
    _db = await _init();
    return _db!;
  }

  Future<Database> _init() async {
    final path = p.join(await getDatabasesPath(), 'quiz_app.db');
    return openDatabase(
      path,
      version: 1,
      onCreate: (db, v) async {
        await db.execute('''
          CREATE TABLE question_banks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            description TEXT DEFAULT '',
            questionCount INTEGER DEFAULT 0,
            importDate INTEGER NOT NULL
          )
        ''');
        await db.execute('''
          CREATE TABLE questions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            bankId INTEGER NOT NULL,
            questionType TEXT NOT NULL,
            content TEXT NOT NULL,
            options TEXT DEFAULT '',
            answer TEXT DEFAULT '',
            analysis TEXT DEFAULT '',
            FOREIGN KEY (bankId) REFERENCES question_banks(id) ON DELETE CASCADE
          )
        ''');
        await db.execute('''
          CREATE TABLE wrong_records (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            questionId INTEGER NOT NULL UNIQUE,
            wrongCount INTEGER DEFAULT 1,
            lastWrongTime INTEGER NOT NULL,
            isRemoved INTEGER DEFAULT 0,
            FOREIGN KEY (questionId) REFERENCES questions(id) ON DELETE CASCADE
          )
        ''');
        await db.execute('''
          CREATE TABLE exam_records (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            bankId INTEGER NOT NULL,
            score INTEGER NOT NULL,
            totalCount INTEGER NOT NULL,
            correctCount INTEGER NOT NULL,
            questionDetails TEXT DEFAULT '',
            examDate INTEGER NOT NULL,
            FOREIGN KEY (bankId) REFERENCES question_banks(id) ON DELETE CASCADE
          )
        ''');
      },
    );
  }

  // Question Banks
  Future<List<QuestionBank>> getAllBanks() async {
    final d = await db;
    final rows = await d.query('question_banks', orderBy: 'importDate DESC');
    return rows.map((r) => QuestionBank.fromMap(r)).toList();
  }

  Future<int> insertBank(QuestionBank bank) async {
    final d = await db;
    return d.insert('question_banks', bank.toMap());
  }

  Future<void> deleteBank(int id) async {
    final d = await db;
    await d.delete('question_banks', where: 'id = ?', whereArgs: [id]);
  }

  // Questions
  Future<List<Question>> getQuestionsByBank(int bankId) async {
    final d = await db;
    final rows = await d.query('questions', where: 'bankId = ?', whereArgs: [bankId]);
    return rows.map((r) => Question.fromMap(r)).toList();
  }

  Future<List<Question>> getQuestionsByType(int bankId, String type) async {
    final d = await db;
    final rows = await d.query('questions',
      where: 'bankId = ? AND questionType = ?', whereArgs: [bankId, type]);
    return rows.map((r) => Question.fromMap(r)).toList();
  }

  Future<List<Question>> getQuestionsRandom(int bankId, {int? limit}) async {
    final d = await db;
    final rows = await d.query('questions',
      where: 'bankId = ?', whereArgs: [bankId],
      orderBy: 'RANDOM()', limit: limit);
    return rows.map((r) => Question.fromMap(r)).toList();
  }

  Future<void> insertQuestions(List<Question> questions) async {
    final d = await db;
    final batch = d.batch();
    for (final q in questions) {
      batch.insert('questions', q.toMap());
    }
    await batch.commit(noResult: true);
  }

  Future<int> getQuestionCount(int bankId) async {
    final d = await db;
    final r = await d.rawQuery(
      'SELECT COUNT(*) as c FROM questions WHERE bankId = ?', [bankId]);
    return Sqflite.firstIntValue(r) ?? 0;
  }

  // Wrong Records
  Future<List<Map<String, dynamic>>> getWrongWithQuestion(int bankId) async {
    final d = await db;
    return d.rawQuery('''
      SELECT q.*, wr.id as wr_id, wr.wrongCount, wr.lastWrongTime, wr.isRemoved
      FROM wrong_records wr
      INNER JOIN questions q ON wr.questionId = q.id
      WHERE q.bankId = ? AND wr.isRemoved = 0
      ORDER BY wr.lastWrongTime DESC
    ''', [bankId]);
  }

  Future<int?> getWrongCount(int questionId) async {
    final d = await db;
    final r = await d.query('wrong_records',
      columns: ['wrongCount'], where: 'questionId = ?', whereArgs: [questionId]);
    if (r.isEmpty) return null;
    return r.first['wrongCount'] as int;
  }

  Future<void> upsertWrong(int questionId) async {
    final d = await db;
    final existing = await d.query('wrong_records',
      where: 'questionId = ?', whereArgs: [questionId]);
    if (existing.isEmpty) {
      await d.insert('wrong_records', {
        'questionId': questionId,
        'wrongCount': 1,
        'lastWrongTime': DateTime.now().millisecondsSinceEpoch,
        'isRemoved': 0,
      });
    } else {
      final cur = existing.first;
      await d.update('wrong_records', {
        'wrongCount': (cur['wrongCount'] as int) + 1,
        'lastWrongTime': DateTime.now().millisecondsSinceEpoch,
      }, where: 'questionId = ?', whereArgs: [questionId]);
    }
  }

  Future<void> removeWrong(int questionId) async {
    final d = await db;
    await d.update('wrong_records', {'isRemoved': 1},
      where: 'questionId = ?', whereArgs: [questionId]);
  }

  // Exam Records
  Future<void> insertExamRecord(ExamRecord record) async {
    final d = await db;
    await d.insert('exam_records', record.toMap());
  }

  Future<List<ExamRecord>> getExamRecords(int bankId) async {
    final d = await db;
    final rows = await d.query('exam_records',
      where: 'bankId = ?', whereArgs: [bankId], orderBy: 'examDate DESC');
    return rows.map((r) => ExamRecord.fromMap(r)).toList();
  }

  Future<bool> isDatabaseEmpty() async {
    final d = await db;
    final r = await d.rawQuery('SELECT COUNT(*) as c FROM question_banks');
    return (Sqflite.firstIntValue(r) ?? 0) == 0;
  }
}
