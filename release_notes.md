## v1.8.1 更新内容

### 🐛 修复

- **填空题正确答案不显示**：FillResultPanel 被死代码屏蔽，AnimatedVisibility 外层 if guard 错误跳过 FILL 类型
- **双卡片重复**：内联卡片和 FillResultPanel 同时渲染，正确答案文字在 Row 内被裁剪
- **深色模式答案不可见**：color = Color.Unspecified → 改用蓝色 Color(0xFF2563EB)
- **数据库重建**：v7 → v8 → v9，触发 fallbackToDestructiveMigration 确保新题库正确入库
- **填空题即输即交**：改为文本编辑 + 按钮确认提交

## v1.8 更新内容

### ✨ 新增功能

- **📝 填空题类型支持**：新增 FILL 类型，紫色标签，文本输入+提交，答错显示参考答案
- **📚 操作系统原理与应用题库**：规范化内置题库 879 题（单选489 + 多选235 + 判断147 + 填空8）
- **🎯 题型分类新增填空题**：练习模式选择页、题型分类入口增加填空题选项

### 🐛 修复

- DocxParser 重写：ZIP→XML→段落解析，支持标黄答案、3种答案格式（正确答案/参考答案/正确的答案是）
- 导入文件 Content URI 无扩展名报错 → 通过 ContentResolver 查询 DISPLAY_NAME
- 题库 <10 题闪退 → coerceIn 下限防御性修正
- containsMatchIn 替代 matches() 修复选项被吞
- 判断题对/错归一化为正确/错误
- 填空题误入判断题/多选题 → 无选项+文本答案+空白标记 → FILL 类型
- 选项跨段拼接安全阈值收紧

### 🛠 涉及模块

| 模块 | 变更 |
|------|------|
| 解析器 | DocxParser 重写，TxtParser 支持 FILL/参考答案/嵌入式判断 |
| 版本 | 1.6 → 1.8，versionCode 5 → 6 |
| 题库 | 新增操作系统原理与应用_规范化.txt（879题） |
| UI | FillResultPanel 组件，TypeTag 紫色填空标签 |
| PracticeScreen | 题型分类新增填空题 |
| AnswerUtils | 新增 FILL 类型答案比对 |
