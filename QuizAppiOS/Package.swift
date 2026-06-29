// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "QuizAppiOS",
    platforms: [
        .iOS(.v16)
    ],
    dependencies: [
        .package(url: "https://github.com/groue/GRDB.swift.git", from: "6.29.0"),
        .package(url: "https://github.com/weichsel/ZIPFoundation.git", from: "0.9.0"),
    ],
    targets: [
        .executableTarget(
            name: "QuizAppiOS",
            dependencies: [
                .product(name: "GRDB", package: "GRDB.swift"),
                .product(name: "ZIPFoundation", package: "ZIPFoundation"),
            ],
            path: "QuizAppiOS",
            resources: [
                .process("Resources")
            ]
        ),
    ]
)
