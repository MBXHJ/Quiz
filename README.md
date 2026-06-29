# 刷题助手 QuizApp 🤖📚

> 人工智能训练师三级考证刷题工具 — Android 端

一款专为**人工智能训练师三级**考证设计的 Android 刷题 App。支持多种刷题模式、模拟考试、错题集、学习进度追踪等功能。

> **当前版本：v1.6**

## 功能特色 ✨

| 功能 | 说明 |
|------|------|
| **顺序练习** | 按顺序逐题练习，支持**续练**（自动保存进度）、**跳题**、**重新开始** |
| **随机刷题** | 随机抽取题目，可自定义题数（默认 100 题） |
| **题型分类** | 按单选题、多选题、判断题分类刷题 |
| **错题重做** | 自动记录错题，针对性巩固提分 |
| **模拟考试** | 按比例（单选70%+多选20%+判断10%）随机抽题，模拟真实考场 |
| **考试回顾** | 考试结束后可查看每题作答情况，支持复盘 |
| **⭐ 收藏题目** | 收藏重点题目，单独练习巩固 |
| **🔍 搜索题目** | 按题目内容关键词搜索，快速定位 |
| **📌 答题卡标记** | 答题卡网格视图，标记答题状态，快速跳转到任意题目 |
| **📝 笔记功能** | 为题目添加个人笔记，方便复习 |
| **⏱️ 练习计时** | 练习过程中显示用时，记录学习时长 |
| **📊 学习报告** | 统计总学习时长、正确率、最佳成绩 |
| **🌙 护眼模式** | 支持跟随系统/浅色/深色三种主题切换 |
| **🔤 字体调整** | 题目和选项字体大小可调（80%~140%） |
| **🔔 学习提醒** | 每日定时推送通知，提醒坚持学习 |
| **📤 错题导出** | 导出错题为文件，支持分享到微信/QQ等 |
| **练习记录** | 每次练习自动保存，支持续练、删除记录 |
| **错题历史** | 每次练习保存答错的题目，支持查看和重新练习 |
| **考试记录** | 每次模拟考试保存分数、正确率、时间 |
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
| 数据库 | Room（10 张表） |
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
│       │   │   ├── entity/        # Room 实体 (10个)
│       │   │   ├── dao/           # 数据访问对象 (10个)
│       │   │   ├── AppDatabase.kt # 数据库单例
│       │   │   └── Converters.kt  # 类型转换器
│       │   ├── parser/            # 题库解析器 (txt/docx/xlsx/json/md)
│       │   └── repository/        # 数据仓库
│       ├── di/                    # Hilt 依赖注入模块
│       ├── ui/
│       │   ├── navigation/        # 路由导航
│       │   ├── components/        # 通用 UI 组件
│       │   ├── screens/
│       │   │   ├── home/          # 题库列表页
│       │   │   ├── practice/      # 练习模式选择页
│       │   │   ├── question/      # 刷题页（核心）
│       │   │   ├── exam/          # 模拟考试 + 考试回顾 + 结果页
│       │   │   ├── profile/       # 个人统计页
│       │   │   ├── settings/      # 设置页（主题/字体/提醒）
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
| `favorite_questions` | ⭐ 收藏题目 |
| `marked_questions` | 📌 标记题目 |
| `question_notes` | 📝 题目笔记 |

> 数据库版本 6，使用 `fallbackToDestructiveMigration()` — 版本升级会清空数据。

## 版权 📄

**本软件著作权人：唐家俊**  
本软件仅用于分享学习使用，不可用于任何商业行为。
