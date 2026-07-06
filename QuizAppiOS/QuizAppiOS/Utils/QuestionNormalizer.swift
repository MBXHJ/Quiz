import Foundation

/// 规范化题目数据：统一答案格式、选项格式、题目类型判定。
struct QuestionNormalizer {

    static func normalize(_ question: Question) -> Question {
        var content = question.content.trimmingCharacters(in: .whitespaces)
        var options = question.options
        var answer = question.answer.trimmingCharacters(in: .whitespaces)
        var type = question.questionType

        // ── 1. 标准化选项格式为 "A. xxx" ──
        options = options.enumerated().map { (idx, opt) in
            let letter = String(UnicodeScalar(65 + idx)!) // A, B, C...
            let trimmed = opt.trimmingCharacters(in: .whitespaces)
            if let m = try? NSRegularExpression(pattern: #"^([A-Za-z])[.、．):]\s*(.*)"#)
                .firstMatch(in: trimmed, range: NSRange(location: 0, length: (trimmed as NSString).length)) {
                let l = (trimmed as NSString).substring(with: m.range(at: 1)).uppercased()
                let t = (trimmed as NSString).substring(with: m.range(at: 2)).trimmingCharacters(in: .whitespaces)
                return "\(l). \(t)"
            }
            return "\(letter). \(trimmed)"
        }

        // ── 2. 去除题目末尾嵌入的答案标记 ──
        if let ej = try? NSRegularExpression(pattern: #"\s*[（(]\s*(对|错|正确|错误)\s*[）)]\s*$"#)
            .firstMatch(in: content, range: NSRange(location: 0, length: (content as NSString).length)),
           answer.isEmpty {
            let ans = (content as NSString).substring(with: ej.range(at: 1))
            answer = (ans == "对" || ans == "正确") ? "正确" : "错误"
            content = ((content as NSString).replacingCharacters(in: ej.range, with: "")).trimmingCharacters(in: .whitespaces)
        } else if let el = try? NSRegularExpression(pattern: #"\s*[（(]\s*([A-Za-z])\s*[）)]\s*$"#)
            .firstMatch(in: content, range: NSRange(location: 0, length: (content as NSString).length)),
                  answer.isEmpty {
            answer = (content as NSString).substring(with: el.range(at: 1)).uppercased()
            content = ((content as NSString).replacingCharacters(in: el.range, with: "")).trimmingCharacters(in: .whitespaces)
        }

        // ── 3. 统一答案格式 ──
        answer = normalizeAnswer(answer)

        // ── 4. 重新判定题目类型 ──
        type = detectType(content: content, options: options, answer: answer, initialType: type)

        // ── 5. 判断题补充默认选项 ──
        if type == .judge && options.isEmpty {
            options = ["A. 正确", "B. 错误"]
        }

        // ── 6. 多选题答案排序 ──
        if type == .multi && answer.allSatisfy({ $0.isASCII && $0.isLetter }) {
            answer = String(answer.uppercased().sorted())
        }

        return Question(
            bankId: question.bankId,
            questionType: type,
            content: content,
            options: options,
            answer: answer,
            analysis: question.analysis.trimmingCharacters(in: .whitespaces)
        )
    }

    /// 规范化答案字符串
    static func normalizeAnswer(_ raw: String) -> String {
        var s = raw.trimmingCharacters(in: .whitespaces)
            .trimmingCharacters(in: CharacterSet(charactersIn: "\"'""""'""''""''"""))
            .trimmingCharacters(in: CharacterSet(charactersIn: "。.，,、；;：: "))

        if s.isEmpty { return "" }

        // 判断题归一
        if s == "对" { return "正确" }
        if s == "错" { return "错误" }
        if s == "正确" || s == "错误" { return s }

        // 单字母答案
        if s.count == 1, let c = s.uppercased().first, c.isASCII, c.isLetter { return String(c) }

        // 多字母连写 "ACD"
        if s.allSatisfy({ $0.isASCII && $0.isLetter }) { return String(s.uppercased().sorted()) }

        // 逗号/空格/顿号分隔 "A, C, D"
        let parts = s.split { $0.isWhitespace || "，、,;；".contains($0) }
            .map { $0.trimmingCharacters(in: .whitespaces).uppercased() }
            .filter { $0.count == 1 && $0.first!.isASCII && $0.first!.isLetter }
        if parts.count >= 2 { return parts.joined() }

        // 单个字母带标点 "A." → "A"
        if let m = try? NSRegularExpression(pattern: #"^([A-Za-z])[.、．)]?$"#)
            .firstMatch(in: s, range: NSRange(location: 0, length: (s as NSString).length)) {
            return (s as NSString).substring(with: m.range(at: 1)).uppercased()
        }

        return s
    }

    /// 根据题目内容、选项、答案重新判定题目类型
    static func detectType(content: String, options: [String], answer: String, initialType: QuestionType) -> QuestionType {
        let hasBlank = ["____", "___", "（）", "( )", "（  ）", "_____"].contains { content.contains($0) }
        let isTextAnswer = !answer.isEmpty && answer != "正确" && answer != "错误" &&
            !answer.allSatisfy({ $0.isASCII && $0.isLetter })

        // FILL 填空题
        if options.isEmpty && isTextAnswer && hasBlank { return .fill }

        // JUDGE 判断题
        if options.count == 2 {
            let optTexts = options.map { $0.components(separatedBy: ". ").last?.trimmingCharacters(in: .whitespaces) ?? "" }
            if optTexts.contains("正确") && optTexts.contains("错误") { return .judge }
            if optTexts.contains("对") && optTexts.contains("错") { return .judge }
        }
        if (answer == "正确" || answer == "错误") && options.isEmpty { return .judge }

        // MULTI 多选题
        if answer.count > 1 && answer.allSatisfy({ $0.isASCII && $0.isLetter }) && answer != "正确" && answer != "错误" {
            return .multi
        }

        return initialType
    }
}
