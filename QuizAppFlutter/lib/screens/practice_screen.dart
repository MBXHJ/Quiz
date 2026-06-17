import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../db/database.dart';
import '../models/models.dart';
import '../providers/app_provider.dart';
import 'question_screen.dart';
import 'exam_screen.dart';

class PracticeScreen extends StatelessWidget {
  final int bankId;

  const PracticeScreen({super.key, required this.bankId});

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<AppProvider>();

    return Scaffold(
      appBar: AppBar(title: const Text('刷题')),
      body: FutureBuilder(
        future: Future.wait([
          AppDatabase().getQuestionCount(bankId),
          AppDatabase().getWrongWithQuestion(bankId),
        ]),
        builder: (context, snapshot) {
          if (!snapshot.hasData) {
            return const Center(child: CircularProgressIndicator());
          }
          final totalCount = snapshot.data![0] as int;
          final wrongCount = (snapshot.data![1] as List).length;

          return Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Card(
                  color: Colors.blue.shade50,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        _StatItem('总题数', '$totalCount'),
                        _StatItem('错题数', '$wrongCount'),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                const Text('选择刷题模式',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                const SizedBox(height: 8),
                _ModeCard(
                  icon: Icons.list,
                  title: '顺序练习',
                  desc: '按顺序逐题练习，查看答案和解析',
                  onTap: () {
                    provider.loadQuestions(bankId);
                    Navigator.push(context, MaterialPageRoute(
                      builder: (_) => const QuestionScreen()));
                  },
                ),
                _ModeCard(
                  icon: Icons.shuffle,
                  title: '模拟考试',
                  desc: '随机抽题，完成后评分',
                  onTap: () {
                    provider.initExam(bankId);
                    Navigator.push(context, MaterialPageRoute(
                      builder: (_) => const ExamScreen()));
                  },
                ),
                _ModeCard(
                  icon: Icons.category,
                  title: '题型分类',
                  desc: '按单选/多选/判断分类练习',
                  onTap: () => _showTypeDialog(context, bankId),
                ),
                if (wrongCount > 0)
                  _ModeCard(
                    icon: Icons.error_outline,
                    title: '错题重做',
                    desc: '共 $wrongCount 道错题，针对性巩固',
                    onTap: () {
                      provider.loadWrongQuestions(bankId);
                      Navigator.push(context, MaterialPageRoute(
                        builder: (_) => const QuestionScreen()));
                    },
                  ),
              ],
            ),
          );
        },
      ),
    );
  }

  void _showTypeDialog(BuildContext context, int bankId) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('选择题型'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              title: const Text('单选题'),
              onTap: () {
                Navigator.pop(ctx);
                context.read<AppProvider>().loadQuestions(bankId, type: 'single');
                Navigator.push(context, MaterialPageRoute(
                  builder: (_) => const QuestionScreen()));
              },
            ),
            ListTile(
              title: const Text('多选题'),
              onTap: () {
                Navigator.pop(ctx);
                context.read<AppProvider>().loadQuestions(bankId, type: 'multi');
                Navigator.push(context, MaterialPageRoute(
                  builder: (_) => const QuestionScreen()));
              },
            ),
            ListTile(
              title: const Text('判断题'),
              onTap: () {
                Navigator.pop(ctx);
                context.read<AppProvider>().loadQuestions(bankId, type: 'judge');
                Navigator.push(context, MaterialPageRoute(
                  builder: (_) => const QuestionScreen()));
              },
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('取消')),
        ],
      ),
    );
  }
}

class _StatItem extends StatelessWidget {
  final String label, value;
  const _StatItem(this.label, this.value);

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      Text(value, style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
      Text(label, style: const TextStyle(fontSize: 12)),
    ]);
  }
}

class _ModeCard extends StatelessWidget {
  final IconData icon;
  final String title, desc;
  final VoidCallback onTap;

  const _ModeCard({
    required this.icon, required this.title,
    required this.desc, required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 4),
      child: ListTile(
        leading: Icon(icon, size: 36, color: Colors.blue),
        title: Text(title, style: const TextStyle(fontWeight: FontWeight.bold)),
        subtitle: Text(desc, style: const TextStyle(fontSize: 12)),
        onTap: onTap,
      ),
    );
  }
}
