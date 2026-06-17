import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import '../db/database.dart';
import '../models/models.dart';
import '../parsers/txt_parser.dart';

class ImportScreen extends StatefulWidget {
  const ImportScreen({super.key});

  @override
  State<ImportScreen> createState() => _ImportScreenState();
}

class _ImportScreenState extends State<ImportScreen> {
  final _nameController = TextEditingController();
  bool _importing = false;
  String? _error;

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  Future<void> _pickFile() async {
    if (_nameController.text.trim().isEmpty) {
      setState(() => _error = '请输入题库名称');
      return;
    }

    final result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['txt', 'md', 'docx', 'xlsx', 'xls'],
    );

    if (result == null) return;

    setState(() {
      _importing = true;
      _error = null;
    });

    try {
      final db = AppDatabase();
      final bank = QuestionBank(name: _nameController.text.trim());
      final bankId = await db.insertBank(bank);

      final bytes = result.files.single.bytes!;
      final fileName = result.files.single.name;
      List<Question> questions;

      if (fileName.endsWith('.txt') || fileName.endsWith('.md')) {
        final content = String.fromCharCodes(bytes);
        questions = TxtParser().parse(content, bankId);
      } else {
        final content = String.fromCharCodes(bytes);
        questions = TxtParser().parse(content, bankId);
      }

      await db.insertQuestions(questions);

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('成功导入 ${questions.length} 道题！')),
      );
      Navigator.pop(context, true);
    } catch (e) {
      setState(() => _error = '导入失败: $e');
    } finally {
      setState(() => _importing = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('导入题库')),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          children: [
            const SizedBox(height: 32),
            const Text('导入题库', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Text('支持 .txt/.md/.docx/.xlsx 格式的题库文件',
              style: TextStyle(color: Colors.grey[600])),
            const SizedBox(height: 32),
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(
                labelText: '题库名称',
                hintText: '如：人工智能训练师、考研政治',
                border: OutlineInputBorder(),
              ),
              enabled: !_importing,
            ),
            const SizedBox(height: 24),
            if (_importing)
              const Column(
                children: [
                  LinearProgressIndicator(),
                  SizedBox(height: 16),
                  Text('正在解析题库...'),
                ],
              )
            else
              SizedBox(
                width: double.infinity,
                height: 56,
                child: ElevatedButton(
                  onPressed: _pickFile,
                  child: const Text('选择文件并导入', style: TextStyle(fontSize: 16)),
                ),
              ),
            if (_error != null) ...[
              const SizedBox(height: 16),
              Text(_error!, style: const TextStyle(color: Colors.red)),
            ],
          ],
        ),
      ),
    );
  }
}
