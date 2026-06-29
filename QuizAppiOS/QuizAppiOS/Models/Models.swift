import Foundation

// MARK: - QuestionBank
struct QuestionBank: Identifiable, Codable, Equatable {
    var id: Int64?
    var name: String
    var description: String
    var questionCount: Int
    var importDate: Date

    init(id: Int64? = nil, name: String, description: String = "",
         questionCount: Int = 0, importDate: Date = Date()) {
        self.id = id
        self.name = name
        self.description = description
        self.questionCount = questionCount
        self.importDate = importDate
    }
}

// MARK: - QuestionType
enum QuestionType: String, Codable, CaseIterable {
    case single
    case multi
    case judge

    var label: String {
        switch self {
        case .single: return "单选题"
        case .multi: return "多选题"
        case .judge: return "判断题"
        }
    }
}

// MARK: - Question
struct Question: Identifiable, Codable, Equatable {
    var id: Int64?
    var bankId: Int64
    var questionType: QuestionType
    var content: String
    var options: [String]
    var answer: String
    var analysis: String

    init(id: Int64? = nil, bankId: Int64, questionType: QuestionType,
         content: String, options: [String] = [],
         answer: String = "", analysis: String = "") {
        self.id = id
        self.bankId = bankId
        self.questionType = questionType
        self.content = content
        self.options = options
        self.answer = answer
        self.analysis = analysis
    }
}

// MARK: - WrongRecord
struct WrongRecord: Identifiable, Codable, Equatable {
    var id: Int64?
    var questionId: Int64
    var wrongCount: Int
    var lastWrongTime: Date
    var isRemoved: Bool

    init(id: Int64? = nil, questionId: Int64, wrongCount: Int = 1,
         lastWrongTime: Date = Date(), isRemoved: Bool = false) {
        self.id = id
        self.questionId = questionId
        self.wrongCount = wrongCount
        self.lastWrongTime = lastWrongTime
        self.isRemoved = isRemoved
    }
}

// MARK: - ExamRecord
struct ExamRecord: Identifiable, Codable, Equatable {
    var id: Int64?
    var bankId: Int64
    var score: Int
    var totalCount: Int
    var correctCount: Int
    var questionDetails: String   // JSON: [{"questionId":1,"userAnswer":"A","isCorrect":true},...]
    var examDate: Date

    init(id: Int64? = nil, bankId: Int64, score: Int, totalCount: Int,
         correctCount: Int, questionDetails: String = "", examDate: Date = Date()) {
        self.id = id
        self.bankId = bankId
        self.score = score
        self.totalCount = totalCount
        self.correctCount = correctCount
        self.questionDetails = questionDetails
        self.examDate = examDate
    }
}

// MARK: - PracticeRecord
struct PracticeRecord: Identifiable, Codable, Equatable {
    var id: Int64?
    var bankId: Int64
    var mode: String           // "sequential", "random", "type", "wrong"
    var totalCount: Int
    var correctCount: Int
    var wrongCount: Int
    var duration: TimeInterval // seconds
    var practiceDate: Date

    init(id: Int64? = nil, bankId: Int64, mode: String,
         totalCount: Int, correctCount: Int, wrongCount: Int,
         duration: TimeInterval = 0, practiceDate: Date = Date()) {
        self.id = id
        self.bankId = bankId
        self.mode = mode
        self.totalCount = totalCount
        self.correctCount = correctCount
        self.wrongCount = wrongCount
        self.duration = duration
        self.practiceDate = practiceDate
    }
}

// MARK: - PracticeProgress
struct PracticeProgress: Identifiable, Codable, Equatable {
    var id: Int64?
    var bankId: Int64
    var mode: String
    var currentIndex: Int
    var answeredIds: [Int64]    // JSON array
    var wrongIds: [Int64]       // JSON array
    var updatedAt: Date

    init(id: Int64? = nil, bankId: Int64, mode: String,
         currentIndex: Int = 0, answeredIds: [Int64] = [],
         wrongIds: [Int64] = [], updatedAt: Date = Date()) {
        self.id = id
        self.bankId = bankId
        self.mode = mode
        self.currentIndex = currentIndex
        self.answeredIds = answeredIds
        self.wrongIds = wrongIds
        self.updatedAt = updatedAt
    }
}

// MARK: - FavoriteQuestion
struct FavoriteQuestion: Identifiable, Codable, Equatable {
    var id: Int64?
    var questionId: Int64
    var createdAt: Date

    init(id: Int64? = nil, questionId: Int64, createdAt: Date = Date()) {
        self.id = id
        self.questionId = questionId
        self.createdAt = createdAt
    }
}

// MARK: - MarkedQuestion
struct MarkedQuestion: Identifiable, Codable, Equatable {
    var id: Int64?
    var questionId: Int64
    var createdAt: Date

    init(id: Int64? = nil, questionId: Int64, createdAt: Date = Date()) {
        self.id = id
        self.questionId = questionId
        self.createdAt = createdAt
    }
}

// MARK: - QuestionNote
struct QuestionNote: Identifiable, Codable, Equatable {
    var id: Int64?
    var questionId: Int64
    var content: String
    var createdAt: Date
    var updatedAt: Date

    init(id: Int64? = nil, questionId: Int64, content: String,
         createdAt: Date = Date(), updatedAt: Date = Date()) {
        self.id = id
        self.questionId = questionId
        self.content = content
        self.createdAt = createdAt
        self.updatedAt = updatedAt
    }
}

// MARK: - AnsweredQuestion
struct AnsweredQuestion: Identifiable, Codable, Equatable {
    var id: Int64?
    var questionId: Int64
    var practiceRecordId: Int64
    var userAnswer: String
    var isCorrect: Bool

    init(id: Int64? = nil, questionId: Int64, practiceRecordId: Int64,
         userAnswer: String, isCorrect: Bool) {
        self.id = id
        self.questionId = questionId
        self.practiceRecordId = practiceRecordId
        self.userAnswer = userAnswer
        self.isCorrect = isCorrect
    }
}

// MARK: - ExamDetail (helper model, not persisted separately)
struct ExamDetail: Codable, Equatable {
    var questionId: Int64
    var userAnswer: String
    var isCorrect: Bool
}

// MARK: - PracticeMode
enum PracticeMode: String, CaseIterable {
    case sequential
    case random
    case type
    case wrong
    case favorite

    var label: String {
        switch self {
        case .sequential: return "顺序练习"
        case .random: return "随机刷题"
        case .type: return "题型分类"
        case .wrong: return "错题重做"
        case .favorite: return "收藏练习"
        }
    }
}

// MARK: - ThemeMode
enum ThemeMode: String, CaseIterable {
    case system
    case light
    case dark

    var label: String {
        switch self {
        case .system: return "跟随系统"
        case .light: return "浅色模式"
        case .dark: return "深色模式"
        }
    }
}

// MARK: - AppSettings
struct AppSettings: Codable, Equatable {
    var themeMode: ThemeMode = .system
    var fontSize: Double = 1.0         // 0.8 ~ 1.4
    var reminderEnabled: Bool = false
    var reminderTime: Date = Calendar.current.date(from: DateComponents(hour: 20, minute: 0)) ?? Date()
}
