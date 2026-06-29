import Foundation

/// Central answer normalization utilities.
/// Mirrors the Android AnswerUtils.kt logic.
struct AnswerUtils {

    /// Normalize an answer string for comparison.
    /// - Sorts multi-character answers alphabetically
    /// - Removes whitespace, commas, and other separators
    /// - Handles common Chinese answer formats
    static func normalizeAnswer(_ raw: String) -> String {
        var answer = raw.trimmingCharacters(in: .whitespaces)

        // Map Chinese judge answers to standard form
        let judgeMap: [String: String] = [
            "正确": "正确", "对": "正确", "√": "正确", "✓": "正确",
            "A": "正确", "B": "错误",
            "错误": "错误", "错": "错误", "×": "错误", "✗": "错误",
        ]

        if let mapped = judgeMap[answer] {
            return mapped
        }

        // Remove common separators
        let separators = CharacterSet(charactersIn: ",，、;；\t ")
        let parts = answer.components(separatedBy: separators)
            .map { $0.trimmingCharacters(in: .whitespaces).uppercased() }
            .filter { !$0.isEmpty }

        if parts.count > 1 {
            return parts.sorted().joined()
        }

        return answer.uppercased()
    }

    /// Compare user answer with correct answer
    static func isAnswerCorrect(userAnswer: String, correctAnswer: String) -> Bool {
        normalizeAnswer(userAnswer) == normalizeAnswer(correctAnswer)
    }
}
