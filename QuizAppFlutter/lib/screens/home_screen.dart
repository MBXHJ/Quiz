import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/models.dart';
import '../providers/app_provider.dart';
import 'practice_screen.dart';
import 'import_screen.dart';
import 'profile_screen.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('刷题助手'),
        actions: [
          TextButton(
            onPressed: () => Navigator.push(context,
              MaterialPageRoute(builder: (_) => const ProfileScreen())),
            child: const Text('我的'),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => Navigator.push(context,
          MaterialPageRoute(builder: (_) => const ImportScreen())),
        child: const Icon(Icons.add),
      ),
      body: Consumer<AppProvider>(
        builder: (context, provider, _) {
          if (provider.banks.isEmpty) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.school, size: 80, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('还没有题库', style: TextStyle(fontSize: 22, color: Colors.grey)),
                  SizedBox(height: 8),
                  Text('点击右下角 + 按钮导入题库文件\n支持 .txt/.md/.docx/.xlsx 格式',
                    textAlign: TextAlign.center,
                    style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }

          return ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: provider.banks.length,
            itemBuilder: (context, index) {
              final bank = provider.banks[index];
              return Card(
                margin: const EdgeInsets.only(bottom: 12),
                child: ListTile(
                  leading: const Icon(Icons.school, size: 40, color: Colors.blue),
                  title: Text(bank.name, style: const TextStyle(fontWeight: FontWeight.bold)),
                  subtitle: Text('${bank.questionCount} 道题'),
                  trailing: IconButton(
                    icon: const Icon(Icons.delete, color: Colors.red),
                    onPressed: () {
                      showDialog(
                        context: context,
                        builder: (ctx) => AlertDialog(
                          title: const Text('删除题库'),
                          content: Text('确定要删除「${bank.name}」吗？'),
                          actions: [
                            TextButton(onPressed: () => Navigator.pop(ctx),
                              child: const Text('取消')),
                            TextButton(onPressed: () {
                              AppDatabase().deleteBank(bank.id!).then((_) {
                                provider.loadBanks();
                                Navigator.pop(ctx);
                              });
                            }, child: const Text('删除', style: TextStyle(color: Colors.red))),
                          ],
                        ),
                      );
                    },
                  ),
                  onTap: () => Navigator.push(context,
                    MaterialPageRoute(builder: (_) => PracticeScreen(bankId: bank.id!))),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
