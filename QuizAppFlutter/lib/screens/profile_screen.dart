import 'package:flutter/material.dart';
import '../db/database.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('我的')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Card(
              color: Colors.blue.shade50,
              child: const Padding(
                padding: EdgeInsets.all(24),
                child: Column(
                  children: [
                    Icon(Icons.bar_chart, size: 48, color: Colors.blue),
                    SizedBox(height: 8),
                    Text('学习统计', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                    SizedBox(height: 4),
                    Text('多刷题，多练习！', style: TextStyle(color: Colors.grey)),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            Card(
              child: ListTile(
                leading: const Icon(Icons.history),
                title: const Text('考试记录'),
                subtitle: const Text('暂无记录'),
              ),
            ),
            const SizedBox(height: 16),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(children: [
                      Icon(Icons.copyright, color: Colors.blue.shade700),
                      const SizedBox(width: 8),
                      const Text('著作权声明',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    ]),
                    const SizedBox(height: 8),
                    const Text('本软件著作权人：唐家俊'),
                    const SizedBox(height: 4),
                    const Text('本软件仅用于分享学习使用，不可用于任何商业行为。',
                      style: TextStyle(fontSize: 12)),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            Card(
              child: ListTile(
                title: const Text('关于'),
                subtitle: const Text('刷题助手 v1.0\n支持导入 txt/md/docx/xlsx 题库，多题库切换'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
