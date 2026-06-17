class QuestionBank {
  final int? id;
  final String name;
  final String description;
  final int questionCount;
  final DateTime importDate;

  QuestionBank({
    this.id,
    required this.name,
    this.description = '',
    this.questionCount = 0,
    DateTime? importDate,
  }) : importDate = importDate ?? DateTime.now();

  Map<String, dynamic> toMap() => {
    if (id != null) 'id': id,
    'name': name,
    'description': description,
    'questionCount': questionCount,
    'importDate': importDate.millisecondsSinceEpoch,
  };

  factory QuestionBank.fromMap(Map<String, dynamic> m) => QuestionBank(
    id: m['id'] as int?,
    name: m['name'] as String,
    description: m['description'] as String? ?? '',
    questionCount: m['questionCount'] as int? ?? 0,
    importDate: DateTime.fromMillisecondsSinceEpoch(m['importDate'] as int),
  );
}

enum QuestionType { single, multi, judge }

class Question {
  final int? id;
  final int bankId;
  final QuestionType questionType;
  final String content;
  final List<String> options;
  final String answer;
  final String analysis;

  Question({
    this.id,
    required this.bankId,
    required this.questionType,
    required this.content,
    this.options = const [],
    this.answer = '',
    this.analysis = '',
  });

  Map<String, dynamic> toMap() => {
    if (id != null) 'id': id,
    'bankId': bankId,
    'questionType': questionType.name,
    'content': content,
    'options': options.join('|||'),
    'answer': answer,
    'analysis': analysis,
  };

  factory Question.fromMap(Map<String, dynamic> m) => Question(
    id: m['id'] as int?,
    bankId: m['bankId'] as int,
    questionType: QuestionType.values.firstWhere(
      (e) => e.name == m['questionType'],
      orElse: () => QuestionType.single,
    ),
    content: m['content'] as String,
    options: (m['options'] as String?)?.split('|||') ?? [],
    answer: m['answer'] as String? ?? '',
    analysis: m['analysis'] as String? ?? '',
  );

  String get typeLabel {
    switch (questionType) {
      case QuestionType.single: return '单选题';
      case QuestionType.multi: return '多选题';
      case QuestionType.judge: return '判断题';
    }
  }
}

class WrongRecord {
  final int? id;
  final int questionId;
  final int wrongCount;
  final DateTime lastWrongTime;
  final bool isRemoved;

  WrongRecord({
    this.id,
    required this.questionId,
    this.wrongCount = 1,
    DateTime? lastWrongTime,
    this.isRemoved = false,
  }) : lastWrongTime = lastWrongTime ?? DateTime.now();

  Map<String, dynamic> toMap() => {
    if (id != null) 'id': id,
    'questionId': questionId,
    'wrongCount': wrongCount,
    'lastWrongTime': lastWrongTime.millisecondsSinceEpoch,
    'isRemoved': isRemoved ? 1 : 0,
  };

  factory WrongRecord.fromMap(Map<String, dynamic> m) => WrongRecord(
    id: m['id'] as int?,
    questionId: m['questionId'] as int,
    wrongCount: m['wrongCount'] as int? ?? 1,
    lastWrongTime: DateTime.fromMillisecondsSinceEpoch(m['lastWrongTime'] as int),
    isRemoved: (m['isRemoved'] as int?) == 1,
  );
}

class ExamRecord {
  final int? id;
  final int bankId;
  final int score;
  final int totalCount;
  final int correctCount;
  final String questionDetails;
  final DateTime examDate;

  ExamRecord({
    this.id,
    required this.bankId,
    required this.score,
    required this.totalCount,
    required this.correctCount,
    this.questionDetails = '',
    DateTime? examDate,
  }) : examDate = examDate ?? DateTime.now();

  Map<String, dynamic> toMap() => {
    if (id != null) 'id': id,
    'bankId': bankId,
    'score': score,
    'totalCount': totalCount,
    'correctCount': correctCount,
    'questionDetails': questionDetails,
    'examDate': examDate.millisecondsSinceEpoch,
  };

  factory ExamRecord.fromMap(Map<String, dynamic> m) => ExamRecord(
    id: m['id'] as int?,
    bankId: m['bankId'] as int,
    score: m['score'] as int,
    totalCount: m['totalCount'] as int,
    correctCount: m['correctCount'] as int,
    questionDetails: m['questionDetails'] as String? ?? '',
    examDate: DateTime.fromMillisecondsSinceEpoch(m['examDate'] as int),
  );
}
