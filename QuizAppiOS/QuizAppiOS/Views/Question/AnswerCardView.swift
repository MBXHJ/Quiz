import SwiftUI

// MARK: - Answer Card View

struct AnswerCardView: View {
    let questions: [Question]
    let currentIndex: Int
    let onJump: (Int) -> Void

    @Environment(\.dismiss) private var dismiss

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 8), count: 5)

    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVGrid(columns: columns, spacing: 8) {
                    ForEach(Array(questions.enumerated()), id: \.offset) { index, _ in
                        Button {
                            onJump(index)
                            dismiss()
                        } label: {
                            Text("\(index + 1)")
                                .font(.subheadline.bold())
                                .frame(maxWidth: .infinity)
                                .frame(height: 44)
                                .background(cellColor(for: index))
                                .foregroundColor(index == currentIndex ? .white : .primary)
                                .cornerRadius(8)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 8)
                                        .stroke(index == currentIndex ? Color.blue : Color(.systemGray4), lineWidth: index == currentIndex ? 2 : 1)
                                )
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("答题卡 (\(questions.count)题)")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("关闭") { dismiss() }
                }
            }
        }
    }

    private func cellColor(for index: Int) -> Color {
        if index == currentIndex { return .blue }
        return Color(.systemBackground)
    }
}

// MARK: - Search View

struct SearchView: View {
    let bankId: Int64
    let onSelect: (Question) -> Void

    @State private var searchText = ""
    @State private var results: [Question] = []
    @State private var isSearching = false
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            VStack {
                if results.isEmpty && !searchText.isEmpty && !isSearching {
                    ContentUnavailableView("未找到结果", systemImage: "magnifyingglass", description: Text("尝试其他关键词"))
                } else if results.isEmpty && searchText.isEmpty {
                    VStack {
                        Text("搜索题目")
                            .font(.headline)
                            .padding(.top, 32)
                        Text("输入关键词在当前题库中搜索")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                } else {
                    List(results) { question in
                        Button {
                            onSelect(question)
                        } label: {
                            VStack(alignment: .leading, spacing: 4) {
                                HStack {
                                    TypeTag(question.questionType.label)
                                    Text(question.content)
                                        .lineLimit(2)
                                        .font(.subheadline)
                                }
                                if !question.analysis.isEmpty {
                                    Text(question.analysis)
                                        .lineLimit(1)
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                            }
                        }
                    }
                }
            }
            .searchable(text: $searchText, prompt: "输入题目关键词")
            .navigationTitle("搜索题目")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("关闭") { dismiss() }
                }
            }
            .onChange(of: searchText) { _, newValue in
                guard !newValue.isEmpty else { results = []; return }
                isSearching = true
                Task {
                    if let qs = try? QuestionDAO().search(bankId, keyword: newValue) {
                        results = qs
                    }
                    isSearching = false
                }
            }
        }
    }
}
