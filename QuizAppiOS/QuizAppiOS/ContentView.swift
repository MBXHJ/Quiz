import SwiftUI

struct ContentView: View {
    @StateObject private var homeVM = HomeViewModel()
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            NavigationStack {
                HomeView(viewModel: homeVM)
            }
            .tabItem {
                Label("题库", systemImage: "books.vertical.fill")
            }
            .tag(0)

            NavigationStack {
                ProfileView(viewModel: ProfileViewModel())
            }
            .tabItem {
                Label("我的", systemImage: "person.fill")
            }
            .tag(1)

            NavigationStack {
                SettingsView()
            }
            .tabItem {
                Label("设置", systemImage: "gearshape.fill")
            }
            .tag(2)
        }
        .onAppear {
            homeVM.loadBanks()
            // Import built-in banks on first launch
            Task {
                await BuiltInBankImporter.importIfNeeded()
                homeVM.loadBanks()
            }
        }
    }
}
