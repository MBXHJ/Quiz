import SwiftUI

struct SettingsView: View {
    @EnvironmentObject private var settingsManager: SettingsManager
    @State private var showReminderTimePicker = false

    var body: some View {
        Form {
            // Theme
            Section("护眼模式") {
                Picker("主题", selection: $settingsManager.settings.themeMode) {
                    ForEach(ThemeMode.allCases, id: \.self) { mode in
                        Text(mode.label).tag(mode)
                    }
                }
                .pickerStyle(.segmented)
                .padding(.vertical, 4)
            }

            // Font size
            Section("字体大小") {
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Text("A")
                            .font(.caption)
                        Slider(value: $settingsManager.settings.fontSize, in: 0.8...1.4, step: 0.05)
                        Text("A")
                            .font(.title3)
                    }

                    HStack {
                        Text("\(Int(settingsManager.settings.fontSize * 100))%")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        Spacer()
                        Button("恢复默认") {
                            settingsManager.settings.fontSize = 1.0
                        }
                        .font(.caption)
                    }
                }

                // Preview
                Text("这是一段预览文字，用于查看当前字体大小效果。")
                    .font(.system(size: 14 * settingsManager.fontSizeScale))
                    .padding(.vertical, 4)
            }

            // Study reminder
            Section("学习提醒") {
                Toggle("每日学习提醒", isOn: $settingsManager.settings.reminderEnabled)

                if settingsManager.settings.reminderEnabled {
                    DatePicker(
                        "提醒时间",
                        selection: $settingsManager.settings.reminderTime,
                        displayedComponents: .hourAndMinute
                    )
                    .datePickerStyle(.compact)
                    .onChange(of: settingsManager.settings.reminderTime) { _, newTime in
                        updateReminder(enabled: true, time: newTime)
                    }
                    .onChange(of: settingsManager.settings.reminderEnabled) { _, enabled in
                        updateReminder(enabled: enabled, time: settingsManager.settings.reminderTime)
                    }
                }
            }

            // Export
            Section("数据") {
                Button {
                    // Export handled per-bank from ProfileView
                } label: {
                    Label("导出错题（请在题库统计页操作）", systemImage: "square.and.arrow.up")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .disabled(true)
            }

            // About
            Section("关于") {
                HStack {
                    Text("版本")
                    Spacer()
                    Text("1.0.0")
                        .foregroundColor(.secondary)
                }

                VStack(alignment: .leading, spacing: 4) {
                    Text("刷题助手 iOS")
                        .font(.headline)
                    Text("人工智能训练师三级考证刷题工具")
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
        .navigationTitle("设置")
        .onAppear {
            if settingsManager.settings.reminderEnabled {
                updateReminder(enabled: true, time: settingsManager.settings.reminderTime)
            }
        }
    }

    private func updateReminder(enabled: Bool, time: Date) {
        Task {
            let granted = await ReminderService.shared.requestPermission()
            if granted {
                await ReminderService.shared.scheduleReminder(enabled: enabled, time: time)
            }
        }
    }
}
