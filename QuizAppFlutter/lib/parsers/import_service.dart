import 'package:file_picker/file_picker.dart';
import '../models/models.dart';
import 'txt_parser.dart';
import 'question_normalizer.dart';
import 'question_validator.dart';

class ImportResult {
  final List<Question> questions;
  final List<InvalidQuestion> invalidQuestions;

  const ImportResult({required this.questions, required this.invalidQuestions});

  int get successCount => questions.length;
  int get failCount => invalidQuestions.length;
}

class ImportService {
  final int bankId;

  ImportService(this.bankId);

  Future<ImportResult> parseFile(PlatformFile file, int bankId) async {
    final name = file.name.toLowerCase();

    List<Question> parsed;
    if (name.endsWith('.txt') || name.endsWith('.md')) {
      final content = String.fromCharCodes(file.bytes!);
      parsed = TxtParser().parse(content, bankId);
    } else if (name.endsWith('.docx')) {
      final content = String.fromCharCodes(file.bytes!);
      parsed = TxtParser().parse(content, bankId);
    } else {
      throw Exception('不支持的文件格式: ${file.name}');
    }

    // ── 规范化 ──
    final normalized = parsed.map((q) => QuestionNormalizer.normalize(q)).toList();

    // ── 校验 ──
    final result = QuestionValidator.validateAll(normalized);

    return ImportResult(
      questions: result.valid,
      invalidQuestions: result.invalid,
    );
  }

  static Future<ImportResult> parseFromBytes(
    String fileName, List<int> bytes, int bankId) async {
    if (fileName.endsWith('.txt') || fileName.endsWith('.md')) {
      final content = String.fromCharCodes(bytes);
      final parsed = TxtParser().parse(content, bankId);
      final normalized = parsed.map((q) => QuestionNormalizer.normalize(q)).toList();
      final result = QuestionValidator.validateAll(normalized);
      return ImportResult(
        questions: result.valid,
        invalidQuestions: result.invalid,
      );
    }
    throw Exception('不支持的文件格式: $fileName');
  }
}
