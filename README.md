# 刷题助手 QuizApp 🤖📚

> 人工智能训练师三级考证刷题工具 — Android 端

一款专为**人工智能训练师三级**考证设计的 Android 刷题 App。支持多种刷题模式、模拟考试、错题集、学习进度追踪等功能。

## 功能特色 ✨

| 功能 | 说明 |
|------|------|
| **顺序练习** | 按顺序逐题练习，支持**续练**（自动保存进度）、**跳题**、**重新开始** |
| **随机刷题** | 随机抽取题目，可自定义题数（默认 100 题） |
| **题型分类** | 按单选题、多选题、判断题分类刷题 |
| **错题重做** | 自动记录错题，针对性巩固提分 |
| **模拟考试** | 按比例（单选70%+多选20%+判断10%）随机抽题，模拟真实考场 |
| **练习记录** | 每次练习自动保存，支持续练、删除记录 |
| **错题历史** | 每次练习保存答错的题目，支持查看和重新练习 |
| **考试记录** | 每次模拟考试保存分数、正确率、时间 |
| **数据统计** | 个人页展示学习进度、已答题数、正确率 |
| **题目跳转** | 答题卡网格视图，快速跳转到任意题目 |
| **题库导入** | 支持导入 txt / docx / xlsx / json / md 格式题库文件 |

## 内置题库 📖

首次启动自动导入以下题库：

- **人工智能训练师三级(1200题)** — 爬取自在线刷题平台
- **人工智能训练师(1000题)** — 理论题 1000 题版本
- **人工智能训练师(1432题)** — 试题库 1432 题

## 技术栈 🛠️

| 层 | 技术 |
|----|------|
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Repository 模式 |
| 数据库 | Room（7 张表） |
| 依赖注入 | Hilt |
| 导航 | Navigation Compose |
| 解析 | Apache POI（xlsx 解析）、kotlinx-serialization（json 解析） |
| 最低 SDK | Android 8.0 (API 26) |
| 目标 SDK | Android 15 (API 35) |

## 项目结构 📁

```
QuizApp/
├── app/
│   └── src/main/java/com/quizapp/
│       ├── data/
│       │   ├── db/
│       │   │   ├── entity/        # Room 实体 (7个)
│       │   │   ├── dao/           # 数据访问对象 (7个)
│       │   │   ├── AppDatabase.kt # 数据库单例
│       │   │   └── Converters.kt  # 类型转换器
│       │   ├── parser/            # 题库解析器 (txt/docx/xlsx/json)
│       │   └── repository/        # 数据仓库
│       ├── di/                    # Hilt 依赖注入模块
│       ├── ui/
│       │   ├── navigation/        # 路由导航
│       │   ├── screens/
│       │   │   ├── home/          # 题库列表页
│       │   │   ├── practice/      # 练习模式选择页
│       │   │   ├── question/      # 刷题页（核心）
│       │   │   ├── exam/          # 模拟考试 + 结果页
│       │   │   ├── profile/       # 个人统计页
│       │   │   └── importt/       # 文件导入页
│       │   └── theme/             # 主题、颜色、组件库
│       ├── QuizApp.kt             # Application 入口
│       └── MainActivity.kt        # 主 Activity
│   └── src/main/assets/           # 内置题库文件
├── CLAUDE.md                      # Claude Code 开发指南
└── build.gradle.kts
```

## 构建与运行 🔨

### 环境要求

- **JDK 17+**
- Android Studio Hedgehog 或更新版本
- Android SDK 35

### 编译 Debug APK

```bash
cd QuizApp
JAVA_HOME="/c/Program Files/Java/jdk-17.0.19+10" ./gradlew assembleDebug
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

### 编译 Release APK

```bash
JAVA_HOME="/c/Program Files/Java/jdk-17.0.19+10" ./gradlew assembleRelease
```

## 下载 📲

从 [GitHub Releases](https://github.com/MBXHJ/Quiz/releases) 下载最新 APK。

## 数据库说明 🗄️

| 表名 | 说明 |
|------|------|
| `question_banks` | 题库信息 |
| `questions` | 题目（单选/多选/判断） |
| `wrong_records` | 错题记录 |
| `exam_records` | 考试记录 |
| `answered_questions` | 已答题目 |
| `practice_progress` | 练习进度（续练支持） |
| `practice_records` | 练习历史记录 |

> 数据库版本 3，使用 `fallbackToDestructiveMigration()` — 版本升级会清空数据。

## 版权 📄

**本软件著作权人：唐家俊**  
本软件仅用于分享学习使用，不可用于任何商业行为。
