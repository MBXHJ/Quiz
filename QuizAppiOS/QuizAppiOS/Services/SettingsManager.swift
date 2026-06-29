import Foundation
import SwiftUI

/// Manages app settings via UserDefaults.
/// Mirrors Android SettingsManager functionality.
@MainActor
final class SettingsManager: ObservableObject {
    static let shared = SettingsManager()

    @Published var settings: AppSettings {
        didSet { save() }
    }

    private let defaults = UserDefaults.standard

    private enum Keys {
        static let themeMode = "themeMode"
        static let fontSize = "fontSize"
        static let reminderEnabled = "reminderEnabled"
        static let reminderHour = "reminderHour"
        static let reminderMinute = "reminderMinute"
    }

    private init() {
        let themeRaw = defaults.string(forKey: Keys.themeMode) ?? ThemeMode.system.rawValue
        let themeMode = ThemeMode(rawValue: themeRaw) ?? .system
        let fontSize = defaults.double(forKey: Keys.fontSize)
        let reminderEnabled = defaults.bool(forKey: Keys.reminderEnabled)
        let reminderHour = defaults.integer(forKey: Keys.reminderHour)
        let reminderMinute = defaults.integer(forKey: Keys.reminderMinute)

        var reminderTime = Calendar.current.date(from: DateComponents(hour: 20, minute: 0)) ?? Date()
        if reminderHour > 0 || reminderMinute > 0 {
            reminderTime = Calendar.current.date(from: DateComponents(hour: reminderHour, minute: reminderMinute)) ?? reminderTime
        }

        self.settings = AppSettings(
            themeMode: themeMode,
            fontSize: fontSize > 0 ? fontSize : 1.0,
            reminderEnabled: reminderEnabled,
            reminderTime: reminderTime
        )
    }

    private func save() {
        defaults.set(settings.themeMode.rawValue, forKey: Keys.themeMode)
        defaults.set(settings.fontSize, forKey: Keys.fontSize)
        defaults.set(settings.reminderEnabled, forKey: Keys.reminderEnabled)

        let components = Calendar.current.dateComponents([.hour, .minute], from: settings.reminderTime)
        defaults.set(components.hour ?? 20, forKey: Keys.reminderHour)
        defaults.set(components.minute ?? 0, forKey: Keys.reminderMinute)
    }

    // MARK: - Computed Properties

    var colorScheme: ColorScheme? {
        switch settings.themeMode {
        case .system: return nil
        case .light: return .light
        case .dark: return .dark
        }
    }

    var fontSizeScale: Double {
        settings.fontSize
    }
}
