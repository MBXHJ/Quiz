import SwiftUI

// MARK: - Exam View

struct ExamView: View {
    let bankId: Int64

    @StateObject private var viewModel: ExamViewModel
    @State private var showSubmitAlert = false
    @Environment(\.fontSizeScale) private var fontSizeScale

    init(bankId: Int64) {
        self.bankId = bankId
        _viewModel = StateObject(wrappedValue: ExamViewModel(bankId: bankId))
    }

    var body: some View {
        Group {
            if viewModel.examFinished {
                ExamResultView(viewModel: viewModel)
            } else {
                examContent
            }
        }
        .navigationTitle("模拟考试")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { viewModel.initExam() }
        .alert("确认交卷？", isPresented: $showSubmitAlert) {
            Button("继续答题", role: .cancel) {}
            Button("交卷", role: .destructive) { viewModel.finishExam() }
        } message: {
            Text("还有 \(viewModel.totalCount - viewModel.answeredCount) 道题未作答")
        }
    }

    // MARK: - Exam Content

    private var examContent: some View {
        VStack(spacing: 0) {
            ProgressView(value: viewModel.progress)
                .tint(.blue)

            if viewModel.isLoading {
                Spacer()
                ProgressView("加载考题...")
                Spacer()
            } else if let q = viewModel.currentQuestion {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        // Question header
                        HStack {
                            Text("第 \(viewModel.currentIndex + 1) 题 / 共 \(viewModel.totalCount) 题")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            Spacer()
                            TypeTag(q.questionType.label)
                        }

                        Text(q.content)
                            .font(.system(size: 16 * fontSizeScale))
                            .padding(.vertical, 8)

                        // Options
                        switch q.questionType {
                        case .single:
                            examSingleOptions(q)
                        case .multi:
                            examMultiOptions(q)
                        case .judge:
                            examJudgeOptions(q)
                        }
                    }
                    .padding()
                }

                // Bottom navigation
                HStack {
                    Button {
                        viewModel.goToPrevious()
                    } label: {
                        HStack {
                            Image(systemName: "chevron.left")
                            Text("上一题")
                        }
                    }
                    .disabled(viewModel.currentIndex == 0)

                    Spacer()

                    if viewModel.currentIndex < viewModel.totalCount - 1 {
                        Button {
                            viewModel.goToNext()
                        } label: {
                            HStack {
                                Text("下一题")
                                Image(systemName: "chevron.right")
                            }
                        }
                    } else {
                        Button {
                            showSubmitAlert = true
                        } label: {
                            HStack {
                                Image(systemName: "checkmark")
                                Text("交卷")
                            }
                            .foregroundColor(.white)
                            .padding(.horizontal, 20)
                            .padding(.vertical, 8)
                            .background(Color.green)
                            .cornerRadius(8)
                        }
                    }
                }
                .padding(.horizontal)
                .padding(.vertical, 8)
                .background(Color(.systemGray6))
            }
        }
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button("交卷 (\(viewModel.answeredCount)/\(viewModel.totalCount))") {
                    showSubmitAlert = true
                }
                .font(.caption)
            }
        }
    }

    // MARK: - Option Views

    private func examSingleOptions(_ q: Question) -> some View {
        VStack(spacing: 8) {
            ForEach(q.options, id: \.self) { option in
                let letter = String(option.prefix(1))
                let text = String(option.dropFirst(2).trimmingCharacters(in: .whitespaces))
                Button {
                    viewModel.selectAnswer(q.id ?? 0, letter)
                } label: {
                    HStack {
                        Image(systemName: viewModel.examAnswers[q.id ?? 0] == letter ? "circle.fill" : "circle")
                            .foregroundColor(viewModel.examAnswers[q.id ?? 0] == letter ? .blue : .gray)
                        Text(letter)
                            .font(.subheadline.bold())
                            .foregroundColor(.secondary)
                        Text(text)
                            .foregroundColor(.primary)
                        Spacer()
                    }
                    .padding()
                    .background(viewModel.examAnswers[q.id ?? 0] == letter ? Color.blue.opacity(0.06) : Color(.systemBackground))
                    .cornerRadius(10)
                }
            }
        }
    }

    private func examMultiOptions(_ q: Question) -> some View {
        VStack(spacing: 8) {
            ForEach(q.options, id: \.self) { option in
                let letter = String(option.prefix(1))
                let text = String(option.dropFirst(2).trimmingCharacters(in: .whitespaces))
                Button {
                    viewModel.selectAnswer(q.id ?? 0, letter)
                } label: {
                    HStack {
                        Image(systemName: (viewModel.examMulti[q.id ?? 0] ?? []).contains(letter) ? "checkmark.square.fill" : "square")
                            .foregroundColor((viewModel.examMulti[q.id ?? 0] ?? []).contains(letter) ? .blue : .gray)
                        Text(letter)
                            .font(.subheadline.bold())
                            .foregroundColor(.secondary)
                        Text(text)
                            .foregroundColor(.primary)
                        Spacer()
                    }
                    .padding()
                    .cornerRadius(10)
                }
            }
        }
    }

    private func examJudgeOptions(_ q: Question) -> some View {
        HStack(spacing: 16) {
            ForEach(["正确", "错误"], id: \.self) { choice in
                Button {
                    viewModel.selectAnswer(q.id ?? 0, choice)
                } label: {
                    VStack {
                        Image(systemName: viewModel.examAnswers[q.id ?? 0] == choice ? "checkmark.circle.fill" : "circle")
                            .font(.title)
                            .foregroundColor(viewModel.examAnswers[q.id ?? 0] == choice ? .blue : .gray)
                        Text(choice)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 80)
                    .background(viewModel.examAnswers[q.id ?? 0] == choice ? Color.blue.opacity(0.06) : Color(.systemGray6))
                    .cornerRadius(10)
                }
            }
        }
    }
}

// MARK: - Exam Result View

struct ExamResultView: View {
    @ObservedObject var viewModel: ExamViewModel

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Score circle
                ZStack {
                    Circle()
                        .stroke(scoreColor.opacity(0.2), lineWidth: 12)
                        .frame(width: 160, height: 160)

                    Circle()
                        .trim(from: 0, to: viewModel.totalCount > 0 ? Double(viewModel.correctCount) / Double(viewModel.totalCount) : 0)
                        .stroke(scoreColor, style: StrokeStyle(lineWidth: 12, lineCap: .round))
                        .rotationEffect(.degrees(-90))
                        .frame(width: 160, height: 160)

                    VStack {
                        Text("\(viewModel.examScore)")
                            .font(.system(size: 42, weight: .bold))
                            .foregroundColor(scoreColor)
                        Text("分")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .padding(.top, 16)

                Text(viewModel.examScore >= 60 ? "恭喜通过！" : "继续加油！")
                    .font(.title3.bold())

                // Stats
                HStack(spacing: 0) {
                    StatBadge(value: "\(viewModel.totalCount)", label: "总题数", color: .primary)
                    StatBadge(value: "\(viewModel.correctCount)", label: "正确", color: .green)
                    StatBadge(value: "\(viewModel.totalCount - viewModel.correctCount)", label: "错误", color: .red)
                }
                .padding()
                .background(Color(.systemBackground))
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.06), radius: 4, y: 2)

                // Review button
                NavigationLink {
                    ExamReviewView(viewModel: viewModel)
                } label: {
                    Label("回顾错题", systemImage: "arrow.counterclockwise")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }

                // Back button
                NavigationLink {
                    // Pop to root - handled by navigation
                } label: {
                    Text("返回题库")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color(.systemGray5))
                        .foregroundColor(.primary)
                        .cornerRadius(10)
                }
            }
            .padding()
        }
        .navigationBarBackButtonHidden(true)
    }

    private var scoreColor: Color {
        viewModel.examScore >= 60 ? .green : .red
    }
}

// MARK: - Exam Review View

struct ExamReviewView: View {
    @ObservedObject var viewModel: ExamViewModel
    @Environment(\.fontSizeScale) private var fontSizeScale

    var body: some View {
        List {
            ForEach(Array(viewModel.questions.enumerated()), id: \.offset) { index, question in
                let qId = question.id ?? 0
                let userAns: String = {
                    if question.questionType == .multi {
                        return (viewModel.examMulti[qId] ?? []).sorted().joined()
                    }
                    return viewModel.examAnswers[qId] ?? ""
                }()
                let isCorrect = AnswerUtils.isAnswerCorrect(userAnswer: userAns, correctAnswer: question.answer)

                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Text("\(index + 1).")
                            .font(.subheadline.bold())
                        TypeTag(question.questionType.label)
                        Spacer()
                        Image(systemName: isCorrect ? "checkmark.circle.fill" : "xmark.circle.fill")
                            .foregroundColor(isCorrect ? .green : .red)
                    }

                    Text(question.content)
                        .font(.system(size: 14 * fontSizeScale))

                    if !question.options.isEmpty {
                        VStack(alignment: .leading, spacing: 2) {
                            ForEach(question.options, id: \.self) { opt in
                                let letter = String(opt.prefix(1))
                                Text(opt)
                                    .font(.caption)
                                    .foregroundColor(
                                        letter == userAns && !isCorrect ? .red :
                                        question.answer.contains(letter) ? .green : .secondary
                                    )
                            }
                        }
                    }

                    HStack {
                        Text("你的答案: \(userAns.isEmpty ? "未作答" : userAns)")
                            .font(.caption)
                            .foregroundColor(isCorrect ? .green : .red)
                        Text("正确答案: \(question.answer)")
                            .font(.caption)
                            .foregroundColor(.green)
                    }

                    if !question.analysis.isEmpty {
                        Text("解析: \(question.analysis)")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
                .padding(.vertical, 4)
            }
        }
        .navigationTitle("考试回顾")
        .navigationBarTitleDisplayMode(.inline)
    }
}
