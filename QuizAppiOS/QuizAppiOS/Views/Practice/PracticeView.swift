import SwiftUI

struct PracticeView: View {
    let bankId: Int64
    let bankName: String

    @State private var totalCount: Int = 0
    @State private var wrongCount: Int = 0
    @State private var favoriteCount: Int = 0
    @State private var showRandomPicker = false
    @State private var randomCount: Int = 100
    @State private var showTypePicker = false
    @State private var navigateToQuestion = false
    @State private var practiceMode: PracticeMode = .sequential
    @State private var selectedType: QuestionType? = nil

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Stats card
                statsCard

                // Mode selection
                modeSection
            }
            .padding()
        }
        .navigationTitle(bankName)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear(perform: loadStats)
        .sheet(isPresented: $showRandomPicker) {
            randomPickerSheet
        }
        .sheet(isPresented: $showTypePicker) {
            typePickerSheet
        }
        .navigationDestination(isPresented: $navigateToQuestion) {
            QuestionListView(
                bankId: bankId,
                mode: practiceMode,
                type: selectedType,
                randomCount: practiceMode == .random ? randomCount : nil
            )
        }
    }

    // MARK: - Stats Card

    private var statsCard: some View {
        HStack(spacing: 0) {
            StatBadge(value: "\(totalCount)", label: "总题数", color: .blue)
            Divider().frame(height: 40)
            StatBadge(value: "\(wrongCount)", label: "错题数", color: .red)
            Divider().frame(height: 40)
            StatBadge(value: "\(favoriteCount)", label: "收藏", color: .orange)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.06), radius: 4, y: 2)
    }

    // MARK: - Mode Section

    private var modeSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("选择刷题模式")
                .font(.headline)
                .padding(.bottom, 4)

            ModeCard(
                icon: "list.bullet",
                title: "顺序练习",
                subtitle: "按顺序逐题练习，自动保存进度",
                action: { startPractice(.sequential) }
            )

            ModeCard(
                icon: "shuffle",
                title: "随机刷题",
                subtitle: "随机抽取题目，可自定义题数（默认100题）",
                action: { showRandomPicker = true }
            )

            ModeCard(
                icon: "square.grid.3x3",
                title: "题型分类",
                subtitle: "按单选题/多选题/判断题分类练习",
                action: { showTypePicker = true }
            )

            if wrongCount > 0 {
                ModeCard(
                    icon: "xmark.circle",
                    title: "错题重做",
                    subtitle: "共 \(wrongCount) 道错题，针对性巩固提分",
                    action: { startPractice(.wrong) }
                )
            }

            if favoriteCount > 0 {
                ModeCard(
                    icon: "star.fill",
                    title: "收藏练习",
                    subtitle: "共 \(favoriteCount) 道收藏题，集中攻克重点",
                    action: { startPractice(.favorite) }
                )
            }

            ModeCard(
                icon: "doc.text.fill",
                title: "模拟考试",
                subtitle: "按比例随机抽题（单选70%+多选20%+判断10%），限时评分",
                action: {
                    practiceMode = .random
                    navigateToExam = true
                }
            )
        }
        .background(
            NavigationLink(
                destination: ExamView(bankId: bankId),
                isActive: $navigateToExam
            ) { EmptyView() }
            .hidden()
        )
    }

    @State private var navigateToExam = false

    // MARK: - Actions

    private func startPractice(_ mode: PracticeMode, type: QuestionType? = nil) {
        practiceMode = mode
        selectedType = type
        navigateToQuestion = true
    }

    private func loadStats() {
        do {
            totalCount = try QuestionDAO().getCount(bankId)
            wrongCount = try WrongRecordDAO().getWrongCountForBank(bankId)
            favoriteCount = try FavoriteQuestionDAO().getFavoriteCount(bankId: bankId)
        } catch {
            print("Failed to load stats: \(error)")
        }
    }

    // MARK: - Sheets

    private var randomPickerSheet: some View {
        NavigationStack {
            VStack(spacing: 24) {
                Text("随机刷题题数")
                    .font(.headline)
                    .padding(.top, 32)

                HStack(spacing: 16) {
                    ForEach([50, 100, 200], id: \.self) { count in
                        Button {
                            randomCount = count
                        } label: {
                            Text("\(count)题")
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                                .background(randomCount == count ? Color.blue : Color(.systemGray5))
                                .foregroundColor(randomCount == count ? .white : .primary)
                                .cornerRadius(8)
                        }
                    }
                }
                .padding(.horizontal)

                Stepper("自定义: \(randomCount) 题", value: $randomCount, in: 5...500, step: 5)
                    .padding(.horizontal)

                Spacer()

                Button {
                    showRandomPicker = false
                    startPractice(.random)
                } label: {
                    Text("开始刷题 (\(randomCount)题)")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                }
                .padding()
            }
            .navigationTitle("随机刷题")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("取消") { showRandomPicker = false }
                }
            }
        }
        .presentationDetents([.medium])
    }

    private var typePickerSheet: some View {
        NavigationStack {
            List {
                ForEach(QuestionType.allCases, id: \.self) { type in
                    Button {
                        showTypePicker = false
                        startPractice(.type, type: type)
                    } label: {
                        HStack {
                            Label(type.label, systemImage: typeIcon(for: type))
                            Spacer()
                            Image(systemName: "chevron.right")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
            }
            .navigationTitle("选择题型")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("取消") { showTypePicker = false }
                }
            }
        }
        .presentationDetents([.medium])
    }

    private func typeIcon(for type: QuestionType) -> String {
        switch type {
        case .single: return "circle"
        case .multi: return "checklist"
        case .judge: return "checkmark.circle"
        }
    }
}

// MARK: - Subviews

struct StatBadge: View {
    let value: String
    let label: String
    let color: Color

    var body: some View {
        VStack(spacing: 4) {
            Text(value)
                .font(.title2.bold())
                .foregroundColor(color)
            Text(label)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
    }
}

struct ModeCard: View {
    let icon: String
    let title: String
    let subtitle: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(systemName: icon)
                    .font(.title3)
                    .foregroundColor(.blue)
                    .frame(width: 32)

                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(.subheadline.bold())
                        .foregroundColor(.primary)
                    Text(subtitle)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(10)
            .shadow(color: .black.opacity(0.04), radius: 2, y: 1)
        }
    }
}
