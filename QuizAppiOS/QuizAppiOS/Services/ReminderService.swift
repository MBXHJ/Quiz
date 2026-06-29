import Foundation
import UserNotifications

/// Manages local notification reminders for daily study.
/// Mirrors Android ReminderHelper functionality.
final class ReminderService {
    static let shared = ReminderService()

    private let center = UNUserNotificationCenter.current()

    private init() {}

    // MARK: - Request Permission

    func requestPermission() async -> Bool {
        do {
            return try await center.requestAuthorization(options: [.alert, .sound, .badge])
        } catch {
            return false
        }
    }

    // MARK: - Schedule Reminder

    func scheduleReminder(enabled: Bool, time: Date) async {
        // Remove existing reminders
        center.removeAllPendingNotificationRequests()

        guard enabled else { return }

        let components = Calendar.current.dateComponents([.hour, .minute], from: time)

        let content = UNMutableNotificationContent()
        content.title = "📚 学习提醒"
        content.body = "别忘了今天的刷题任务！坚持练习，考试必过！"
        content.sound = .default
        content.badge = 1

        let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: true)
        let request = UNNotificationRequest(
            identifier: "daily_study_reminder",
            content: content,
            trigger: trigger
        )

        do {
            try await center.add(request)
        } catch {
            print("Failed to schedule reminder: \(error)")
        }
    }

    // MARK: - Check Status

    func getPendingReminders() async -> [UNNotificationRequest] {
        await center.pendingNotificationRequests()
    }
}
