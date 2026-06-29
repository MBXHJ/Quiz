import Foundation
import GRDB

// MARK: - QuestionBank DAO

struct QuestionBankDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func getAll() throws -> [QuestionBank] {
        try dbManager.db.read { db in
            try RowDecoder<QuestionBank>()
                .fetchAll(db, sql: "SELECT * FROM question_banks ORDER BY importDate DESC")
        }
    }

    func insert(_ bank: inout QuestionBank) throws {
        try dbManager.db.write { db in
            bank.id = try db.executeAndReturnLastInsertedRowID(
                sql: """
                    INSERT INTO question_banks (name, description, questionCount, importDate)
                    VALUES (?, ?, ?, ?)
                    """,
                arguments: [bank.name, bank.description, bank.questionCount, bank.importDate.timeIntervalSince1970]
            )
        }
    }

    func delete(id: Int64) throws {
        try dbManager.db.write { db in
            try db.execute(sql: "DELETE FROM question_banks WHERE id = ?", arguments: [id])
        }
    }

    func updateQuestionCount(bankId: Int64) throws {
        try dbManager.db.write { db in
            try db.execute(sql: """
                UPDATE question_banks SET questionCount = (
                    SELECT COUNT(*) FROM questions WHERE bankId = ?
                ) WHERE id = ?
                """, arguments: [bankId, bankId])
        }
    }

    func getCount() throws -> Int {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: "SELECT COUNT(*) FROM question_banks") ?? 0
        }
    }
}

// MARK: - Question DAO

struct QuestionDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func getByBank(_ bankId: Int64) throws -> [Question] {
        try dbManager.db.read { db in
            try RowDecoder<Question>().fetchAll(db,
                sql: "SELECT * FROM questions WHERE bankId = ? ORDER BY id", arguments: [bankId])
        }
    }

    func getByType(_ bankId: Int64, type: QuestionType) throws -> [Question] {
        try dbManager.db.read { db in
            try RowDecoder<Question>().fetchAll(db,
                sql: "SELECT * FROM questions WHERE bankId = ? AND questionType = ? ORDER BY id",
                arguments: [bankId, type.rawValue])
        }
    }

    func getRandom(_ bankId: Int64, limit: Int? = nil) throws -> [Question] {
        try dbManager.db.read { db in
            let sql = limit != nil
                ? "SELECT * FROM questions WHERE bankId = ? ORDER BY RANDOM() LIMIT ?"
                : "SELECT * FROM questions WHERE bankId = ? ORDER BY RANDOM()"
            let args: [DatabaseValueConvertible] = limit != nil ? [bankId, limit!] : [bankId]
            return try RowDecoder<Question>().fetchAll(db, sql: sql, arguments: StatementArguments(args))
        }
    }

    func getCount(_ bankId: Int64) throws -> Int {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: "SELECT COUNT(*) FROM questions WHERE bankId = ?", arguments: [bankId]) ?? 0
        }
    }

    func getByTypeCounts(_ bankId: Int64) throws -> [QuestionType: Int] {
        try dbManager.db.read { db in
            let rows = try Row.fetchAll(db, sql: """
                SELECT questionType, COUNT(*) as cnt FROM questions
                WHERE bankId = ? GROUP BY questionType
                """, arguments: [bankId])
            var result: [QuestionType: Int] = [:]
            for row in rows {
                if let type = QuestionType(rawValue: row["questionType"]) {
                    result[type] = row["cnt"]
                }
            }
            return result
        }
    }

    func search(_ bankId: Int64, keyword: String) throws -> [Question] {
        try dbManager.db.read { db in
            try RowDecoder<Question>().fetchAll(db,
                sql: "SELECT * FROM questions WHERE bankId = ? AND content LIKE ? ORDER BY id",
                arguments: [bankId, "%\(keyword)%"])
        }
    }

    func insertBatch(_ questions: [Question]) throws {
        try dbManager.db.write { db in
            for q in questions {
                _ = try db.executeAndReturnLastInsertedRowID(
                    sql: """
                        INSERT INTO questions (bankId, questionType, content, options, answer, analysis)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                    arguments: [q.bankId, q.questionType.rawValue, q.content,
                                q.options.joined(separator: "|||"), q.answer, q.analysis]
                )
            }
        }
    }

    func getById(_ id: Int64) throws -> Question? {
        try dbManager.db.read { db in
            try RowDecoder<Question>().fetchOne(db,
                sql: "SELECT * FROM questions WHERE id = ?", arguments: [id])
        }
    }

    func getByIds(_ ids: [Int64]) throws -> [Question] {
        guard !ids.isEmpty else { return [] }
        let placeholders = ids.map { _ in "?" }.joined(separator: ",")
        return try dbManager.db.read { db in
            try RowDecoder<Question>().fetchAll(db,
                sql: "SELECT * FROM questions WHERE id IN (\(placeholders)) ORDER BY id",
                arguments: StatementArguments(ids))
        }
    }
}

// MARK: - WrongRecord DAO

struct WrongRecordDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func getWrongQuestions(bankId: Int64) throws -> [(WrongRecord, Question)] {
        try dbManager.db.read { db in
            let rows = try Row.fetchAll(db, sql: """
                SELECT wr.*, q.*
                FROM wrong_records wr
                INNER JOIN questions q ON wr.questionId = q.id
                WHERE q.bankId = ? AND wr.isRemoved = 0
                ORDER BY wr.lastWrongTime DESC
                """, arguments: [bankId])
            return rows.map { row in
                let wr = WrongRecord(
                    id: row["id"], questionId: row["questionId"],
                    wrongCount: row["wrongCount"],
                    lastWrongTime: Date(timeIntervalSince1970: row["lastWrongTime"]),
                    isRemoved: row["isRemoved"]
                )
                let q = Question(
                    id: row["id_1"], bankId: row["bankId"],
                    questionType: QuestionType(rawValue: row["questionType_1"]) ?? .single,
                    content: row["content"],
                    options: (row["options"] as String?)?.split(separator: "|||").map(String.init) ?? [],
                    answer: row["answer"], analysis: row["analysis"]
                )
                return (wr, q)
            }
        }
    }

    func getWrongCount(questionId: Int64) throws -> Int {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: "SELECT wrongCount FROM wrong_records WHERE questionId = ?", arguments: [questionId]) ?? 0
        }
    }

    func upsertWrong(questionId: Int64) throws {
        try dbManager.db.write { db in
            if try Row.fetchOne(db, sql: "SELECT id FROM wrong_records WHERE questionId = ?", arguments: [questionId]) != nil {
                try db.execute(sql: """
                    UPDATE wrong_records SET wrongCount = wrongCount + 1,
                    lastWrongTime = ?, isRemoved = 0 WHERE questionId = ?
                    """, arguments: [Date().timeIntervalSince1970, questionId])
            } else {
                try db.execute(sql: """
                    INSERT INTO wrong_records (questionId, wrongCount, lastWrongTime, isRemoved)
                    VALUES (?, 1, ?, 0)
                    """, arguments: [questionId, Date().timeIntervalSince1970])
            }
        }
    }

    func removeWrong(questionId: Int64) throws {
        try dbManager.db.write { db in
            try db.execute(sql: "UPDATE wrong_records SET isRemoved = 1 WHERE questionId = ?", arguments: [questionId])
        }
    }

    func getWrongCountForBank(_ bankId: Int64) throws -> Int {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: """
                SELECT COUNT(*) FROM wrong_records wr
                INNER JOIN questions q ON wr.questionId = q.id
                WHERE q.bankId = ? AND wr.isRemoved = 0
                """, arguments: [bankId]) ?? 0
        }
    }
}

// MARK: - ExamRecord DAO

struct ExamRecordDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func getAll(bankId: Int64) throws -> [ExamRecord] {
        try dbManager.db.read { db in
            try RowDecoder<ExamRecord>().fetchAll(db,
                sql: "SELECT * FROM exam_records WHERE bankId = ? ORDER BY examDate DESC",
                arguments: [bankId])
        }
    }

    func insert(_ record: ExamRecord) throws -> Int64 {
        try dbManager.db.write { db in
            try db.executeAndReturnLastInsertedRowID(sql: """
                INSERT INTO exam_records (bankId, score, totalCount, correctCount, questionDetails, examDate)
                VALUES (?, ?, ?, ?, ?, ?)
                """, arguments: [
                record.bankId, record.score, record.totalCount,
                record.correctCount, record.questionDetails,
                record.examDate.timeIntervalSince1970
            ])
        }
    }

    func getById(_ id: Int64) throws -> ExamRecord? {
        try dbManager.db.read { db in
            try RowDecoder<ExamRecord>().fetchOne(db,
                sql: "SELECT * FROM exam_records WHERE id = ?", arguments: [id])
        }
    }

    func getTotalExamCount() throws -> Int {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: "SELECT COUNT(*) FROM exam_records") ?? 0
        }
    }
}

// MARK: - PracticeRecord DAO

struct PracticeRecordDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func getAll(bankId: Int64) throws -> [PracticeRecord] {
        try dbManager.db.read { db in
            try RowDecoder<PracticeRecord>().fetchAll(db,
                sql: "SELECT * FROM practice_records WHERE bankId = ? ORDER BY practiceDate DESC",
                arguments: [bankId])
        }
    }

    func insert(_ record: inout PracticeRecord) throws {
        try dbManager.db.write { db in
            record.id = try db.executeAndReturnLastInsertedRowID(sql: """
                INSERT INTO practice_records (bankId, mode, totalCount, correctCount, wrongCount, duration, practiceDate)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, arguments: [
                record.bankId, record.mode, record.totalCount,
                record.correctCount, record.wrongCount,
                record.duration, record.practiceDate.timeIntervalSince1970
            ])
        }
    }

    func delete(id: Int64) throws {
        try dbManager.db.write { db in
            try db.execute(sql: "DELETE FROM practice_records WHERE id = ?", arguments: [id])
        }
    }

    func getTotalDuration() throws -> TimeInterval {
        try dbManager.db.read { db in
            try Double.fetchOne(db, sql: "SELECT COALESCE(SUM(duration), 0) FROM practice_records") ?? 0
        }
    }

    func getTotalAnswered() throws -> Int {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: "SELECT COALESCE(SUM(totalCount), 0) FROM practice_records") ?? 0
        }
    }

    func getBestScore(bankId: Int64) throws -> Int {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: """
                SELECT COALESCE(MAX(correctCount * 100 / totalCount), 0)
                FROM practice_records WHERE bankId = ? AND totalCount > 0
                """, arguments: [bankId]) ?? 0
        }
    }
}

// MARK: - PracticeProgress DAO

struct PracticeProgressDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func get(bankId: Int64, mode: String) throws -> PracticeProgress? {
        try dbManager.db.read { db in
            try RowDecoder<PracticeProgress>().fetchOne(db,
                sql: "SELECT * FROM practice_progress WHERE bankId = ? AND mode = ?",
                arguments: [bankId, mode])
        }
    }

    func save(_ progress: inout PracticeProgress) throws {
        try dbManager.db.write { db in
            let answeredJson = try JSONEncoder().encode(progress.answeredIds)
            let wrongJson = try JSONEncoder().encode(progress.wrongIds)
            let answeredStr = String(data: answeredJson, encoding: .utf8) ?? "[]"
            let wrongStr = String(data: wrongJson, encoding: .utf8) ?? "[]"

            if let existing = try RowDecoder<PracticeProgress>().fetchOne(db,
                sql: "SELECT * FROM practice_progress WHERE bankId = ? AND mode = ?",
                arguments: [progress.bankId, progress.mode]
            ) {
                try db.execute(sql: """
                    UPDATE practice_progress SET currentIndex = ?, answeredIds = ?, wrongIds = ?, updatedAt = ?
                    WHERE id = ?
                    """, arguments: [progress.currentIndex, answeredStr, wrongStr, Date().timeIntervalSince1970, existing.id!])
            } else {
                progress.id = try db.executeAndReturnLastInsertedRowID(sql: """
                    INSERT INTO practice_progress (bankId, mode, currentIndex, answeredIds, wrongIds, updatedAt)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, arguments: [progress.bankId, progress.mode, progress.currentIndex, answeredStr, wrongStr, Date().timeIntervalSince1970])
            }
        }
    }

    func delete(bankId: Int64, mode: String) throws {
        try dbManager.db.write { db in
            try db.execute(sql: "DELETE FROM practice_progress WHERE bankId = ? AND mode = ?", arguments: [bankId, mode])
        }
    }
}

// MARK: - FavoriteQuestion DAO

struct FavoriteQuestionDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func isFavorite(questionId: Int64) throws -> Bool {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: "SELECT COUNT(*) FROM favorite_questions WHERE questionId = ?", arguments: [questionId]) ?? 0 > 0
        }
    }

    func toggle(questionId: Int64) throws -> Bool {
        try dbManager.db.write { db in
            if try Row.fetchOne(db, sql: "SELECT id FROM favorite_questions WHERE questionId = ?", arguments: [questionId]) != nil {
                try db.execute(sql: "DELETE FROM favorite_questions WHERE questionId = ?", arguments: [questionId])
                return false
            } else {
                try db.execute(sql: "INSERT INTO favorite_questions (questionId, createdAt) VALUES (?, ?)",
                    arguments: [questionId, Date().timeIntervalSince1970])
                return true
            }
        }
    }

    func getFavoriteQuestions(bankId: Int64) throws -> [Question] {
        try dbManager.db.read { db in
            try RowDecoder<Question>().fetchAll(db, sql: """
                SELECT q.* FROM questions q
                INNER JOIN favorite_questions fq ON q.id = fq.questionId
                WHERE q.bankId = ? ORDER BY fq.createdAt DESC
                """, arguments: [bankId])
        }
    }

    func getFavoriteCount(bankId: Int64) throws -> Int {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: """
                SELECT COUNT(*) FROM favorite_questions fq
                INNER JOIN questions q ON fq.questionId = q.id
                WHERE q.bankId = ?
                """, arguments: [bankId]) ?? 0
        }
    }
}

// MARK: - MarkedQuestion DAO

struct MarkedQuestionDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func isMarked(questionId: Int64) throws -> Bool {
        try dbManager.db.read { db in
            try Int.fetchOne(db, sql: "SELECT COUNT(*) FROM marked_questions WHERE questionId = ?", arguments: [questionId]) ?? 0 > 0
        }
    }

    func toggle(questionId: Int64) throws -> Bool {
        try dbManager.db.write { db in
            if try Row.fetchOne(db, sql: "SELECT id FROM marked_questions WHERE questionId = ?", arguments: [questionId]) != nil {
                try db.execute(sql: "DELETE FROM marked_questions WHERE questionId = ?", arguments: [questionId])
                return false
            } else {
                try db.execute(sql: "INSERT INTO marked_questions (questionId, createdAt) VALUES (?, ?)",
                    arguments: [questionId, Date().timeIntervalSince1970])
                return true
            }
        }
    }

    func getMarkedQuestionIds(bankId: Int64) throws -> Set<Int64> {
        try dbManager.db.read { db in
            let rows = try Row.fetchAll(db, sql: """
                SELECT mq.questionId FROM marked_questions mq
                INNER JOIN questions q ON mq.questionId = q.id
                WHERE q.bankId = ?
                """, arguments: [bankId])
            return Set(rows.map { $0["questionId"] })
        }
    }
}

// MARK: - QuestionNote DAO

struct QuestionNoteDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func get(questionId: Int64) throws -> QuestionNote? {
        try dbManager.db.read { db in
            try RowDecoder<QuestionNote>().fetchOne(db,
                sql: "SELECT * FROM question_notes WHERE questionId = ?", arguments: [questionId])
        }
    }

    func save(_ note: inout QuestionNote) throws {
        try dbManager.db.write { db in
            if let existing = try RowDecoder<QuestionNote>().fetchOne(db,
                sql: "SELECT * FROM question_notes WHERE questionId = ?", arguments: [note.questionId]
            ) {
                try db.execute(sql: """
                    UPDATE question_notes SET content = ?, updatedAt = ? WHERE id = ?
                    """, arguments: [note.content, Date().timeIntervalSince1970, existing.id!])
            } else {
                note.id = try db.executeAndReturnLastInsertedRowID(sql: """
                    INSERT INTO question_notes (questionId, content, createdAt, updatedAt)
                    VALUES (?, ?, ?, ?)
                    """, arguments: [note.questionId, note.content, Date().timeIntervalSince1970, Date().timeIntervalSince1970])
            }
        }
    }

    func delete(questionId: Int64) throws {
        try dbManager.db.write { db in
            try db.execute(sql: "DELETE FROM question_notes WHERE questionId = ?", arguments: [questionId])
        }
    }
}

// MARK: - AnsweredQuestion DAO

struct AnsweredQuestionDAO {
    private let dbManager: DatabaseManager

    init(dbManager: DatabaseManager = .shared) {
        self.dbManager = dbManager
    }

    func insertBatch(_ answers: [AnsweredQuestion]) throws {
        try dbManager.db.write { db in
            for a in answers {
                try db.execute(sql: """
                    INSERT INTO answered_questions (questionId, practiceRecordId, userAnswer, isCorrect)
                    VALUES (?, ?, ?, ?)
                    """, arguments: [a.questionId, a.practiceRecordId, a.userAnswer, a.isCorrect])
            }
        }
    }

    func getByRecord(_ recordId: Int64) throws -> [AnsweredQuestion] {
        try dbManager.db.read { db in
            try RowDecoder<AnsweredQuestion>().fetchAll(db,
                sql: "SELECT * FROM answered_questions WHERE practiceRecordId = ?", arguments: [recordId])
        }
    }
}

// MARK: - GRDB RowDecoder Helper

/// A lightweight decoder for fetching our models from GRDB rows.
/// Our models are Codable and don't use GRDB's Record protocol directly,
/// so we decode via JSON round-trip from the row.
struct RowDecoder<T: Codable> {
    func fetchAll(_ db: Database, sql: String, arguments: StatementArguments = StatementArguments()) throws -> [T] {
        let rows = try Row.fetchAll(db, sql: sql, arguments: arguments)
        return try rows.map { row in
            let dict = row.toDictionary()
            let data = try JSONSerialization.data(withJSONObject: dict)
            return try JSONDecoder().decode(T.self, from: data)
        }
    }

    func fetchOne(_ db: Database, sql: String, arguments: StatementArguments = StatementArguments()) throws -> T? {
        guard let row = try Row.fetchOne(db, sql: sql, arguments: arguments) else { return nil }
        let dict = row.toDictionary()
        let data = try JSONSerialization.data(withJSONObject: dict)
        return try JSONDecoder().decode(T.self, from: data)
    }
}

extension Row {
    func toDictionary() -> [String: Any] {
        var dict: [String: Any] = [:]
        for (col, dbValue) in self {
            switch dbValue.storage {
            case .null: dict[col] = NSNull()
            case .int64(let v): dict[col] = v
            case .double(let v): dict[col] = v
            case .string(let v): dict[col] = v
            case .blob(let v): dict[col] = v
            }
        }
        return dict
    }
}
