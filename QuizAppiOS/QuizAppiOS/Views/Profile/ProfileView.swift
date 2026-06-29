import SwiftUI

struct ProfileView: View {
    @ObservedObject var viewModel: ProfileViewModel

    var body: some View {
        List {
            // Stats section
            Section("学习统计") {
                HStack(spacing: 0) {
                    StatCell(value: viewModel.totalDuration.formattedDuration, label: "总时长", icon: "clock.fill", color: .blue)
                    StatCell(value: "\(viewModel.totalAnswered)", label: "总答题", icon: "list.bullet", color: .green)
                    StatCell(value: "\(viewModel.totalExams)", label: "考试次数", icon: "doc.text.fill", color: .orange)
                }
                .padding(.vertical, 8)
            }

            // Wrong questions
            if let bankId = viewModel.bankId {
                Section("错题库") {
                    NavigationLink {
                        QuestionListView(bankId: bankId, mode: .wrong)
                    } label: {
                        Label("错题重做 (\(viewModel.totalWrongCount)题)", systemImage: "xmark.circle")
                            .foregroundColor(.red)
                    }
                }
            }

            // Practice records
            if viewModel.practiceRecords.isNotEmpty {
                Section("练习记录") {
                    ForEach(viewModel.practiceRecords) { record in
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Text(PracticeMode(rawValue: record.mode)?.label ?? record.mode)
                                    .font(.subheadline.bold())
                                Spacer()
                                Text(record.practiceDate.shortFormat)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            HStack(spacing: 16) {
                                Text("正确: \(record.correctCount)")
                                    .foregroundColor(.green)
                                Text("错误: \(record.wrongCount)")
                                    .foregroundColor(.red)
                                Text(record.duration.shortDuration)
                                    .foregroundColor(.secondary)
                            }
                            .font(.caption)
                        }
                        .swipeActions {
                            Button(role: .destructive) {
                                if let id = record.id {
                                    viewModel.deletePracticeRecord(id)
                                }
                            } label: {
                                Label("删除", systemImage: "trash")
                            }
                        }
                    }
                }
            }

            // Exam records
            if viewModel.examRecords.isNotEmpty {
                Section("考试记录") {
                    ForEach(viewModel.examRecords) { record in
                        NavigationLink {
                            ExamRecordDetailView(record: record)
                        } label: {
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("得分: \(record.score)分")
                                        .font(.subheadline.bold())
                                    Text("\(record.correctCount)/\(record.totalCount) 正确")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                Spacer()
                                Text(record.examDate.shortFormat)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                }
            }

            // Copyright
            Section("关于") {
                VStack(alignment: .leading, spacing: 4) {
                    Text("刷题助手 iOS")
                        .font(.headline)
                    Text("版本 1.0")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }

                VStack(alignment: .leading, spacing: 4) {
                    Text("著作权声明")
                        .font(.subheadline.bold())
                    Text("本软件著作权人：唐家俊\n本软件仅用于分享学习使用，不可用于任何商业行为。")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
        }
        .navigationTitle("我的")
        .onAppear { viewModel.loadStats() }
    }
}

// MARK: - Stat Cell

struct StatCell: View {
    let value: String
    let label: String
    let icon: String
    let color: Color

    var body: some View {
        VStack(spacing: 6) {
            Image(systemName: icon)
                .foregroundColor(color)
                .font(.title3)
            Text(value)
                .font(.subheadline.bold())
            Text(label)
                .font(.caption2)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
    }
}

// MARK: - Exam Record Detail View

struct ExamRecordDetailView: View {
    let record: ExamRecord

    var body: some View {
        List {
            Section("考试概况") {
                LabeledContent("得分", value: "\(record.score) 分")
                LabeledContent("正确率", value: "\(record.totalCount > 0 ? record.correctCount * 100 / record.totalCount : 0)%")
                LabeledContent("正确/总题", value: "\(record.correctCount)/\(record.totalCount)")
                LabeledContent("考试时间", value: record.examDate.fullFormat)
            }

            if !record.questionDetails.isEmpty {
                Section("答题详情") {
                    let details = parseDetails()
                    ForEach(Array(details.enumerated()), id: \.offset) { index, detail in
                        HStack {
                            Text("\(index + 1).")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            Image(systemName: detail.isCorrect ? "checkmark" : "xmark")
                                .foregroundColor(detail.isCorrect ? .green : .red)
                            Text("题\(detail.questionId)")
                                .font(.caption)
                            Text("答: \(detail.userAnswer.isEmpty ? "未作答" : detail.userAnswer)")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
            }
        }
        .navigationTitle("考试详情")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func parseDetails() -> [ExamDetail] {
        guard let data = record.questionDetails.data(using: .utf8),
              let details = try? JSONDecoder().decode([ExamDetail].self, from: data) else {
            return []
        }
        return details
    }
}
