import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/models.dart';
import '../providers/app_provider.dart';

class ExamScreen extends StatelessWidget {
  const ExamScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();

    if (provider.examFinished) {
      return _ExamResult(provider: provider);
    }

    final q = provider.currentQuestion;
    if (q == null) return Scaffold(
      appBar: AppBar(title: const Text('模拟考试')),
      body: const Center(child: Text('暂无题目')),
    );

    final answered = provider.questions
      .where((q) => provider.isQuestionAnswered(q.id!)).length;

    return Scaffold(
      appBar: AppBar(
        title: const Text('模拟考试'),
        actions: [
          TextButton(
            onPressed: () {
              showDialog(
                context: context,
                builder: (ctx) => AlertDialog(
                  title: const Text('确认交卷？'),
                  content: Text('还有 ${provider.totalQuestions - answered} 道题未作答'),
                  actions: [
                    TextButton(onPressed: () => Navigator.pop(ctx),
                      child: const Text('继续答题')),
                    TextButton(onPressed: () {
                      Navigator.pop(ctx);
                      provider.finishExam();
                    }, child: const Text('交卷')),
                  ],
                ),
              );
            },
            child: Text('交卷 ($answered/${provider.totalQuestions})'),
          ),
        ],
      ),
      body: Column(
        children: [
          LinearProgressIndicator(
            value: provider.totalQuestions > 0
              ? (provider.currentIndex + 1) / provider.totalQuestions : 0,
          ),
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('第 ${provider.currentIndex + 1} 题 / 共 ${provider.totalQuestions} 题',
                    style: TextStyle(color: Colors.grey[600])),
                  const SizedBox(height: 16),
                  Text(q.content, style: const TextStyle(fontSize: 16)),
                  const SizedBox(height: 16),
                  if (q.questionType == QuestionType.single)
                    ...q.options.map((opt) {
                      final letter = opt.split('.')[0].trim();
                      return Card(
                        margin: const EdgeInsets.symmetric(vertical: 4),
                        child: ListTile(
                          leading: Radio<bool>(
                            value: true,
                            groupValue: provider.examAnswers[q.id!] == letter ? true : null,
                            onChanged: (_) => provider.examSelectAnswer(q.id!, letter),
                          ),
                          title: Text(opt.substring(opt.indexOf('.') + 1).trim()),
                          onTap: () => provider.examSelectAnswer(q.id!, letter),
                        ),
                      );
                    }),
                  if (q.questionType == QuestionType.multi)
                    ...q.options.map((opt) {
                      final letter = opt.split('.')[0].trim();
                      return Card(
                        margin: const EdgeInsets.symmetric(vertical: 4),
                        child: CheckboxListTile(
                          value: (provider.examMulti[q.id!] ?? {}).contains(letter),
                          onChanged: (_) => provider.examSelectAnswer(q.id!, letter),
                          title: Text(opt.substring(opt.indexOf('.') + 1).trim()),
                        ),
                      );
                    }),
                  if (q.questionType == QuestionType.judge)
                    Row(
                      children: ['正确', '错误'].map((v) => Expanded(
                        child: Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 8),
                          child: SizedBox(
                            height: 80,
                            child: Card(
                              color: provider.examAnswers[q.id!] == v
                                ? Colors.blue.shade50 : null,
                              child: InkWell(
                                onTap: () => provider.examSelectAnswer(q.id!, v),
                                child: Center(child: Text(v, style: const TextStyle(fontSize: 18))),
                              ),
                            ),
                          ),
                        ),
                      )).toList(),
                    ),
                ],
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                OutlinedButton.icon(
                  onPressed: provider.currentIndex > 0 ? provider.previousQuestion : null,
                  icon: const Icon(Icons.arrow_back),
                  label: const Text('上一题'),
                ),
                ElevatedButton.icon(
                  onPressed: provider.currentIndex < provider.totalQuestions - 1
                    ? provider.nextQuestion
                    : () => provider.finishExam(),
                  label: Text(provider.currentIndex < provider.totalQuestions - 1
                    ? '下一题' : '交卷'),
                  icon: provider.currentIndex < provider.totalQuestions - 1
                    ? const Icon(Icons.arrow_forward)
                    : const Icon(Icons.check),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _ExamResult extends StatelessWidget {
  final AppProvider provider;
  const _ExamResult({required this.provider});

  @override
  Widget build(BuildContext context) {
    final total = provider.totalQuestions;
    final correct = provider.questions
      .where((q) {
        final userAns = q.questionType == QuestionType.multi
          ? (provider.examMulti[q.id!]?.toList()..sort())?.join('') ?? ''
          : provider.examAnswers[q.id!] ?? '';
        return userAns == q.answer;
      }).length;
    final score = total > 0 ? (correct * 100 ~/ total) : 0;

    return Scaffold(
      appBar: AppBar(title: const Text('考试结果')),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                width: 180, height: 180,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: score >= 60 ? Colors.green.shade50 : Colors.red.shade50,
                ),
                child: Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text('$score',
                        style: TextStyle(
                          fontSize: 48, fontWeight: FontWeight.bold,
                          color: score >= 60 ? Colors.green : Colors.red)),
                      Text('分', style: TextStyle(color: Colors.grey[600])),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 32),
              Text(score >= 60 ? '恭喜通过！' : '继续加油！',
                style: const TextStyle(fontSize: 24),
              ),
              const SizedBox(height: 24),
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          _ResultStat('$total', '总题数'),
                          _ResultStat('$correct', '正确', color: Colors.green),
                          _ResultStat('${total - correct}', '错误', color: Colors.red),
                        ],
                      ),
                      const SizedBox(height: 16),
                      ClipRRect(
                        borderRadius: BorderRadius.circular(8),
                        child: LinearProgressIndicator(
                          value: total > 0 ? correct / total : 0,
                          minHeight: 8,
                          color: Colors.green,
                          backgroundColor: Colors.red.shade100,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 32),
              SizedBox(
                width: double.infinity, height: 48,
                child: ElevatedButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('返回题库'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _ResultStat extends StatelessWidget {
  final String value, label;
  final Color? color;
  const _ResultStat(this.value, this.label, {this.color});

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Text(value, style: TextStyle(
        fontSize: 22, fontWeight: FontWeight.bold, color: color)),
      Text(label, style: const TextStyle(fontSize: 12)),
    ]);
  }
}
