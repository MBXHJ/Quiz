# 刷题助手 iOS 版

> SwiftUI 原生 iOS 版本，功能对齐 Android v1.6

## 环境要求

- macOS 14+ (Sonoma)
- Xcode 16+
- iOS 16.0+ 部署目标

## 技术栈

| 层 | 技术 |
|----|------|
| UI | SwiftUI |
| 数据库 | GRDB.swift (SQLite) |
| 架构 | MVVM + DAO |
| 依赖管理 | Swift Package Manager |
| 解析 | ZIPFoundation (docx) |

## 项目结构

```
QuizAppiOS/
├── Package.swift                    # SPM 依赖
├── QuizAppiOS/
│   ├── QuizAppiOSApp.swift          # @main 入口
│   ├── ContentView.swift            # TabView 主框架
│   ├── Models/Models.swift          # 10 个数据模型
│   ├── Database/
│   │   ├── DatabaseManager.swift    # GRDB 初始化 + 4 次迁移
│   │   └── DAOs.swift               # 10 个 DAO
│   ├── Parsers/Parsers.swift        # 5 种解析器 (txt/md/json/docx/xlsx)
│   ├── ViewModels/ViewModels.swift  # 5 个 ViewModel
│   ├── Views/
│   │   ├── Home/HomeView.swift
│   │   ├── Practice/PracticeView.swift
│   │   ├── Question/QuestionView.swift + AnswerCardView.swift
│   │   ├── Exam/ExamView.swift      # 考试 + 结果 + 回顾
│   │   ├── Profile/ProfileView.swift
│   │   ├── Import/ImportView.swift
│   │   └── Settings/SettingsView.swift
│   ├── Services/
│   │   ├── SettingsManager.swift    # UserDefaults 设置
│   │   ├── ReminderService.swift    # 本地通知
│   │   ├── ExportService.swift      # 错题导出
│   │   └── BuiltInBankImporter.swift # 内置题库导入
│   └── Utils/
│       ├── AnswerUtils.swift        # 答案规范化
│       └── Extensions.swift         # 扩展 + EnvironmentKey
└── Resources/                       # 内置题库 .txt 文件
```

## 功能对齐

✅ 全部 20 项 Android v1.6 功能已实现：

| 功能 | 状态 |
|------|------|
| 顺序练习（续练） | ✅ |
| 随机刷题（自定题数） | ✅ |
| 题型分类 | ✅ |
| 错题重做 | ✅ |
| 模拟考试（70/20/10） | ✅ |
| 考试回顾 | ✅ |
| ⭐ 收藏题目 | ✅ |
| 🔍 搜索题目 | ✅ |
| 📌 答题卡标记 | ✅ |
| 📝 笔记功能 | ✅ |
| ⏱️ 练习计时 | ✅ |
| 📊 学习报告 | ✅ |
| 🌙 护眼模式 | ✅ |
| 🔤 字体调整 | ✅ |
| 🔔 学习提醒 | ✅ |
| 📤 错题导出 | ✅ |
| 练习记录/删除 | ✅ |
| 考试记录/详情 | ✅ |
| 题库导入（5种格式） | ✅ |
| 内置题库（3套） | ✅ |

## 构建与运行

### 1. 在 Xcode 中打开

由于项目使用 Swift Package Manager，推荐创建 Xcode 项目并添加文件：

```bash
# 方法一：直接用 Xcode 创建新 iOS App 项目
# 然后将 QuizAppiOS/ 目录下所有 .swift 文件拖入项目
# 在 Xcode 中添加 SPM 依赖：GRDB.swift + ZIPFoundation

# 方法二：使用 swift package 命令行
cd QuizAppiOS
swift package resolve
open Package.swift
```

### 2. 添加内置题库

将 Android 版的 3 个内置题库 .txt 文件复制到 `QuizAppiOS/Resources/` 目录：
- `ai_trainer_1000.txt`
- `ai_trainer_1200.txt`
- `ai_trainer_1432.txt`

### 3. 编译运行

在 Xcode 中选择 iOS Simulator 或真机，按 Cmd+R 运行。

## 数据库

10 张表，分 4 次迁移（v1→v4），使用 GRDB Migrator：

| 表名 | 迁移版本 |
|------|----------|
| question_banks, questions, wrong_records, exam_records | v1 |
| practice_records, practice_progress | v2 |
| favorite_questions, marked_questions, question_notes | v3 |
| answered_questions | v4 |

## 版权

本软件著作权人：唐家俊
本软件仅用于分享学习使用，不可用于任何商业行为。
