import SwiftUI

@main
struct QuizAppiOSApp: App {
    @StateObject private var settingsManager = SettingsManager.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .preferredColorScheme(settingsManager.colorScheme)
                .environment(\.fontSizeScale, settingsManager.fontSizeScale)
                .environmentObject(settingsManager)
        }
    }
}
