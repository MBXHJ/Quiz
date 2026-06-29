import Foundation
import SwiftUI

// MARK: - Date Formatting

extension Date {
    var shortFormat: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: self)
    }

    var fullFormat: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm"
        return formatter.string(from: self)
    }

    var timeFormat: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: self)
    }
}

// MARK: - TimeInterval Formatting

extension TimeInterval {
    var formattedDuration: String {
        let hours = Int(self) / 3600
        let minutes = (Int(self) % 3600) / 60
        let seconds = Int(self) % 60

        if hours > 0 {
            return String(format: "%d时%d分", hours, minutes)
        } else if minutes > 0 {
            return String(format: "%d分%d秒", minutes, seconds)
        } else {
            return String(format: "%d秒", seconds)
        }
    }

    var shortDuration: String {
        let minutes = Int(self) / 60
        let seconds = Int(self) % 60
        return String(format: "%02d:%02d", minutes, seconds)
    }
}

// MARK: - Color Extensions

extension Color {
    static let appBlue = Color.blue
    static let appGreen = Color.green
    static let appRed = Color.red
    static let appOrange = Color.orange

    static let correctBg = Color.green.opacity(0.1)
    static let wrongBg = Color.red.opacity(0.1)
    static let selectedBg = Color.blue.opacity(0.1)
}

// MARK: - View Extensions

extension View {
    func cardStyle() -> some View {
        self
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)
    }
}

// MARK: - String Extensions

extension String {
    var isNotEmpty: Bool { !isEmpty }
}

// MARK: - Array Extensions

extension Array {
    var isNotEmpty: Bool { !isEmpty }

    func chunked(into size: Int) -> [[Element]] {
        stride(from: 0, to: count, by: size).map {
            Array(self[$0..<Swift.min($0 + size, count)])
        }
    }
}

extension Array where Element == Int64 {
    func toJsonString() -> String {
        guard let data = try? JSONEncoder().encode(self),
              let str = String(data: data, encoding: .utf8) else {
            return "[]"
        }
        return str
    }

    static func fromJsonString(_ str: String) -> [Int64] {
        guard let data = str.data(using: .utf8),
              let result = try? JSONDecoder().decode([Int64].self, from: data) else {
            return []
        }
        return result
    }
}

// MARK: - Environment Keys

struct FontSizeScaleKey: EnvironmentKey {
    static let defaultValue: Double = 1.0
}

extension EnvironmentValues {
    var fontSizeScale: Double {
        get { self[FontSizeScaleKey.self] }
        set { self[FontSizeScaleKey.self] = newValue }
    }
}
