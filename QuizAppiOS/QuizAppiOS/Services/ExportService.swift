import Foundation
import SwiftUI
import UniformTypeIdentifiers

/// Service for exporting wrong questions to shareable files.
/// Mirrors Android wrong-question export functionality.
final class ExportService {
    static let shared = ExportService()

    private init() {}

    // MARK: - Generate Export File

    /// Generate a temporary text file with wrong questions for sharing
    func exportWrongQuestions(
        bankName: String,
        wrongQuestions: [(WrongRecord, Question)]
    ) throws -> URL {
        var lines: [String] = []
        lines.append("错题导出 - \(bankName)")
        lines.append("导出时间: \(Date().fullFormat)")
        lines.append("共 \(wrongQuestions.count) 道错题")
        lines.append(String(repeating: "=", count: 40))
        lines.append("")

        for (index, (wr, q)) in wrongQuestions.enumerated() {
            lines.append("\(index + 1). [\(q.questionType.label)] \(q.content)")

            if !q.options.isEmpty {
                for opt in q.options {
                    lines.append("   \(opt)")
                }
            }

            lines.append("   正确答案: \(q.answer)")
            if !q.analysis.isEmpty {
                lines.append("   解析: \(q.analysis)")
            }
            lines.append("   错误次数: \(wr.wrongCount)")
            lines.append("")
        }

        let content = lines.joined(separator: "\n")

        let tempDir = FileManager.default.temporaryDirectory
        let fileName = "错题_\(bankName)_\(Date().shortFormat).txt"
        let fileURL = tempDir.appendingPathComponent(fileName)

        try content.write(to: fileURL, atomically: true, encoding: .utf8)
        return fileURL
    }

    // MARK: - Share Sheet

    @MainActor
    func shareWrongQuestions(
        bankName: String,
        wrongQuestions: [(WrongRecord, Question)]
    ) {
        do {
            let fileURL = try exportWrongQuestions(bankName: bankName, wrongQuestions: wrongQuestions)

            let activityVC = UIActivityViewController(
                activityItems: [fileURL],
                applicationActivities: nil
            )

            // Present from root view controller
            if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
               let rootVC = windowScene.windows.first?.rootViewController {
                // Find topmost presented VC
                var topVC = rootVC
                while let presented = topVC.presentedViewController {
                    topVC = presented
                }
                topVC.present(activityVC, animated: true)
            }
        } catch {
            print("Export failed: \(error)")
        }
    }
}
