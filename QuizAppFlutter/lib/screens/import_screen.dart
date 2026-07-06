import 'dart:io';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import '../db/database.dart';
import '../models/models.dart';
import '../parsers/import_service.dart';

class ImportScreen extends StatefulWidget {
  const ImportScreen({super.key});

  @override
  State<ImportScreen> createState() => _ImportScreenState();
}

class _ImportScreenState extends State<ImportScreen> {
  final _nameController = TextEditingController();
  bool _importing = false;
  String? _error;

  // Import results
  int _successCount = 0;
  int _failCount = 0;
  List<InvalidQuestion> _invalidQuestions = [];
  bool _isComplete = false;

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
      _isComplete = false;
    });

    try {
      final db = AppDatabase();
      final bank = QuestionBank(name: _nameController.text.trim());
      final bankId = await db.insertBank(bank);

      final bytes = result.files.single.bytes!;
      final fileName = result.files.single.name;

      List<Question> questions;
      List<InvalidQuestion> invalidQuestions;

      final importResult = await ImportService.parseFromBytes(fileName, bytes, bankId);
      questions = importResult.questions;
      invalidQuestions = importResult.invalidQuestions;

      await db.insertQuestions(questions);

      setState(() {
        _successCount = questions.length;
        _failCount = invalidQuestions.length;
        _invalidQuestions = invalidQuestions;
        _isComplete = true;
        _importing = false;
      });
    } catch (e) {
      setState(() {
        _error = '导入失败: $e';
        _importing = false;
      });
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
            else if (_isComplete)
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    children: [
                      const Icon(Icons.check_circle, color: Colors.green, size: 48),
                      const SizedBox(height: 12),
                      const Text('导入成功！',
                          style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.green)),
                      const SizedBox(height: 8),
                      Text('成功导入 $_successCount 道题',
                          style: const TextStyle(fontSize: 16)),
                      if (_failCount > 0) ...[
                        const SizedBox(height: 4),
                        Text('$_failCount 道题校验未通过，已跳过',
                            style: const TextStyle(color: Colors.red, fontSize: 13)),
                      ],
                      if (_invalidQuestions.isNotEmpty) ...[
                        const SizedBox(height: 12),
                        Container(
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Colors.red.shade50,
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text('校验详情（前${_invalidQuestions.take(10).length}条）：',
                                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
                              ..._invalidQuestions.take(10).map((iq) => Padding(
                                padding: const EdgeInsets.only(top: 4),
                                child: Text(
                                  '第${iq.index + 1}题: ${iq.errors.join("; ")}',
                                  style: const TextStyle(fontSize: 12, color: Colors.black87),
                                ),
                              )),
                            ],
                          ),
                        ),
                      ],
                    ],
                  ),
                ),
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
