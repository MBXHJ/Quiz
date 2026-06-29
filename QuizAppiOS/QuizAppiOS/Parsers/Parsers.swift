import Foundation

// MARK: - Parser Protocol

protocol QuestionParser {
    func parse(content: String, bankId: Int64) throws -> [Question]
    func canHandle(fileExtension: String) -> Bool
}

// MARK: - Txt/Md Parser

struct TxtParser: QuestionParser {
    func canHandle(fileExtension: String) -> Bool {
        ["txt", "md"].contains(fileExtension.lowercased())
    }

    func parse(content: String, bankId: Int64) throws -> [Question] {
        let lines = content.components(separatedBy: .newlines)
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty }

        var questions: [Question] = []
        var i = 0

        while i < lines.count {
            let line = lines[i]
            // Match question number: "1.", "1、", "1．", "# 1.", "## 1."
            let qPattern = try! NSRegularExpression(pattern: #"^(?:#+\s*)?(\d+)[.、．,，]\s*(.*)"#)
            let nsLine = line as NSString
            if let match = qPattern.firstMatch(in: line, range: NSRange(location: 0, length: nsLine.length)) {
                let qContent = nsLine.substring(with: match.range(at: 2)).trimmingCharacters(in: .whitespaces)
                var options: [String] = []
                i += 1

                // Parse options
                while i < lines.count {
                    let optLine = lines[i]
                    let optPattern = try! NSRegularExpression(pattern: #"^[•·\-*\s]*([A-Za-z])[.、．)]\s*(.*)"#)
                    if let optMatch = optPattern.firstMatch(in: optLine, range: NSRange(location: 0, length: (optLine as NSString).length)) {
                        let letter = (optLine as NSString).substring(with: optMatch.range(at: 1))
                        let text = (optLine as NSString).substring(with: optMatch.range(at: 2)).trimmingCharacters(in: .whitespaces)
                        // Skip if looks like a question number
                        if try! NSRegularExpression(pattern: #"^\d+[.、]"#).firstMatch(in: text, range: NSRange(location: 0, length: (text as NSString).length)) != nil {
                            break
                        }
                        options.append("\(letter). \(text)")
                        i += 1
                    } else {
                        break
                    }
                }

                // Parse answer and analysis
                var answer = ""
                var analysis = ""
                while i < lines.count {
                    let metaLine = lines[i]
                    if metaLine.hasPrefix("答案") || metaLine.contains("**答案**") {
                        answer = metaLine
                            .replacingOccurrences(of: #"(答案|\*\*答案\*\*)[:：]?\s*"#, with: "", options: .regularExpression)
                            .trimmingCharacters(in: .whitespaces)
                        i += 1
                    } else if metaLine.hasPrefix("解析") || metaLine.contains("**解析**") {
                        analysis = metaLine
                            .replacingOccurrences(of: #"(解析|\*\*解析\*\*)[:：]?\s*"#, with: "", options: .regularExpression)
                            .trimmingCharacters(in: .whitespaces)
                        i += 1
                    } else {
                        break
                    }
                }

                // Normalize answer
                answer = AnswerUtils.normalizeAnswer(answer)

                // Detect type
                var type: QuestionType = .single
                if options.isEmpty {
                    type = .judge
                } else if answer.count > 1 && answer != "正确" && answer != "错误" {
                    type = .multi
                }

                questions.append(Question(
                    bankId: bankId,
                    questionType: type,
                    content: qContent,
                    options: options,
                    answer: answer,
                    analysis: analysis
                ))
            } else {
                i += 1
            }
        }

        return questions
    }
}

// MARK: - JSON Parser

struct JsonParser: QuestionParser {
    func canHandle(fileExtension: String) -> Bool {
        fileExtension.lowercased() == "json"
    }

    func parse(content: String, bankId: Int64) throws -> [Question] {
        guard let data = content.data(using: .utf8) else {
            throw ParseError.invalidEncoding
        }

        let json = try JSONSerialization.jsonObject(with: data)

        guard let array = json as? [[String: Any]] else {
            // Try root object with "questions" key
            if let dict = json as? [String: Any], let questions = dict["questions"] as? [[String: Any]] {
                return try parseArray(questions, bankId: bankId)
            }
            throw ParseError.invalidFormat("Expected JSON array or { questions: [...] }")
        }

        return try parseArray(array, bankId: bankId)
    }

    private func parseArray(_ array: [[String: Any]], bankId: Int64) throws -> [Question] {
        var questions: [Question] = []

        for item in array {
            guard let content = item["content"] as? String ?? item["题目"] as? String else {
                continue
            }

            let typeStr = item["type"] as? String ?? item["题型"] as? String ?? "single"
            let type: QuestionType = {
                switch typeStr.lowercased() {
                case "multi", "multiple", "多选题": return .multi
                case "judge", "判断", "判断题": return .judge
                default: return .single
                }
            }()

            var options: [String] = []
            if let opts = item["options"] as? [String] {
                options = opts
            } else if let opts = item["选项"] as? [String] {
                options = opts
            } else if let optsStr = item["options"] as? String {
                options = optsStr.components(separatedBy: "\n").filter { !$0.isEmpty }
            }

            let answer = AnswerUtils.normalizeAnswer(
                item["answer"] as? String ?? item["答案"] as? String ?? ""
            )
            let analysis = item["analysis"] as? String ?? item["解析"] as? String ?? ""

            questions.append(Question(
                bankId: bankId,
                questionType: type,
                content: content,
                options: options,
                answer: answer,
                analysis: analysis
            ))
        }

        return questions
    }
}

// MARK: - Docx Parser

struct DocxParser: QuestionParser {
    func canHandle(fileExtension: String) -> Bool {
        fileExtension.lowercased() == "docx"
    }

    func parse(content: String, bankId: Int64) throws -> [Question] {
        // For simple cases, docx content is extracted as text and parsed like txt
        // In real implementation, use ZIPFoundation to extract word/document.xml
        // and parse the XML content
        return try TxtParser().parse(content: content, bankId: bankId)
    }

    /// Extract text from docx data using ZIPFoundation
    static func extractText(from data: Data) throws -> String {
        // This requires ZIPFoundation package
        // For now, return empty - actual implementation needs the dependency
        throw ParseError.unsupportedFormat("DOCX parsing requires ZIPFoundation dependency")
    }
}

// MARK: - Xlsx Parser

struct XlsxParser: QuestionParser {
    func canHandle(fileExtension: String) -> Bool {
        ["xlsx", "xls"].contains(fileExtension.lowercased())
    }

    func parse(content: String, bankId: Int64) throws -> [Question] {
        // Simple CSV-like parsing for xlsx content extracted as text
        let lines = content.components(separatedBy: .newlines)
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty }

        guard lines.count > 1 else { return [] }

        var questions: [Question] = []
        // Skip header row
        for line in lines.dropFirst() {
            let columns = line.components(separatedBy: "\t")
            guard columns.count >= 4 else { continue }

            let typeStr = columns[0].trimmingCharacters(in: .whitespaces)
            let qContent = columns[1].trimmingCharacters(in: .whitespaces)
            let optionsStr = columns[2].trimmingCharacters(in: .whitespaces)
            let answer = AnswerUtils.normalizeAnswer(columns[3].trimmingCharacters(in: .whitespaces))
            let analysis = columns.count > 4 ? columns[4].trimmingCharacters(in: .whitespaces) : ""

            let type: QuestionType = {
                switch typeStr {
                case "多选题", "多选", "multi": return .multi
                case "判断题", "判断", "judge": return .judge
                default: return .single
                }
            }()

            let options = optionsStr.components(separatedBy: "\n")
                .map { $0.trimmingCharacters(in: .whitespaces) }
                .filter { !$0.isEmpty }

            questions.append(Question(
                bankId: bankId,
                questionType: type,
                content: qContent,
                options: options,
                answer: answer,
                analysis: analysis
            ))
        }

        return questions
    }
}

// MARK: - Parse Errors

enum ParseError: LocalizedError {
    case invalidEncoding
    case invalidFormat(String)
    case unsupportedFormat(String)

    var errorDescription: String? {
        switch self {
        case .invalidEncoding: return "文件编码无效"
        case .invalidFormat(let msg): return "格式无效: \(msg)"
        case .unsupportedFormat(let msg): return "不支持的格式: \(msg)"
        }
    }
}

// MARK: - Parser Registry

struct ParserRegistry {
    static let all: [QuestionParser] = [
        TxtParser(),
        JsonParser(),
        DocxParser(),
        XlsxParser(),
    ]

    static func parser(for fileExtension: String) -> QuestionParser? {
        all.first { $0.canHandle(fileExtension: fileExtension) }
    }

    static var supportedExtensions: [String] {
        ["txt", "md", "json", "docx", "xlsx", "xls"]
    }
}
