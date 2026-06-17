import 'dart:html' show FileReader;
import 'package:file_picker/file_picker.dart';
import '../models/models.dart';
import 'txt_parser.dart';

class ImportService {
  final bankId;

  ImportService(this.bankId);

  Future<List<Question>> parseFile(PlatformFile file, int bankId) async {
    final name = file.name.toLowerCase();

    if (name.endsWith('.txt') || name.endsWith('.md')) {
      final content = String.fromCharCodes(file.bytes!);
      return TxtParser().parse(content, bankId);
    }

    // For .docx and .xlsx on mobile, we rely on the bytes
    if (name.endsWith('.docx')) {
      final content = String.fromCharCodes(file.bytes!);
      return TxtParser().parse(content, bankId);
    }

    throw Exception('不支持的文件格式: ${file.name}');
  }

  static Future<List<Question>> parseFromBytes(
    String fileName, List<int> bytes, int bankId) async {
    if (fileName.endsWith('.txt') || fileName.endsWith('.md')) {
      final content = String.fromCharCodes(bytes);
      return TxtParser().parse(content, bankId);
    }
    throw Exception('不支持的文件格式: $fileName');
  }
}
