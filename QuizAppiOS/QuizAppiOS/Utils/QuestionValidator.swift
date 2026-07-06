import Foundation

/// 校验题目数据的完整性、一致性和格式正确性。
struct QuestionValidator {

    struct InvalidQuestion {
        let index: Int
        let content: String
        let errors: [String]
    }

    struct ValidationResult {
        let valid: [Question]
        let invalid: [InvalidQuestion]
    }

    /// 校验单道题，返回错误列表（空 = 有效）。
    static func validate(_ question: Question) -> [String] {
        var errors: [String] = []

        if question.content.trimmingCharacters(in: .whitespaces).isEmpty {
            errors.append("题目内容为空")
        }

        if ![QuestionType.single, .multi, .judge, .fill].contains(question.questionType) {
            errors.append("未知题目类型: \(question.questionType.rawValue)")
        }

        if question.answer.trimmingCharacters(in: .whitespaces).isEmpty {
            errors.append("答案为空")
        }

        switch question.questionType {
        case .single: validateSingle(question, &errors)
        case .multi: validateMulti(question, &errors)
        case .judge: validateJudge(question, &errors)
        case .fill: validateFill(question, &errors)
        }

        return errors
    }

    /// 批量校验
    static func validateAll(_ questions: [Question]) -> ValidationResult {
        var valid: [Question] = []
        var invalid: [InvalidQuestion] = []

        for (i, q) in questions.enumerated() {
            let errors = validate(q)
            if errors.isEmpty {
                valid.append(q)
            } else {
                invalid.append(InvalidQuestion(
                    index: i,
                    content: String(q.content.prefix(60)),
                    errors: errors
                ))
            }
        }

        return ValidationResult(valid: valid, invalid: invalid)
    }

    private static func validateSingle(_ q: Question, _ errors: inout [String]) {
        if q.options.isEmpty { errors.append("单选题缺少选项") }
        if q.options.count < 2 { errors.append("单选题至少需要2个选项，当前\(q.options.count)个") }
        if !q.answer.isEmpty && !(q.answer.count == 1 && q.answer.first?.isASCII == true && q.answer.first?.isLetter == true) {
            errors.append("单选题答案应为单个字母(A-Z)，当前: \(q.answer)")
        }
        if q.answer.count == 1, let c = q.answer.uppercased().first {
            let idx = Int(c.asciiValue! - Character("A").asciiValue!)
            if idx >= q.options.count {
                errors.append("答案\(q.answer)超出选项范围(共\(q.options.count)个选项)")
            }
        }
    }

    private static func validateMulti(_ q: Question, _ errors: inout [String]) {
        if q.options.isEmpty { errors.append("多选题缺少选项") }
        if q.options.count < 2 { errors.append("多选题至少需要2个选项，当前\(q.options.count)个") }
        if !q.answer.isEmpty && !q.answer.allSatisfy({ $0.isASCII && $0.isLetter }) {
            errors.append("多选题答案应为字母组合(A-Z)，当前: \(q.answer)")
        }
        for c in q.answer.uppercased() {
            let idx = Int(c.asciiValue! - Character("A").asciiValue!)
            if idx >= q.options.count {
                errors.append("答案\(c)超出选项范围(共\(q.options.count)个选项)")
            }
        }
    }

    private static func validateJudge(_ q: Question, _ errors: inout [String]) {
        if !q.answer.isEmpty && q.answer != "正确" && q.answer != "错误" {
            errors.append("判断题答案应为'正确'或'错误'，当前: \(q.answer)")
        }
        if !q.options.isEmpty && q.options.count != 2 {
            errors.append("判断题选项数量应为2，当前\(q.options.count)个")
        }
        if q.options.count == 2 {
            let optTexts = q.options.map { $0.components(separatedBy: ". ").last?.trimmingCharacters(in: .whitespaces) ?? "" }
            if !optTexts.contains("正确") || !optTexts.contains("错误") {
                errors.append("判断题选项应为'正确'和'错误'，当前: \(optTexts)")
            }
        }
    }

    private static func validateFill(_ q: Question, _ errors: inout [String]) {
        if !q.options.isEmpty { errors.append("填空题不应包含选项") }
        if !q.answer.isEmpty && q.answer.count == 1,
           let c = q.answer.first, c.isASCII, c.isLetter {
            errors.append("填空题答案不应为选项字母，当前: \(q.answer)")
        }
        let hasBlank = ["____", "___", "（）", "( )", "_____"].contains { q.content.contains($0) }
        if !hasBlank { errors.append("填空题内容中缺少空白标记(____或（）)") }
    }
}
