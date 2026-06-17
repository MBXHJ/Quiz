package com.quizapp.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.quizapp.data.db.dao.AnsweredQuestionDao;
import com.quizapp.data.db.dao.AnsweredQuestionDao_Impl;
import com.quizapp.data.db.dao.ExamRecordDao;
import com.quizapp.data.db.dao.ExamRecordDao_Impl;
import com.quizapp.data.db.dao.PracticeProgressDao;
import com.quizapp.data.db.dao.PracticeProgressDao_Impl;
import com.quizapp.data.db.dao.PracticeRecordDao;
import com.quizapp.data.db.dao.PracticeRecordDao_Impl;
import com.quizapp.data.db.dao.QuestionBankDao;
import com.quizapp.data.db.dao.QuestionBankDao_Impl;
import com.quizapp.data.db.dao.QuestionDao;
import com.quizapp.data.db.dao.QuestionDao_Impl;
import com.quizapp.data.db.dao.WrongRecordDao;
import com.quizapp.data.db.dao.WrongRecordDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile QuestionBankDao _questionBankDao;

  private volatile QuestionDao _questionDao;

  private volatile WrongRecordDao _wrongRecordDao;

  private volatile ExamRecordDao _examRecordDao;

  private volatile AnsweredQuestionDao _answeredQuestionDao;

  private volatile PracticeProgressDao _practiceProgressDao;

  private volatile PracticeRecordDao _practiceRecordDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `question_banks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `questionCount` INTEGER NOT NULL, `importDate` INTEGER NOT NULL, `examConfig` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bankId` INTEGER NOT NULL, `questionType` TEXT NOT NULL, `content` TEXT NOT NULL, `options` TEXT NOT NULL, `answer` TEXT NOT NULL, `analysis` TEXT NOT NULL, FOREIGN KEY(`bankId`) REFERENCES `question_banks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_questions_bankId` ON `questions` (`bankId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `wrong_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `questionId` INTEGER NOT NULL, `wrongCount` INTEGER NOT NULL, `lastWrongTime` INTEGER NOT NULL, `isRemoved` INTEGER NOT NULL, FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_wrong_records_questionId` ON `wrong_records` (`questionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exam_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bankId` INTEGER NOT NULL, `score` INTEGER NOT NULL, `totalCount` INTEGER NOT NULL, `correctCount` INTEGER NOT NULL, `questionDetails` TEXT NOT NULL, `examDate` INTEGER NOT NULL, FOREIGN KEY(`bankId`) REFERENCES `question_banks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_exam_records_bankId` ON `exam_records` (`bankId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `answered_questions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `questionId` INTEGER NOT NULL, `bankId` INTEGER NOT NULL, `answeredDate` INTEGER NOT NULL, FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`bankId`) REFERENCES `question_banks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_answered_questions_questionId` ON `answered_questions` (`questionId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_answered_questions_bankId` ON `answered_questions` (`bankId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `practice_progress` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bankId` INTEGER NOT NULL, `mode` TEXT NOT NULL, `currentIndex` INTEGER NOT NULL, `totalQuestions` INTEGER NOT NULL, `answeredCount` INTEGER NOT NULL, `correctCount` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, FOREIGN KEY(`bankId`) REFERENCES `question_banks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_practice_progress_bankId_mode` ON `practice_progress` (`bankId`, `mode`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `practice_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bankId` INTEGER NOT NULL, `mode` TEXT NOT NULL, `modeLabel` TEXT NOT NULL, `totalCount` INTEGER NOT NULL, `answeredCount` INTEGER NOT NULL, `correctCount` INTEGER NOT NULL, `wrongCount` INTEGER NOT NULL, `isCompleted` INTEGER NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, FOREIGN KEY(`bankId`) REFERENCES `question_banks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_practice_records_bankId` ON `practice_records` (`bankId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '31ec4610101a594323de3cda0c97dbcc')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `question_banks`");
        db.execSQL("DROP TABLE IF EXISTS `questions`");
        db.execSQL("DROP TABLE IF EXISTS `wrong_records`");
        db.execSQL("DROP TABLE IF EXISTS `exam_records`");
        db.execSQL("DROP TABLE IF EXISTS `answered_questions`");
        db.execSQL("DROP TABLE IF EXISTS `practice_progress`");
        db.execSQL("DROP TABLE IF EXISTS `practice_records`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsQuestionBanks = new HashMap<String, TableInfo.Column>(6);
        _columnsQuestionBanks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestionBanks.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestionBanks.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestionBanks.put("questionCount", new TableInfo.Column("questionCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestionBanks.put("importDate", new TableInfo.Column("importDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestionBanks.put("examConfig", new TableInfo.Column("examConfig", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuestionBanks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQuestionBanks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQuestionBanks = new TableInfo("question_banks", _columnsQuestionBanks, _foreignKeysQuestionBanks, _indicesQuestionBanks);
        final TableInfo _existingQuestionBanks = TableInfo.read(db, "question_banks");
        if (!_infoQuestionBanks.equals(_existingQuestionBanks)) {
          return new RoomOpenHelper.ValidationResult(false, "question_banks(com.quizapp.data.db.entity.QuestionBankEntity).\n"
                  + " Expected:\n" + _infoQuestionBanks + "\n"
                  + " Found:\n" + _existingQuestionBanks);
        }
        final HashMap<String, TableInfo.Column> _columnsQuestions = new HashMap<String, TableInfo.Column>(7);
        _columnsQuestions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("bankId", new TableInfo.Column("bankId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("questionType", new TableInfo.Column("questionType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("options", new TableInfo.Column("options", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("answer", new TableInfo.Column("answer", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("analysis", new TableInfo.Column("analysis", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuestions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysQuestions.add(new TableInfo.ForeignKey("question_banks", "CASCADE", "NO ACTION", Arrays.asList("bankId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesQuestions = new HashSet<TableInfo.Index>(1);
        _indicesQuestions.add(new TableInfo.Index("index_questions_bankId", false, Arrays.asList("bankId"), Arrays.asList("ASC")));
        final TableInfo _infoQuestions = new TableInfo("questions", _columnsQuestions, _foreignKeysQuestions, _indicesQuestions);
        final TableInfo _existingQuestions = TableInfo.read(db, "questions");
        if (!_infoQuestions.equals(_existingQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "questions(com.quizapp.data.db.entity.QuestionEntity).\n"
                  + " Expected:\n" + _infoQuestions + "\n"
                  + " Found:\n" + _existingQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsWrongRecords = new HashMap<String, TableInfo.Column>(5);
        _columnsWrongRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWrongRecords.put("questionId", new TableInfo.Column("questionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWrongRecords.put("wrongCount", new TableInfo.Column("wrongCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWrongRecords.put("lastWrongTime", new TableInfo.Column("lastWrongTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWrongRecords.put("isRemoved", new TableInfo.Column("isRemoved", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWrongRecords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWrongRecords.add(new TableInfo.ForeignKey("questions", "CASCADE", "NO ACTION", Arrays.asList("questionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWrongRecords = new HashSet<TableInfo.Index>(1);
        _indicesWrongRecords.add(new TableInfo.Index("index_wrong_records_questionId", true, Arrays.asList("questionId"), Arrays.asList("ASC")));
        final TableInfo _infoWrongRecords = new TableInfo("wrong_records", _columnsWrongRecords, _foreignKeysWrongRecords, _indicesWrongRecords);
        final TableInfo _existingWrongRecords = TableInfo.read(db, "wrong_records");
        if (!_infoWrongRecords.equals(_existingWrongRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "wrong_records(com.quizapp.data.db.entity.WrongRecordEntity).\n"
                  + " Expected:\n" + _infoWrongRecords + "\n"
                  + " Found:\n" + _existingWrongRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsExamRecords = new HashMap<String, TableInfo.Column>(7);
        _columnsExamRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamRecords.put("bankId", new TableInfo.Column("bankId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamRecords.put("score", new TableInfo.Column("score", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamRecords.put("totalCount", new TableInfo.Column("totalCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamRecords.put("correctCount", new TableInfo.Column("correctCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamRecords.put("questionDetails", new TableInfo.Column("questionDetails", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExamRecords.put("examDate", new TableInfo.Column("examDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExamRecords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysExamRecords.add(new TableInfo.ForeignKey("question_banks", "CASCADE", "NO ACTION", Arrays.asList("bankId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesExamRecords = new HashSet<TableInfo.Index>(1);
        _indicesExamRecords.add(new TableInfo.Index("index_exam_records_bankId", false, Arrays.asList("bankId"), Arrays.asList("ASC")));
        final TableInfo _infoExamRecords = new TableInfo("exam_records", _columnsExamRecords, _foreignKeysExamRecords, _indicesExamRecords);
        final TableInfo _existingExamRecords = TableInfo.read(db, "exam_records");
        if (!_infoExamRecords.equals(_existingExamRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "exam_records(com.quizapp.data.db.entity.ExamRecordEntity).\n"
                  + " Expected:\n" + _infoExamRecords + "\n"
                  + " Found:\n" + _existingExamRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsAnsweredQuestions = new HashMap<String, TableInfo.Column>(4);
        _columnsAnsweredQuestions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnsweredQuestions.put("questionId", new TableInfo.Column("questionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnsweredQuestions.put("bankId", new TableInfo.Column("bankId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnsweredQuestions.put("answeredDate", new TableInfo.Column("answeredDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAnsweredQuestions = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysAnsweredQuestions.add(new TableInfo.ForeignKey("questions", "CASCADE", "NO ACTION", Arrays.asList("questionId"), Arrays.asList("id")));
        _foreignKeysAnsweredQuestions.add(new TableInfo.ForeignKey("question_banks", "CASCADE", "NO ACTION", Arrays.asList("bankId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAnsweredQuestions = new HashSet<TableInfo.Index>(2);
        _indicesAnsweredQuestions.add(new TableInfo.Index("index_answered_questions_questionId", true, Arrays.asList("questionId"), Arrays.asList("ASC")));
        _indicesAnsweredQuestions.add(new TableInfo.Index("index_answered_questions_bankId", false, Arrays.asList("bankId"), Arrays.asList("ASC")));
        final TableInfo _infoAnsweredQuestions = new TableInfo("answered_questions", _columnsAnsweredQuestions, _foreignKeysAnsweredQuestions, _indicesAnsweredQuestions);
        final TableInfo _existingAnsweredQuestions = TableInfo.read(db, "answered_questions");
        if (!_infoAnsweredQuestions.equals(_existingAnsweredQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "answered_questions(com.quizapp.data.db.entity.AnsweredQuestionEntity).\n"
                  + " Expected:\n" + _infoAnsweredQuestions + "\n"
                  + " Found:\n" + _existingAnsweredQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsPracticeProgress = new HashMap<String, TableInfo.Column>(8);
        _columnsPracticeProgress.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeProgress.put("bankId", new TableInfo.Column("bankId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeProgress.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeProgress.put("currentIndex", new TableInfo.Column("currentIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeProgress.put("totalQuestions", new TableInfo.Column("totalQuestions", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeProgress.put("answeredCount", new TableInfo.Column("answeredCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeProgress.put("correctCount", new TableInfo.Column("correctCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeProgress.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPracticeProgress = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPracticeProgress.add(new TableInfo.ForeignKey("question_banks", "CASCADE", "NO ACTION", Arrays.asList("bankId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPracticeProgress = new HashSet<TableInfo.Index>(1);
        _indicesPracticeProgress.add(new TableInfo.Index("index_practice_progress_bankId_mode", true, Arrays.asList("bankId", "mode"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoPracticeProgress = new TableInfo("practice_progress", _columnsPracticeProgress, _foreignKeysPracticeProgress, _indicesPracticeProgress);
        final TableInfo _existingPracticeProgress = TableInfo.read(db, "practice_progress");
        if (!_infoPracticeProgress.equals(_existingPracticeProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "practice_progress(com.quizapp.data.db.entity.PracticeProgressEntity).\n"
                  + " Expected:\n" + _infoPracticeProgress + "\n"
                  + " Found:\n" + _existingPracticeProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsPracticeRecords = new HashMap<String, TableInfo.Column>(11);
        _columnsPracticeRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("bankId", new TableInfo.Column("bankId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("modeLabel", new TableInfo.Column("modeLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("totalCount", new TableInfo.Column("totalCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("answeredCount", new TableInfo.Column("answeredCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("correctCount", new TableInfo.Column("correctCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("wrongCount", new TableInfo.Column("wrongCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("startTime", new TableInfo.Column("startTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPracticeRecords.put("endTime", new TableInfo.Column("endTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPracticeRecords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPracticeRecords.add(new TableInfo.ForeignKey("question_banks", "CASCADE", "NO ACTION", Arrays.asList("bankId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPracticeRecords = new HashSet<TableInfo.Index>(1);
        _indicesPracticeRecords.add(new TableInfo.Index("index_practice_records_bankId", false, Arrays.asList("bankId"), Arrays.asList("ASC")));
        final TableInfo _infoPracticeRecords = new TableInfo("practice_records", _columnsPracticeRecords, _foreignKeysPracticeRecords, _indicesPracticeRecords);
        final TableInfo _existingPracticeRecords = TableInfo.read(db, "practice_records");
        if (!_infoPracticeRecords.equals(_existingPracticeRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "practice_records(com.quizapp.data.db.entity.PracticeRecordEntity).\n"
                  + " Expected:\n" + _infoPracticeRecords + "\n"
                  + " Found:\n" + _existingPracticeRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "31ec4610101a594323de3cda0c97dbcc", "66adcc2d65a5401cafaa371dd3a30ce9");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "question_banks","questions","wrong_records","exam_records","answered_questions","practice_progress","practice_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `question_banks`");
      _db.execSQL("DELETE FROM `questions`");
      _db.execSQL("DELETE FROM `wrong_records`");
      _db.execSQL("DELETE FROM `exam_records`");
      _db.execSQL("DELETE FROM `answered_questions`");
      _db.execSQL("DELETE FROM `practice_progress`");
      _db.execSQL("DELETE FROM `practice_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(QuestionBankDao.class, QuestionBankDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QuestionDao.class, QuestionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WrongRecordDao.class, WrongRecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExamRecordDao.class, ExamRecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AnsweredQuestionDao.class, AnsweredQuestionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PracticeProgressDao.class, PracticeProgressDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PracticeRecordDao.class, PracticeRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public QuestionBankDao questionBankDao() {
    if (_questionBankDao != null) {
      return _questionBankDao;
    } else {
      synchronized(this) {
        if(_questionBankDao == null) {
          _questionBankDao = new QuestionBankDao_Impl(this);
        }
        return _questionBankDao;
      }
    }
  }

  @Override
  public QuestionDao questionDao() {
    if (_questionDao != null) {
      return _questionDao;
    } else {
      synchronized(this) {
        if(_questionDao == null) {
          _questionDao = new QuestionDao_Impl(this);
        }
        return _questionDao;
      }
    }
  }

  @Override
  public WrongRecordDao wrongRecordDao() {
    if (_wrongRecordDao != null) {
      return _wrongRecordDao;
    } else {
      synchronized(this) {
        if(_wrongRecordDao == null) {
          _wrongRecordDao = new WrongRecordDao_Impl(this);
        }
        return _wrongRecordDao;
      }
    }
  }

  @Override
  public ExamRecordDao examRecordDao() {
    if (_examRecordDao != null) {
      return _examRecordDao;
    } else {
      synchronized(this) {
        if(_examRecordDao == null) {
          _examRecordDao = new ExamRecordDao_Impl(this);
        }
        return _examRecordDao;
      }
    }
  }

  @Override
  public AnsweredQuestionDao answeredQuestionDao() {
    if (_answeredQuestionDao != null) {
      return _answeredQuestionDao;
    } else {
      synchronized(this) {
        if(_answeredQuestionDao == null) {
          _answeredQuestionDao = new AnsweredQuestionDao_Impl(this);
        }
        return _answeredQuestionDao;
      }
    }
  }

  @Override
  public PracticeProgressDao practiceProgressDao() {
    if (_practiceProgressDao != null) {
      return _practiceProgressDao;
    } else {
      synchronized(this) {
        if(_practiceProgressDao == null) {
          _practiceProgressDao = new PracticeProgressDao_Impl(this);
        }
        return _practiceProgressDao;
      }
    }
  }

  @Override
  public PracticeRecordDao practiceRecordDao() {
    if (_practiceRecordDao != null) {
      return _practiceRecordDao;
    } else {
      synchronized(this) {
        if(_practiceRecordDao == null) {
          _practiceRecordDao = new PracticeRecordDao_Impl(this);
        }
        return _practiceRecordDao;
      }
    }
  }
}
