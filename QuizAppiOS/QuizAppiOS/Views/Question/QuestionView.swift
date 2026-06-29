import SwiftUI

/// Wraps PracticeViewModel and QuestionView together
struct QuestionListView: View {
    let bankId: Int64
    let mode: PracticeMode
    let type: QuestionType?
    let randomCount: Int?

    @StateObject private var viewModel: PracticeViewModel
    @State private var showAnswerCard = false
    @State private var showSearch = false
    @State private var searchText = ""
    @State private var allQuestions: [Question] = []

    init(bankId: Int64, mode: PracticeMode, type: QuestionType? = nil, randomCount: Int? = nil) {
        self.bankId = bankId
        self.mode = mode
        self.type = type
        self.randomCount = randomCount
        _viewModel = StateObject(wrappedValue: PracticeViewModel(bankId: bankId, mode: mode))
    }

    var body: some View {
        VStack(spacing: 0) {
            // Progress bar
            ProgressView(value: viewModel.progress)
                .tint(.blue)

            if viewModel.questions.isEmpty {
                Spacer()
                Text("暂无题目")
                    .foregroundColor(.secondary)
                Spacer()
            } else {
                // Question page
                TabView(selection: $viewModel.currentIndex) {
                    ForEach(Array(viewModel.questions.enumerated()), id: \.element.id) { index, question in
                        QuestionView(
                            question: question,
                            index: index,
                            totalCount: viewModel.totalCount,
                            onAnswer: { answer in
                                let correct = viewModel.submitAnswer(answer)
                                return correct
                            }
                        )
                        .tag(index)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))

                // Bottom navigation
                bottomBar
            }
        }
        .navigationTitle(mode.label)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItemGroup(placement: .navigationBarTrailing) {
                Button { showSearch = true } label: {
                    Image(systemName: "magnifyingglass")
                }
                Button { showAnswerCard = true } label: {
                    Image(systemName: "square.grid.2x2")
                }
            }
        }
        .sheet(isPresented: $showAnswerCard) {
            AnswerCardView(
                questions: viewModel.questions,
                currentIndex: viewModel.currentIndex,
                onJump: { viewModel.goTo(index: $0) }
            )
        }
        .sheet(isPresented: $showSearch) {
            SearchView(
                bankId: bankId,
                onSelect: { question in
                    showSearch = false
                    // Find index and jump
                    if let idx = viewModel.questions.firstIndex(where: { $0.id == question.id }) {
                        viewModel.goTo(index: idx)
                    }
                }
            )
        }
        .onAppear {
            viewModel.loadQuestions(type: type, randomCount: randomCount)
            // Check for resume
            if viewModel.hasProgress {
                // Show resume dialog handled in the view
            }
        }
        .onDisappear {
            viewModel.saveRecord()
        }
    }

    // MARK: - Bottom Bar

    private var bottomBar: some View {
        HStack {
            Text("\(viewModel.currentIndex + 1) / \(viewModel.totalCount)")
                .font(.subheadline)
                .foregroundColor(.secondary)

            Spacer()

            HStack(spacing: 4) {
                Image(systemName: "checkmark.circle.fill")
                    .foregroundColor(.green)
                Text("\(viewModel.correctAnswers)")
                    .foregroundColor(.green)
            }
            .font(.caption)

            HStack(spacing: 4) {
                Image(systemName: "xmark.circle.fill")
                    .foregroundColor(.red)
                Text("\(viewModel.wrongAnswers)")
                    .foregroundColor(.red)
            }
            .font(.caption)

            Spacer()

            Text(viewModel.elapsedTime.shortDuration)
                .font(.subheadline.monospacedDigit())
                .foregroundColor(.secondary)
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .background(Color(.systemGray6))
    }
}

// MARK: - Question View

struct QuestionView: View {
    let question: Question
    let index: Int
    let totalCount: Int
    let onAnswer: (String) -> Bool

    @StateObject private var vm: QuestionViewModel
    @State private var showNoteEditor = false
    @State private var noteText = ""
    @Environment(\.fontSizeScale) private var fontSizeScale

    init(question: Question, index: Int, totalCount: Int, onAnswer: @escaping (String) -> Bool) {
        self.question = question
        self.index = index
        self.totalCount = totalCount
        self.onAnswer = onAnswer
        _vm = StateObject(wrappedValue: QuestionViewModel(question: question))
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Header
                HStack {
                    Text("\(index + 1) / \(totalCount)")
                        .font(.subheadline.bold())
                    Spacer()
                    TypeTag(vm.question.questionType.label)

                    Button { vm.toggleFavorite() } label: {
                        Image(systemName: vm.isFavorite ? "star.fill" : "star")
                            .foregroundColor(vm.isFavorite ? .yellow : .gray)
                    }

                    Button { vm.toggleMarked() } label: {
                        Image(systemName: vm.isMarked ? "bookmark.fill" : "bookmark")
                            .foregroundColor(vm.isMarked ? .blue : .gray)
                    }
                }

                // Question content
                Text(vm.question.content)
                    .font(.system(size: 16 * fontSizeScale))
                    .padding(.vertical, 8)

                // Options
                switch vm.question.questionType {
                case .single:
                    singleChoiceOptions
                case .multi:
                    multiChoiceOptions
                case .judge:
                    judgeOptions
                }

                // Result card
                if vm.showResult {
                    resultCard
                }

                // Note
                if vm.note?.content.isNotEmpty == true {
                    notePreview
                }
            }
            .padding()
        }
        .sheet(isPresented: $showNoteEditor) {
            noteEditorSheet
        }
    }

    // MARK: - Option Views

    private var singleChoiceOptions: some View {
        VStack(spacing: 8) {
            ForEach(vm.question.options, id: \.self) { option in
                let letter = String(option.prefix(1))
                let text = String(option.dropFirst(2).trimmingCharacters(in: .whitespaces))
                OptionButton(
                    label: text,
                    letter: letter,
                    isSelected: vm.selectedAnswer == letter,
                    showResult: vm.showResult,
                    isCorrect: vm.question.answer == letter,
                    type: .single
                ) {
                    _ = onAnswer(letter)
                    vm.submitSingle(letter)
                }
            }
        }
    }

    private var multiChoiceOptions: some View {
        VStack(spacing: 8) {
            ForEach(vm.question.options, id: \.self) { option in
                let letter = String(option.prefix(1))
                let text = String(option.dropFirst(2).trimmingCharacters(in: .whitespaces))
                OptionButton(
                    label: text,
                    letter: letter,
                    isSelected: vm.multiSelected.contains(letter),
                    showResult: vm.showResult,
                    isCorrect: vm.question.answer.contains(letter),
                    type: .multi
                ) {
                    vm.toggleMulti(letter)
                }
            }

            if !vm.showResult {
                Button {
                    let answer = vm.multiSelected.sorted().joined()
                    _ = onAnswer(answer)
                    vm.confirmMulti()
                } label: {
                    Text("确认提交")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(vm.multiSelected.isEmpty ? Color.gray : Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .disabled(vm.multiSelected.isEmpty)
                .padding(.top, 8)
            }
        }
    }

    private var judgeOptions: some View {
        HStack(spacing: 16) {
            ForEach(["正确", "错误"], id: \.self) { choice in
                OptionButton(
                    label: choice,
                    letter: choice == "正确" ? "✓" : "✗",
                    isSelected: vm.selectedAnswer == choice,
                    showResult: vm.showResult,
                    isCorrect: vm.question.answer == choice,
                    type: .judge
                ) {
                    _ = onAnswer(choice)
                    vm.submitSingle(choice)
                }
            }
        }
    }

    // MARK: - Result Card

    private var resultCard: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: vm.isCorrect ? "checkmark.circle.fill" : "xmark.circle.fill")
                    .foregroundColor(vm.isCorrect ? .green : .red)
                Text(vm.isCorrect ? "回答正确！" : "回答错误！")
                    .font(.headline)
                    .foregroundColor(vm.isCorrect ? .green : .red)
            }

            Text("正确答案：\(vm.question.answer)")
                .font(.subheadline)

            if !vm.question.analysis.isEmpty {
                Text("解析：\(vm.question.analysis)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            if vm.wrongCount > 0 {
                Text("本题已错 \(vm.wrongCount) 次")
                    .font(.caption)
                    .foregroundColor(.red)
            }

            // Note button
            Button {
                noteText = vm.note?.content ?? ""
                showNoteEditor = true
            } label: {
                Label(vm.note != nil ? "编辑笔记" : "添加笔记", systemImage: "note.text")
                    .font(.caption)
            }
        }
        .padding()
        .background(vm.isCorrect ? Color.green.opacity(0.08) : Color.red.opacity(0.08))
        .cornerRadius(12)
    }

    // MARK: - Note Preview

    private var notePreview: some View {
        VStack(alignment: .leading, spacing: 4) {
            Label("笔记", systemImage: "note.text")
                .font(.caption.bold())
                .foregroundColor(.orange)
            Text(vm.note?.content ?? "")
                .font(.caption)
                .foregroundColor(.secondary)
                .lineLimit(2)
        }
        .padding(10)
        .background(Color.orange.opacity(0.08))
        .cornerRadius(8)
    }

    // MARK: - Note Editor Sheet

    private var noteEditorSheet: some View {
        NavigationStack {
            VStack {
                TextEditor(text: $noteText)
                    .padding()
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.systemGray4), lineWidth: 1)
                    )
                    .padding()
            }
            .navigationTitle("题目笔记")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("取消") { showNoteEditor = false }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("保存") {
                        vm.saveNote(noteText)
                        showNoteEditor = false
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}

// MARK: - Option Button

struct OptionButton: View {
    enum OptionType { case single, multi, judge }

    let label: String
    let letter: String
    let isSelected: Bool
    let showResult: Bool
    let isCorrect: Bool
    let type: OptionType
    let action: () -> Void

    @Environment(\.fontSizeScale) private var fontSizeScale

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                // Selection indicator
                Group {
                    switch type {
                    case .single:
                        Image(systemName: isSelected ? "circle.fill" : "circle")
                            .foregroundColor(isSelected ? .blue : .gray)
                    case .multi:
                        Image(systemName: isSelected ? "checkmark.square.fill" : "square")
                            .foregroundColor(isSelected ? .blue : .gray)
                    case .judge:
                        Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                            .foregroundColor(isSelected ? .blue : .gray)
                    }
                }
                .font(.title3)

                // Option letter
                Text(letter)
                    .font(.subheadline.bold())
                    .foregroundColor(.secondary)
                    .frame(width: 24)

                // Option text
                Text(label)
                    .font(.system(size: 15 * fontSizeScale))
                    .foregroundColor(.primary)
                    .multilineTextAlignment(.leading)

                Spacer()

                // Result indicator
                if showResult && isCorrect {
                    Image(systemName: "checkmark")
                        .foregroundColor(.green)
                } else if showResult && isSelected && !isCorrect {
                    Image(systemName: "xmark")
                        .foregroundColor(.red)
                }
            }
            .padding()
            .background(backgroundColor)
            .cornerRadius(10)
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(borderColor, lineWidth: 1)
            )
        }
        .disabled(showResult)
    }

    private var backgroundColor: Color {
        if showResult && isCorrect {
            return .green.opacity(0.08)
        } else if showResult && isSelected && !isCorrect {
            return .red.opacity(0.08)
        } else if isSelected && !showResult {
            return .blue.opacity(0.06)
        }
        return Color(.systemBackground)
    }

    private var borderColor: Color {
        if showResult && isCorrect { return .green.opacity(0.3) }
        if showResult && isSelected && !isCorrect { return .red.opacity(0.3) }
        if isSelected && !showResult { return .blue.opacity(0.3) }
        return .clear
    }
}

// MARK: - Type Tag

struct TypeTag: View {
    let label: String

    init(_ label: String) {
        self.label = label
    }

    var body: some View {
        Text(label)
            .font(.caption2)
            .padding(.horizontal, 8)
            .padding(.vertical, 2)
            .background(Color.blue.opacity(0.1))
            .foregroundColor(.blue)
            .cornerRadius(4)
    }
}
