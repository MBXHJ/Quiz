## v1.7 更新内容

### ✨ 新增功能

- **🧠 艾宾浩斯遗忘曲线智能复习**：答错自动排期（0/1/2/4/7/15/30天），PracticeScreen紫色「今日复习」卡片入口，答对晋级答错重置
- **📅 每日目标 + 打卡日历**：首页今日进度条+连续天数徽章，Profile 90天热力图，设置可调目标(10-500题)
- **📊 成绩趋势图表**：Repository层数据查询已实现（日正确率趋势、日答题量、题型正确率）
- **🏷️ 自定义标签系统**：创建/删除/颜色选择标签，题目打标签，标签筛选练习，标签管理页
- **🔊 语音朗读 TTS**：朗读题目+选项，播放/停止按钮，设置可开关+自动播放
- **🎯 薄弱知识点分析**：自动识别正确率<60%的题库，Profile红色警告卡片引导针对性练习

### 🐛 修复

- QuestionScreen顶部6个按钮导致标题截断 → 改为收藏+TTS外露，其余收入下拉菜单
- PracticeScreen/SettingsScreen Column+verticalScroll → 改为LazyColumn提升滚动流畅度
- ExamViewModel 100个协程并发竞态 → 合并为单协程顺序执行
- ProfileScreen forEach内remember状态泄漏 → 提升为父级单例状态
- calculateStreak N次DB查询 → 改为单次查询365天数据

### 🛠 涉及模块

| 模块 | 变更 |
|------|------|
| 数据库 | v6→v7迁移，新增review_schedule/daily_stats/tags/question_tags 4张表 |
| Entity | 新增 ReviewScheduleEntity, DailyStatsEntity, TagEntity, QuestionTagEntity |
| DAO | 新增 ReviewScheduleDao, DailyStatsDao, TagDao, QuestionTagDao |
| Repository | 新增~30个方法（复习排期/每日统计/标签CRUD/趋势数据/薄弱分析） |
| DI | DatabaseModule 新增4个DAO注入，SettingsManager 新增3个配置项 |
| PracticeScreen | LazyColumn重构+今日复习卡片+标签筛选入口+随机数钳制 |
| QuestionScreen | 顶部菜单优化+TTS按钮+标签弹窗+返回防护 |
| BankListScreen | 首页今日进度卡片 |
| ProfileScreen | 打卡日历+薄弱环节+版本号+删除状态修复 |
| SettingsScreen | LazyColumn重构+每日目标+语音设置 |
| ExamViewModel | 协程批量化+空题保护+复习排期 |
| Navigation | 新增TagManage路由 |
| 新建文件 | CalendarHeatmap, TtsHelper, TagManageScreen/ViewModel 共11个文件 |
