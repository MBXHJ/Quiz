import 'package:flutter/material.dart';
import '../db/database.dart';
import '../models/models.dart';

class AppProvider extends ChangeNotifier {
  final AppDatabase _db = AppDatabase();

  List<QuestionBank> _banks = [];
  List<Question> _questions = [];
  int _currentIndex = 0;
  String? _selectedAnswer;
  bool _showResult = false;
  bool _isCorrect = false;
  int _wrongCount = 0;
  Set<String> _multiSelected = {};

  List<QuestionBank> get banks => _banks;
  List<Question> get questions => _questions;
  int get currentIndex => _currentIndex;
  String? get selectedAnswer => _selectedAnswer;
  bool get showResult => _showResult;
  bool get isCorrect => _isCorrect;
  int get wrongCount => _wrongCount;
  Set<String> get multiSelected => _multiSelected;

  Question? get currentQuestion =>
    _currentIndex < _questions.length ? _questions[_currentIndex] : null;

  int get totalQuestions => _questions.length;
  int get answeredCount =>
    _questions.where((q) => q.questionType == QuestionType.multi ? false : true).length;

  Future<void> loadBanks() async {
    _banks = await _db.getAllBanks();
    notifyListeners();
  }

  Future<void> loadQuestions(int bankId, {String? type}) async {
    if (type != null) {
      _questions = await _db.getQuestionsByType(bankId, type);
    } else {
      _questions = await _db.getQuestionsByBank(bankId);
    }
    _currentIndex = 0;
    _selectedAnswer = null;
    _showResult = false;
    notifyListeners();
  }

  Future<void> loadRandomQuestions(int bankId, {int? limit}) async {
    _questions = await _db.getQuestionsRandom(bankId, limit: limit);
    _currentIndex = 0;
    _selectedAnswer = null;
    _showResult = false;
    notifyListeners();
  }

  Future<void> loadWrongQuestions(int bankId) async {
    final rows = await _db.getWrongWithQuestion(bankId);
    _questions = rows.map((r) => Question.fromMap(r)).toList();
    _currentIndex = 0;
    _selectedAnswer = null;
    _showResult = false;
    notifyListeners();
  }

  void selectAnswer(String answer, {bool isMulti = false}) {
    if (_showResult) return;
    final q = currentQuestion;
    if (q == null) return;

    if (isMulti) {
      if (_multiSelected.contains(answer)) {
        _multiSelected.remove(answer);
      } else {
        _multiSelected.add(answer);
      }
      notifyListeners();
    } else {
      _selectedAnswer = answer;
      _isCorrect = answer == q.answer;
      _showResult = true;
      if (!_isCorrect) _recordWrong(q.id!);
      _loadWrongCount(q.id!);
      notifyListeners();
    }
  }

  void confirmMulti() {
    if (_showResult) return;
    final q = currentQuestion;
    if (q == null) return;

    final answer = _multiSelected.toList()..sort();
    final result = answer.join('');
    _selectedAnswer = result;
    _isCorrect = result == q.answer;
    _showResult = true;
    if (!_isCorrect) _recordWrong(q.id!);
    _loadWrongCount(q.id!);
    notifyListeners();
  }

  void nextQuestion() {
    if (_currentIndex < _questions.length - 1) {
      _currentIndex++;
      _selectedAnswer = null;
      _showResult = false;
      _isCorrect = false;
      _multiSelected = {};
      _wrongCount = 0;
      final q = currentQuestion;
      if (q != null) _loadWrongCount(q.id!);
      notifyListeners();
    }
  }

  void previousQuestion() {
    if (_currentIndex > 0) {
      _currentIndex--;
      _selectedAnswer = null;
      _showResult = false;
      _isCorrect = false;
      _multiSelected = {};
      _wrongCount = 0;
      final q = currentQuestion;
      if (q != null) _loadWrongCount(q.id!);
      notifyListeners();
    }
  }

  void goToQuestion(int index) {
    _currentIndex = index;
    _selectedAnswer = null;
    _showResult = false;
    _isCorrect = false;
    _multiSelected = {};
    notifyListeners();
  }

  Future<void> _recordWrong(int questionId) async {
    await _db.upsertWrong(questionId);
  }

  Future<void> _loadWrongCount(int questionId) async {
    final c = await _db.getWrongCount(questionId);
    _wrongCount = c ?? 0;
    notifyListeners();
  }

  // Exam mode
  Map<int, String> _examAnswers = {};
  Map<int, Set<String>> _examMulti = {};
  bool _examFinished = false;
  int _examScore = 0;

  Map<int, String> get examAnswers => _examAnswers;
  Map<int, Set<String>> get examMulti => _examMulti;
  bool get examFinished => _examFinished;
  int get examScore => _examScore;

  void initExam(int bankId) async {
    _examAnswers = {};
    _examMulti = {};
    _examFinished = false;
    _examScore = 0;
    await loadRandomQuestions(bankId);
  }

  void examSelectAnswer(int qId, String answer) {
    final q = questions.firstWhere((q) => q.id == qId);
    if (q.questionType == QuestionType.multi) {
      final cur = _examMulti[qId] ?? {};
      if (cur.contains(answer)) {
        cur.remove(answer);
      } else {
        cur.add(answer);
      }
      _examMulti[qId] = cur;
    } else {
      _examAnswers[qId] = answer;
    }
    notifyListeners();
  }

  void finishExam() {
    int correct = 0;
    for (final q in _questions) {
      final userAns = q.questionType == QuestionType.multi
        ? (_examMulti[q.id!]?.toList()..sort())?.join('') ?? ''
        : _examAnswers[q.id!] ?? '';
      if (userAns == q.answer) correct++;
    }
    _examScore = questions.isNotEmpty ? (correct * 100 ~/ questions.length) : 0;
    _examFinished = true;
    notifyListeners();
  }

  bool isQuestionAnswered(int qId) {
    return _examAnswers.containsKey(qId) || _examMulti.containsKey(qId);
  }
}
