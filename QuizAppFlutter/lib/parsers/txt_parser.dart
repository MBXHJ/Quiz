import '../models/models.dart';

class TxtParser {
  List<Question> parse(String content, int bankId) {
    final lines = content.split('\n')
      .map((l) => l.trim())
      .where((l) => l.isNotEmpty)
      .toList();

    final questions = <Question>[];
    int i = 0;

    while (i < lines.length) {
      final match = RegExp(r'^(?:#+\s*)?(\d+)[.、．,，]\s*(.*)').firstMatch(lines[i]);
      if (match != null) {
        final qContent = match.group(2)!.trim();
        final options = <String>[];
        i++;

        while (i < lines.length) {
          final optMatch = RegExp(r'^[•·\-*\s]*([A-Za-z])[.、．)]\s*(.*)').firstMatch(lines[i]);
          if (optMatch != null) {
            final text = optMatch.group(2)!.trim();
            if (RegExp(r'^\d+[.、]').hasMatch(text)) break;
            options.add('${optMatch.group(1)}. $text');
            i++;
          } else {
            break;
          }
        }

        String answer = '';
        String analysis = '';
        while (i < lines.length) {
          if (lines[i].startsWith('答案') || lines[i].contains('**答案**')) {
            answer = lines[i].replaceAll(RegExp(r'(答案|[\*\__]答案[\*\__])[:：]?\s*'), '').trim();
            i++;
          } else if (lines[i].startsWith('解析') || lines[i].contains('**解析**')) {
            analysis = lines[i].replaceAll(RegExp(r'(解析|[\*\__]解析[\*\__])[:：]?\s*'), '').trim();
            i++;
          } else {
            break;
          }
        }

        var type = QuestionType.single;
        if (options.isEmpty) type = QuestionType.judge;
        if (answer.length > 1 && !answer.contains('正确') && !answer.contains('错误')) {
          type = QuestionType.multi;
        }

        questions.add(Question(
          bankId: bankId,
          questionType: type,
          content: qContent,
          options: options,
          answer: answer,
          analysis: analysis,
        ));
      } else {
        i++;
      }
    }

    return questions;
  }
}
