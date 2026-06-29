import SwiftUI
import UniformTypeIdentifiers

struct ImportView: View {
    let onImport: (String, [Question]) throws -> Void

    @StateObject private var viewModel = ImportViewModel()
    @State private var showFilePicker = false
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Spacer()

                Image(systemName: "doc.badge.plus")
                    .font(.system(size: 56))
                    .foregroundColor(.blue)

                Text("导入题库")
                    .font(.title.bold())

                Text("支持 .txt / .md / .json / .docx / .xlsx 格式")
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                // Bank name input
                TextField("题库名称（如：人工智能训练师）", text: $viewModel.bankName)
                    .textFieldStyle(.roundedBorder)
                    .padding(.horizontal, 32)

                // Supported formats info
                VStack(alignment: .leading, spacing: 6) {
                    Label("TXT/MD：每题为 题号+选项+答案+解析 格式", systemImage: "checkmark")
                    Label("JSON：数组格式 [{content, type, options, answer}]", systemImage: "checkmark")
                    Label("DOCX/XLSX：自动提取文本解析", systemImage: "checkmark")
                }
                .font(.caption)
                .foregroundColor(.secondary)
                .padding(.horizontal)

                Spacer()

                if viewModel.isImporting {
                    ProgressView("正在解析题库...")
                } else {
                    Button {
                        showFilePicker = true
                    } label: {
                        Text("选择文件并导入")
                            .font(.headline)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                    .padding(.horizontal)
                    .disabled(viewModel.bankName.trimmingCharacters(in: .whitespaces).isEmpty)
                }

                if let error = viewModel.errorMessage {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                        .padding(.horizontal)
                }
            }
            .padding()
            .navigationTitle("导入题库")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("取消") { dismiss() }
                }
            }
            .fileImporter(
                isPresented: $showFilePicker,
                allowedContentTypes: [
                    .plainText,
                    .json,
                    UTType(filenameExtension: "md") ?? .plainText,
                    UTType(filenameExtension: "docx") ?? .data,
                    UTType(filenameExtension: "xlsx") ?? .data,
                ],
                allowsMultipleSelection: false
            ) { result in
                switch result {
                case .success(let urls):
                    guard let url = urls.first else { return }
                    viewModel.importFile(url: url)

                    if viewModel.showSuccess {
                        // Trigger callback
                        let qs = (try? ParserRegistry.parser(for: url.pathExtension))?
                            .parse(content: (try? String(contentsOf: url, encoding: .utf8)) ?? "", bankId: 0) ?? []
                        try? onImport(viewModel.bankName, qs)
                        dismiss()
                    }
                case .failure(let error):
                    viewModel.errorMessage = error.localizedDescription
                }
            }
            .alert("导入成功", isPresented: $viewModel.showSuccess) {
                Button("确定") { dismiss() }
            } message: {
                Text("成功导入 \(viewModel.importedCount) 道题目！")
            }
        }
    }
}
