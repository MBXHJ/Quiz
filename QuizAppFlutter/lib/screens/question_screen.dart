import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/models.dart';
import '../providers/app_provider.dart';

class QuestionScreen extends StatelessWidget {
  const QuestionScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();
    final q = provider.currentQuestion;

    if (provider.questions.isEmpty) {
      return Scaffold(
        appBar: AppBar(title: const Text('刷题')),
        body: const Center(child: Text('暂无题目')),
      );
    }

    if (q == null) return const SizedBox.shrink();

    final progress = provider.totalQuestions > 0
      ? (provider.currentIndex + 1) / provider.totalQuestions
      : 0.0;

    return Scaffold(
      appBar: AppBar(title: const Text('刷题')),
      body: Column(
        children: [
          LinearProgressIndicator(value: progress),
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text('${provider.currentIndex + 1} / ${provider.totalQuestions}',
                        style: const TextStyle(fontWeight: FontWeight.bold)),
                      _TypeTag(q.typeLabel),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Text(q.content, style: const TextStyle(fontSize: 16)),
                  const SizedBox(height: 16),
                  if (q.questionType == QuestionType.single)
                    ...q.options.map((opt) => _SingleOption(
                      option: opt,
                      selected: provider.selectedAnswer == opt.split('.')[0].trim(),
                      showResult: provider.showResult,
                      correct: opt.split('.')[0].trim() == q.answer,
                      onTap: () => provider.selectAnswer(opt.split('.')[0].trim()),
                    )),
                  if (q.questionType == QuestionType.multi)
                    ...q.options.map((opt) => _MultiOption(
                      option: opt,
                      selected: provider.multiSelected.contains(opt.split('.')[0].trim()),
                      showResult: provider.showResult,
                      correct: q.answer.contains(opt.split('.')[0].trim()),
                      onTap: () => provider.selectAnswer(
                        opt.split('.')[0].trim(), isMulti: true),
                    )),
                  if (q.questionType == QuestionType.multi && !provider.showResult)
                    Padding(
                      padding: const EdgeInsets.only(top: 8),
                      child: SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: provider.confirmMulti,
                          child: const Text('确认提交'),
                        ),
                      ),
                    ),
                  if (q.questionType == QuestionType.judge)
                    Row(
                      children: [
                        Expanded(
                          child: _JudgeButton(
                            text: '正确',
                            selected: provider.selectedAnswer == '正确',
                            showResult: provider.showResult,
                            correct: q.answer == '正确',
                            onTap: () => provider.selectAnswer('正确'),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: _JudgeButton(
                            text: '错误',
                            selected: provider.selectedAnswer == '错误',
                            showResult: provider.showResult,
                            correct: q.answer == '错误',
                            onTap: () => provider.selectAnswer('错误'),
                          ),
                        ),
                      ],
                    ),
                  if (provider.showResult) ...[
                    const SizedBox(height: 16),
                    Card(
                      color: provider.isCorrect ? Colors.green.shade50 : Colors.red.shade50,
                      child: Padding(
                        padding: const EdgeInsets.all(16),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              provider.isCorrect ? '回答正确！' : '回答错误！',
                              style: TextStyle(
                                fontSize: 16, fontWeight: FontWeight.bold,
                                color: provider.isCorrect ? Colors.green : Colors.red,
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text('正确答案: ${q.answer}'),
                            if (q.analysis.isNotEmpty) ...[
                              const SizedBox(height: 8),
                              Text('解析: ${q.analysis}',
                                style: TextStyle(color: Colors.grey[600])),
                            ],
                            if (provider.wrongCount > 0) ...[
                              const SizedBox(height: 8),
                              Text('本题已错 ${provider.wrongCount} 次',
                                style: const TextStyle(color: Colors.red)),
                            ],
                          ],
                        ),
                      ),
                    ),
                  ],
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
                    ? provider.nextQuestion : null,
                  icon: const Text('下一题'),
                  label: const Icon(Icons.arrow_forward),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _TypeTag extends StatelessWidget {
  final String label;
  const _TypeTag(this.label);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.blue.shade50,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(label, style: TextStyle(color: Colors.blue.shade700, fontSize: 12)),
    );
  }
}

class _SingleOption extends StatelessWidget {
  final String option;
  final bool selected, showResult, correct;
  final VoidCallback onTap;

  const _SingleOption({
    required this.option, required this.selected,
    required this.showResult, required this.correct, required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final letter = option.split('.')[0].trim();
    final text = option.substring(option.indexOf('.') + 1).trim();
    Color? bg;
    if (showResult && correct) bg = Colors.green.shade50;
    else if (showResult && selected && !correct) bg = Colors.red.shade50;

    return Card(
      color: bg,
      margin: const EdgeInsets.symmetric(vertical: 4),
      child: ListTile(
        leading: Radio<bool>(
          value: true,
          groupValue: selected ? true : null,
          onChanged: (_) => onTap(),
        ),
        title: Text(text),
        onTap: onTap,
      ),
    );
  }
}

class _MultiOption extends StatelessWidget {
  final String option;
  final bool selected, showResult, correct;
  final VoidCallback onTap;

  const _MultiOption({
    required this.option, required this.selected,
    required this.showResult, required this.correct, required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final text = option.substring(option.indexOf('.') + 1).trim();
    Color? bg;
    if (showResult && correct) bg = Colors.green.shade50;
    else if (showResult && selected && !correct) bg = Colors.red.shade50;

    return Card(
      color: bg,
      margin: const EdgeInsets.symmetric(vertical: 4),
      child: CheckboxListTile(
        value: selected,
        onChanged: showResult ? null : (_) => onTap(),
        title: Text(text),
      ),
    );
  }
}

class _JudgeButton extends StatelessWidget {
  final String text;
  final bool selected, showResult, correct;
  final VoidCallback onTap;

  const _JudgeButton({
    required this.text, required this.selected,
    required this.showResult, required this.correct, required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    Color? bg;
    if (showResult && correct) bg = Colors.green.shade50;
    else if (showResult && selected && !correct) bg = Colors.red.shade50;

    return SizedBox(
      height: 80,
      child: Card(
        color: bg ?? (selected ? Colors.blue.shade50 : null),
        child: InkWell(
          onTap: showResult ? null : onTap,
          child: Center(child: Text(text, style: const TextStyle(fontSize: 18))),
        ),
      ),
    );
  }
}
