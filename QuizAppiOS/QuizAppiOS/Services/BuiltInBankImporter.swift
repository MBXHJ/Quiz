import Foundation

/// Imports built-in question bank files from the app bundle on first launch.
/// Mirrors Android's first-launch auto-import of 3 built-in banks.
struct BuiltInBankImporter {

    static func importIfNeeded() async {
        let dbManager = DatabaseManager.shared
        do {
            guard try dbManager.isDatabaseEmpty() else { return }
        } catch {
            return
        }

        let bankDAO = QuestionBankDAO()
        let questionDAO = QuestionDAO()
        let parser = TxtParser()

        let builtinBanks: [(name: String, fileName: String)] = [
            ("人工智能训练师三级(1200题)", "ai_trainer_1200"),
            ("人工智能训练师(1000题)", "ai_trainer_1000"),
            ("人工智能训练师(1432题)", "ai_trainer_1432"),
        ]

        for (name, fileName) in builtinBanks {
            guard let url = Bundle.main.url(forResource: fileName, withExtension: "txt"),
                  let content = try? String(contentsOf: url, encoding: .utf8) else {
                print("Built-in bank not found: \(fileName)")
                continue
            }

            let questions = (try? parser.parse(content: content, bankId: 0)) ?? []
            guard !questions.isEmpty else { continue }

            do {
                var bank = QuestionBank(name: name, description: "内置题库")
                try bankDAO.insert(&bank)
                guard let bankId = bank.id else { continue }

                let qs = questions.map { q in
                    Question(bankId: bankId, questionType: q.questionType,
                             content: q.content, options: q.options,
                             answer: q.answer, analysis: q.analysis)
                }
                try questionDAO.insertBatch(qs)
                try bankDAO.updateQuestionCount(bankId: bankId)
            } catch {
                print("Failed to import built-in bank \(name): \(error)")
            }
        }
    }
}
