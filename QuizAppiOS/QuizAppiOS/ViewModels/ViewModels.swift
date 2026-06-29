import Foundation
import SwiftUI
import Combine

// MARK: - HomeViewModel

@MainActor
final class HomeViewModel: ObservableObject {
    @Published var banks: [QuestionBank] = []
    @Published var isLoading = false

    private let bankDAO = QuestionBankDAO()
    private let questionDAO = QuestionDAO()

    func loadBanks() {
        isLoading = true
        defer { isLoading = false }
        do {
            banks = try bankDAO.getAll()
        } catch {
            print("Failed to load banks: \(error)")
        }
    }

    func deleteBank(_ bank: QuestionBank) {
        guard let id = bank.id else { return }
        do {
            try bankDAO.delete(id: id)
            loadBanks()
        } catch {
            print("Failed to delete bank: \(error)")
        }
    }

    func importBank(name: String, questions: [Question]) throws {
        var bank = QuestionBank(name: name)
        try bankDAO.insert(&bank)
        guard let bankId = bank.id else { throw ParseError.invalidFormat("Bank insert failed") }

        let qs = questions.map { q in
            Question(bankId: bankId, questionType: q.questionType,
                     content: q.content, options: q.options,
                     answer: q.answer, analysis: q.analysis)
        }
        try questionDAO.insertBatch(qs)
        try bankDAO.updateQuestionCount(bankId: bankId)
        loadBanks()
    }
}

// MARK: - PracticeViewModel

@MainActor
final class PracticeViewModel: ObservableObject {
    @Published var questions: [Question] = []
    @Published var currentIndex: Int = 0
    @Published var totalCount: Int = 0
    @Published var wrongCount: Int = 0
    @Published var isLoading = false
    @Published var isResuming = false
    @Published var hasProgress = false

    // Practice stats
    @Published var correctAnswers: Int = 0
    @Published var wrongAnswers: Int = 0
    @Published var startTime: Date?
    @Published var elapsedTime: TimeInterval = 0

    let bankId: Int64
    let mode: PracticeMode

    private let questionDAO = QuestionDAO()
    private let wrongDAO = WrongRecordDAO()
    private let progressDAO = PracticeProgressDAO()
    private let recordDAO = PracticeRecordDAO()
    private let answeredDAO = AnsweredQuestionDAO()

    private var answeredRecords: [AnsweredQuestion] = []
    private var timer: Timer?

    var currentQuestion: Question? {
        guard currentIndex < questions.count else { return nil }
        return questions[currentIndex]
    }

    var progress: Double {
        guard totalCount > 0 else { return 0 }
        return Double(currentIndex + 1) / Double(totalCount)
    }

    init(bankId: Int64, mode: PracticeMode) {
        self.bankId = bankId
        self.mode = mode
    }

    // MARK: - Load Questions

    func loadQuestions(type: QuestionType? = nil, randomCount: Int? = nil) {
        isLoading = true
        defer { isLoading = false }

        do {
            let qs: [Question]
            switch mode {
            case .sequential:
                if let t = type {
                    qs = try questionDAO.getByType(bankId, type: t)
                } else {
                    qs = try questionDAO.getByBank(bankId)
                }
            case .random:
                qs = try questionDAO.getRandom(bankId, limit: randomCount ?? 100)
            case .type:
                qs = try questionDAO.getByType(bankId, type: type ?? .single)
            case .wrong:
                let wrongQs = try wrongDAO.getWrongQuestions(bankId: bankId)
                qs = wrongQs.map { $0.1 }
            case .favorite:
                qs = try FavoriteQuestionDAO().getFavoriteQuestions(bankId: bankId)
            }

            questions = qs
            totalCount = qs.count
            currentIndex = 0
            correctAnswers = 0
            wrongAnswers = 0
            answeredRecords = []
            startTime = Date()

            // Check for saved progress
            if mode == .sequential || mode == .wrong || mode == .favorite {
                if let progress = try progressDAO.get(bankId: bankId, mode: mode.rawValue) {
                    if progress.currentIndex > 0 && progress.currentIndex < qs.count {
                        hasProgress = true
                        isResuming = false
                    }
                }
            }

            startTimer()
        } catch {
            print("Failed to load questions: \(error)")
        }
    }

    // MARK: - Resume Progress

    func resumeProgress() {
        do {
            guard let progress = try progressDAO.get(bankId: bankId, mode: mode.rawValue) else { return }
            currentIndex = progress.currentIndex
            isResuming = true
            hasProgress = false
            startTime = Date()
        } catch {
            print("Failed to resume: \(error)")
        }
    }

    func restartProgress() {
        currentIndex = 0
        correctAnswers = 0
        wrongAnswers = 0
        answeredRecords = []
        hasProgress = false
        isResuming = false
        startTime = Date()
        try? progressDAO.delete(bankId: bankId, mode: mode.rawValue)
    }

    // MARK: - Answer

    func submitAnswer(_ userAnswer: String) -> Bool {
        guard let q = currentQuestion, let qId = q.id else { return false }

        let isCorrect = AnswerUtils.isAnswerCorrect(userAnswer: userAnswer, correctAnswer: q.answer)

        if isCorrect {
            correctAnswers += 1
        } else {
            wrongAnswers += 1
            try? wrongDAO.upsertWrong(questionId: qId)
        }

        answeredRecords.append(AnsweredQuestion(
            questionId: qId,
            practiceRecordId: 0, // Will be set when saving record
            userAnswer: userAnswer,
            isCorrect: isCorrect
        ))

        return isCorrect
    }

    // MARK: - Navigation

    func goToNext() {
        guard currentIndex < questions.count - 1 else { return }
        currentIndex += 1
        saveProgress()
    }

    func goToPrevious() {
        guard currentIndex > 0 else { return }
        currentIndex -= 1
    }

    func goTo(index: Int) {
        guard index >= 0, index < questions.count else { return }
        currentIndex = index
    }

    // MARK: - Save

    func saveProgress() {
        var progress = PracticeProgress(bankId: bankId, mode: mode.rawValue)
        progress.currentIndex = currentIndex
        try? progressDAO.save(&progress)
    }

    func saveRecord() {
        stopTimer()
        var record = PracticeRecord(
            bankId: bankId,
            mode: mode.rawValue,
            totalCount: totalCount,
            correctCount: correctAnswers,
            wrongCount: wrongAnswers,
            duration: elapsedTime
        )
        do {
            try recordDAO.insert(&record)
            // Save answered questions
            if let recordId = record.id {
                let answers = answeredRecords.map { r in
                    AnsweredQuestion(
                        questionId: r.questionId,
                        practiceRecordId: recordId,
                        userAnswer: r.userAnswer,
                        isCorrect: r.isCorrect
                    )
                }
                try answeredDAO.insertBatch(answers)
            }
            // Clear progress
            try progressDAO.delete(bankId: bankId, mode: mode.rawValue)
        } catch {
            print("Failed to save record: \(error)")
        }
    }

    // MARK: - Timer

    private func startTimer() {
        stopTimer()
        startTime = Date()
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { [weak self] _ in
            Task { @MainActor in
                if let start = self?.startTime {
                    self?.elapsedTime = Date().timeIntervalSince(start)
                }
            }
        }
    }

    private func stopTimer() {
        timer?.invalidate()
        timer = nil
    }

    deinit {
        stopTimer()
    }
}

// MARK: - QuestionViewModel (single question display)

@MainActor
final class QuestionViewModel: ObservableObject {
    @Published var selectedAnswer: String? = nil
    @Published var multiSelected: Set<String> = []
    @Published var showResult: Bool = false
    @Published var isCorrect: Bool = false
    @Published var wrongCount: Int = 0
    @Published var isFavorite: Bool = false
    @Published var isMarked: Bool = false
    @Published var note: QuestionNote?

    let question: Question
    private let wrongDAO = WrongRecordDAO()
    private let favoriteDAO = FavoriteQuestionDAO()
    private let markedDAO = MarkedQuestionDAO()
    private let noteDAO = QuestionNoteDAO()

    init(question: Question) {
        self.question = question
        loadState()
    }

    private func loadState() {
        guard let qId = question.id else { return }
        Task {
            wrongCount = (try? wrongDAO.getWrongCount(questionId: qId)) ?? 0
            isFavorite = (try? favoriteDAO.isFavorite(questionId: qId)) ?? false
            isMarked = (try? markedDAO.isMarked(questionId: qId)) ?? false
            note = try? noteDAO.get(questionId: qId)
        }
    }

    func submitSingle(_ answer: String) {
        guard !showResult else { return }
        selectedAnswer = answer
        isCorrect = AnswerUtils.isAnswerCorrect(userAnswer: answer, correctAnswer: question.answer)
        showResult = true
        if !isCorrect, let qId = question.id {
            try? wrongDAO.upsertWrong(questionId: qId)
            wrongCount = (try? wrongDAO.getWrongCount(questionId: qId)) ?? 0
        }
    }

    func toggleMulti(_ answer: String) {
        guard !showResult else { return }
        if multiSelected.contains(answer) {
            multiSelected.remove(answer)
        } else {
            multiSelected.insert(answer)
        }
    }

    func confirmMulti() {
        guard !showResult else { return }
        let sorted = multiSelected.sorted().joined()
        selectedAnswer = sorted
        isCorrect = AnswerUtils.isAnswerCorrect(userAnswer: sorted, correctAnswer: question.answer)
        showResult = true
        if !isCorrect, let qId = question.id {
            try? wrongDAO.upsertWrong(questionId: qId)
            wrongCount = (try? wrongDAO.getWrongCount(questionId: qId)) ?? 0
        }
    }

    func toggleFavorite() {
        guard let qId = question.id else { return }
        if let result = try? favoriteDAO.toggle(questionId: qId) {
            isFavorite = result
        }
    }

    func toggleMarked() {
        guard let qId = question.id else { return }
        if let result = try? markedDAO.toggle(questionId: qId) {
            isMarked = result
        }
    }

    func saveNote(_ content: String) {
        guard let qId = question.id else { return }
        var n = note ?? QuestionNote(questionId: qId, content: content)
        n.content = content
        try? noteDAO.save(&n)
        note = n
    }
}

// MARK: - ExamViewModel

@MainActor
final class ExamViewModel: ObservableObject {
    @Published var questions: [Question] = []
    @Published var currentIndex: Int = 0
    @Published var totalCount: Int = 0
    @Published var examAnswers: [Int64: String] = [:]
    @Published var examMulti: [Int64: Set<String>] = [:]
    @Published var examFinished: Bool = false
    @Published var examScore: Int = 0
    @Published var correctCount: Int = 0
    @Published var isLoading = false
    @Published var showReview = false
    @Published var examRecordId: Int64? = nil

    let bankId: Int64

    private let questionDAO = QuestionDAO()
    private let examDAO = ExamRecordDAO()
    private let wrongDAO = WrongRecordDAO()

    var currentQuestion: Question? {
        guard currentIndex < questions.count else { return nil }
        return questions[currentIndex]
    }

    var answeredCount: Int {
        examAnswers.count + examMulti.count
    }

    var progress: Double {
        guard totalCount > 0 else { return 0 }
        return Double(currentIndex + 1) / Double(totalCount)
    }

    init(bankId: Int64) {
        self.bankId = bankId
    }

    func initExam() {
        isLoading = true
        defer { isLoading = false }

        do {
            // Exam: 70% single, 20% multi, 10% judge
            let typeCounts = try questionDAO.getByTypeCounts(bankId)
            let total = typeCounts.values.reduce(0, +)
            guard total > 0 else { return }

            let singleCount = min(typeCounts[.single] ?? 0, max(1, total * 7 / 10))
            let multiCount = min(typeCounts[.multi] ?? 0, max(0, total * 2 / 10))
            let judgeCount = min(typeCounts[.judge] ?? 0, max(0, total * 1 / 10))

            var examQs: [Question] = []
            if singleCount > 0 { examQs += (try? questionDAO.getRandom(bankId, limit: singleCount)) ?? [] }
            if multiCount > 0 {
                let multis = (try? questionDAO.getByType(bankId, type: .multi)) ?? []
                examQs += multis.shuffled().prefix(multiCount)
            }
            if judgeCount > 0 {
                let judges = (try? questionDAO.getByType(bankId, type: .judge)) ?? []
                examQs += judges.shuffled().prefix(judgeCount)
            }

            questions = examQs.shuffled()
            totalCount = questions.count
            currentIndex = 0
            examAnswers = [:]
            examMulti = [:]
            examFinished = false
            examScore = 0
            correctCount = 0
            showReview = false
        } catch {
            print("Failed to init exam: \(error)")
        }
    }

    func selectAnswer(_ qId: Int64, _ answer: String) {
        guard let q = questions.first(where: { $0.id == qId }) else { return }
        if q.questionType == .multi {
            var cur = examMulti[qId] ?? []
            if cur.contains(answer) { cur.remove(answer) } else { cur.insert(answer) }
            examMulti[qId] = cur
        } else {
            examAnswers[qId] = answer
        }
    }

    func isQuestionAnswered(_ qId: Int64) -> Bool {
        examAnswers[qId] != nil || examMulti[qId] != nil
    }

    func finishExam() {
        var correct = 0
        var details: [ExamDetail] = []

        for q in questions {
            guard let qId = q.id else { continue }
            let userAns: String
            if q.questionType == .multi {
                userAns = (examMulti[qId] ?? []).sorted().joined()
            } else {
                userAns = examAnswers[qId] ?? ""
            }

            let isCorrect = AnswerUtils.isAnswerCorrect(userAnswer: userAns, correctAnswer: q.answer)
            if isCorrect { correct += 1 } else {
                try? wrongDAO.upsertWrong(questionId: qId)
            }
            details.append(ExamDetail(questionId: qId, userAnswer: userAns, isCorrect: isCorrect))
        }

        correctCount = correct
        examScore = totalCount > 0 ? correct * 100 / totalCount : 0
        examFinished = true

        // Save record
        if let jsonData = try? JSONEncoder().encode(details),
           let jsonStr = String(data: jsonData, encoding: .utf8) {
            let record = ExamRecord(
                bankId: bankId,
                score: examScore,
                totalCount: totalCount,
                correctCount: correct,
                questionDetails: jsonStr
            )
            examRecordId = try? examDAO.insert(record)
        }
    }

    func goToNext() {
        guard currentIndex < questions.count - 1 else { return }
        currentIndex += 1
    }

    func goToPrevious() {
        guard currentIndex > 0 else { return }
        currentIndex -= 1
    }
}

// MARK: - ProfileViewModel

@MainActor
final class ProfileViewModel: ObservableObject {
    @Published var totalDuration: TimeInterval = 0
    @Published var totalAnswered: Int = 0
    @Published var totalExams: Int = 0
    @Published var totalWrongCount: Int = 0
    @Published var practiceRecords: [PracticeRecord] = []
    @Published var examRecords: [ExamRecord] = []
    @Published var isLoading = false

    let bankId: Int64?
    private let practiceDAO = PracticeRecordDAO()
    private let examDAO = ExamRecordDAO()
    private let wrongDAO = WrongRecordDAO()

    init(bankId: Int64? = nil) {
        self.bankId = bankId
    }

    func loadStats() {
        isLoading = true
        defer { isLoading = false }

        do {
            totalDuration = try practiceDAO.getTotalDuration()
            totalAnswered = try practiceDAO.getTotalAnswered()
            totalExams = try examDAO.getTotalExamCount()

            if let bid = bankId {
                practiceRecords = try practiceDAO.getAll(bankId: bid)
                examRecords = try examDAO.getAll(bankId: bid)
                totalWrongCount = try wrongDAO.getWrongCountForBank(bid)
            } else {
                practiceRecords = []
                examRecords = []
            }
        } catch {
            print("Failed to load stats: \(error)")
        }
    }

    func deletePracticeRecord(_ id: Int64) {
        try? practiceDAO.delete(id: id)
        loadStats()
    }
}

// MARK: - ImportViewModel

@MainActor
final class ImportViewModel: ObservableObject {
    @Published var bankName: String = ""
    @Published var isImporting: Bool = false
    @Published var errorMessage: String? = nil
    @Published var importedCount: Int = 0
    @Published var showSuccess: Bool = false

    func importFile(url: URL) {
        guard !bankName.trimmingCharacters(in: .whitespaces).isEmpty else {
            errorMessage = "请输入题库名称"
            return
        }

        isImporting = true
        errorMessage = nil

        do {
            let ext = url.pathExtension.lowercased()
            guard let parser = ParserRegistry.parser(for: ext) else {
                throw ParseError.unsupportedFormat(ext)
            }

            let content = try String(contentsOf: url, encoding: .utf8)
            let questions = try parser.parse(content: content, bankId: 0)

            guard !questions.isEmpty else {
                throw ParseError.invalidFormat("未解析到任何题目")
            }

            var bank = QuestionBank(name: bankName.trimmingCharacters(in: .whitespaces))
            try QuestionBankDAO().insert(&bank)

            guard let bankId = bank.id else {
                throw ParseError.invalidFormat("创建题库失败")
            }

            let qs = questions.map { q in
                Question(bankId: bankId, questionType: q.questionType,
                         content: q.content, options: q.options,
                         answer: q.answer, analysis: q.analysis)
            }
            try QuestionDAO().insertBatch(qs)
            try QuestionBankDAO().updateQuestionCount(bankId: bankId)

            importedCount = qs.count
            showSuccess = true
        } catch {
            errorMessage = error.localizedDescription
        }

        isImporting = false
    }
}
