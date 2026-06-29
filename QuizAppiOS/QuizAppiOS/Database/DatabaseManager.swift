import Foundation
import GRDB

/// Central database manager for QuizApp.
/// Uses GRDB.swift with WAL mode, foreign keys enabled.
/// Migration strategy mirrors Android: destructive migration on schema change.
final class DatabaseManager {
    static let shared = DatabaseManager()

    private var dbQueue: DatabaseQueue?

    private init() {}

    // MARK: - Setup

    func getDatabase() throws -> DatabaseQueue {
        if let existing = dbQueue { return existing }

        let fileManager = FileManager.default
        let docDir = try fileManager.url(
            for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: true
        )
        let dbPath = docDir.appendingPathComponent("quiz_app.db").path

        let queue = try DatabaseQueue(path: dbPath, configuration: {
            var config = Configuration()
            config.foreignKeysEnabled = true
            config.prepareDatabase { db in
                try db.execute(sql: "PRAGMA journal_mode = WAL")
            }
            return config
        }())

        // Register migrations
        var migrator = DatabaseMigrator()

        // v1: Core tables
        migrator.registerMigration("v1") { db in
            try db.create(table: "question_banks") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("name", .text).notNull()
                t.column("description", .text).defaults(to: "")
                t.column("questionCount", .integer).defaults(to: 0)
                t.column("importDate", .double).notNull()
            }

            try db.create(table: "questions") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("bankId", .integer).notNull().indexed()
                t.column("questionType", .text).notNull()
                t.column("content", .text).notNull()
                t.column("options", .text).defaults(to: "")
                t.column("answer", .text).defaults(to: "")
                t.column("analysis", .text).defaults(to: "")
                t.foreignKey(["bankId"], references: "question_banks", onDelete: .cascade)
            }

            try db.create(table: "wrong_records") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("questionId", .integer).notNull().unique()
                t.column("wrongCount", .integer).defaults(to: 1)
                t.column("lastWrongTime", .double).notNull()
                t.column("isRemoved", .boolean).defaults(to: false)
                t.foreignKey(["questionId"], references: "questions", onDelete: .cascade)
            }

            try db.create(table: "exam_records") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("bankId", .integer).notNull().indexed()
                t.column("score", .integer).notNull()
                t.column("totalCount", .integer).notNull()
                t.column("correctCount", .integer).notNull()
                t.column("questionDetails", .text).defaults(to: "")
                t.column("examDate", .double).notNull()
                t.foreignKey(["bankId"], references: "question_banks", onDelete: .cascade)
            }
        }

        // v2: Practice tables
        migrator.registerMigration("v2") { db in
            try db.create(table: "practice_records") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("bankId", .integer).notNull().indexed()
                t.column("mode", .text).notNull()
                t.column("totalCount", .integer).notNull()
                t.column("correctCount", .integer).notNull()
                t.column("wrongCount", .integer).notNull()
                t.column("duration", .double).defaults(to: 0)
                t.column("practiceDate", .double).notNull()
                t.foreignKey(["bankId"], references: "question_banks", onDelete: .cascade)
            }

            try db.create(table: "practice_progress") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("bankId", .integer).notNull()
                t.column("mode", .text).notNull()
                t.column("currentIndex", .integer).defaults(to: 0)
                t.column("answeredIds", .text).defaults(to: "[]")
                t.column("wrongIds", .text).defaults(to: "[]")
                t.column("updatedAt", .double).notNull()
                t.foreignKey(["bankId"], references: "question_banks", onDelete: .cascade)
            }
        }

        // v3: Favorite, Marked, Notes
        migrator.registerMigration("v3") { db in
            try db.create(table: "favorite_questions") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("questionId", .integer).notNull().unique()
                t.column("createdAt", .double).notNull()
                t.foreignKey(["questionId"], references: "questions", onDelete: .cascade)
            }

            try db.create(table: "marked_questions") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("questionId", .integer).notNull().unique()
                t.column("createdAt", .double).notNull()
                t.foreignKey(["questionId"], references: "questions", onDelete: .cascade)
            }

            try db.create(table: "question_notes") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("questionId", .integer).notNull().unique()
                t.column("content", .text).defaults(to: "")
                t.column("createdAt", .double).notNull()
                t.column("updatedAt", .double).notNull()
                t.foreignKey(["questionId"], references: "questions", onDelete: .cascade)
            }
        }

        // v4: Answered questions
        migrator.registerMigration("v4") { db in
            try db.create(table: "answered_questions") { t in
                t.autoIncrementedPrimaryKey("id")
                t.column("questionId", .integer).notNull()
                t.column("practiceRecordId", .integer).notNull().indexed()
                t.column("userAnswer", .text).defaults(to: "")
                t.column("isCorrect", .boolean).defaults(to: false)
                t.foreignKey(["questionId"], references: "questions", onDelete: .cascade)
                t.foreignKey(["practiceRecordId"], references: "practice_records", onDelete: .cascade)
            }
        }

        try migrator.migrate(queue)
        dbQueue = queue
        return queue
    }

    // MARK: - Convenience

    var db: DatabaseQueue {
        get throws { try getDatabase() }
    }

    func isDatabaseEmpty() throws -> Bool {
        let count = try db.read { db in
            try Int.fetchOne(db, sql: "SELECT COUNT(*) FROM question_banks") ?? 0
        }
        return count == 0
    }
}
