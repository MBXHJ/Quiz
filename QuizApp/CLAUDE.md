# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build commands

```bash
# Debug APK (requires JDK 17+)
JAVA_HOME="/c/Program Files/Java/jdk-17.0.19+10" ./gradlew assembleDebug

# Release APK
JAVA_HOME="/c/Program Files/Java/jdk-17.0.19+10" ./gradlew assembleRelease

# Clean
./gradlew clean
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

Copy to project root for deployment:
```bash
cp app/build/outputs/apk/debug/app-debug.apk "../../QuizApp_v{N}.apk"
```

## Architecture overview

Single-module Android app (Jetpack Compose + Material 3) using **MVVM + Repository + Room + Hilt DI**.

### Data layer (`data/`)

- **Room DB** (`data/db/`): 7 entities → 7 DAOs → `AppDatabase` (singleton, version 3, destructive migration). DB name: `quiz_app.db`
- **Entities**: `QuestionBankEntity`, `QuestionEntity`, `WrongRecordEntity`, `ExamRecordEntity`, `AnsweredQuestionEntity`, `PracticeProgressEntity`, `PracticeRecordEntity`
- **Parsers** (`data/parser/`): `TxtParser`, `DocxParser`, `ExcelParser` — read question files into `ParsedQuestion`
- **Repository** (`data/repository/`): `QuizRepository` (all DB operations), `ImportRepository` (file import + asset auto-import)

### DI (`di/`)

- `DatabaseModule`: provides all 7 DAOs
- `RepositoryModule`: empty (Hilt auto-injects `@Singleton` repos)

### UI layer (`ui/`)

- **Navigation**: `NavGraph` + `Screen` sealed class — 6 routes: `bank_list`, `import`, `practice/{bankId}`, `question/{bankId}/{mode}`, `exam/{bankId}`, `exam_result/{bankId}/{score}/{total}/{correct}`, `profile`
- **Theme** (`ui/theme/`): `QuizAppTheme` (light/dark), `Color.kt` (custom blue-gold palette), `Components.kt` (reusable `GradientHeader`, `StatBadge`, `ModeCard`, `TypeTag`, `OptionCard`, `EmptyPlaceholder`)
- **Screens** (each in own package with XxxScreen + XxxViewModel):
  - `home/` — `BankListScreen`: list of imported question banks, FAB to import
  - `practice/` — `PracticeScreen`: mode selection (sequential/resume, exam, random, type, wrong)
  - `question/` — `QuestionScreen`: question display with options (SINGLE/MULTI/JUDGE), answer reveal, jump-to-question grid, finish & save record
  - `exam/` — `ExamScreen`: timed exam with progress; `ExamResultScreen`: score ring + stats
  - `profile/` — `ProfileScreen`: global stats + exam records + practice records
  - `importt/` — `ImportScreen`: file picker + parsing preview

### Key patterns

- All `ViewModel`s use `MutableStateFlow` + `StateFlow` + `asStateFlow()` pattern
- Room DAO methods return `Flow<List<T>>` for reactive queries, `suspend fun` for one-shot
- Auto-import from assets on first launch (in `QuizApp.onCreate`)
- `fallbackToDestructiveMigration()` — DB version bumps clear data

### Modes (`question/{bankId}/{mode}`)

| mode | description |
|------|-------------|
| `sequential` | Ordered by ID, progress saved to `practice_progress` |
| `random` | Shuffled via `ORDER BY RANDOM()` |
| `wrong` | Questions with `wrong_records.isRemoved = 0` |
| `type_SINGLE` / `type_MULTI` / `type_JUDGE` | Filtered by type |
| *(exam mode is separate via `exam/{bankId}`)* | |
