import SwiftUI

struct HomeView: View {
    @ObservedObject var viewModel: HomeViewModel
    @State private var showImport = false

    var body: some View {
        Group {
            if viewModel.banks.isEmpty {
                emptyState
            } else {
                bankList
            }
        }
        .navigationTitle("刷题助手")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button { showImport = true } label: {
                    Image(systemName: "plus")
                }
            }
        }
        .sheet(isPresented: $showImport) {
            ImportView { bankName, questions in
                try viewModel.importBank(name: bankName, questions: questions)
            }
        }
        .onAppear { viewModel.loadBanks() }
    }

    // MARK: - Empty State

    private var emptyState: some View {
        VStack(spacing: 16) {
            Image(systemName: "books.vertical")
                .font(.system(size: 64))
                .foregroundColor(.gray)
            Text("还没有题库")
                .font(.title2)
                .foregroundColor(.gray)
            Text("点击右上角 + 按钮导入题库文件\n支持 .txt/.md/.json/.docx/.xlsx 格式")
                .multilineTextAlignment(.center)
                .font(.subheadline)
                .foregroundColor(.gray)
            Button {
                showImport = true
            } label: {
                Label("导入题库", systemImage: "plus.circle.fill")
                    .font(.headline)
            }
            .buttonStyle(.borderedProminent)
            .padding(.top, 8)
        }
        .padding()
    }

    // MARK: - Bank List

    private var bankList: some View {
        List {
            ForEach(viewModel.banks) { bank in
                NavigationLink {
                    PracticeView(bankId: bank.id ?? 0, bankName: bank.name)
                } label: {
                    BankRow(bank: bank)
                }
                .swipeActions(edge: .trailing) {
                    Button(role: .destructive) {
                        viewModel.deleteBank(bank)
                    } label: {
                        Label("删除", systemImage: "trash")
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
        .refreshable { viewModel.loadBanks() }
    }
}

// MARK: - Bank Row

struct BankRow: View {
    let bank: QuestionBank

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: "book.fill")
                .font(.title)
                .foregroundColor(.blue)
                .frame(width: 40, height: 40)
                .background(Color.blue.opacity(0.1))
                .cornerRadius(8)

            VStack(alignment: .leading, spacing: 4) {
                Text(bank.name)
                    .font(.headline)
                HStack(spacing: 16) {
                    Label("\(bank.questionCount)题", systemImage: "list.bullet")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text(bank.importDate.shortFormat)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(.vertical, 4)
    }
}
