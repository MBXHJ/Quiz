package com.quizapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.quizapp.data.db.dao.AnsweredQuestionDao
import com.quizapp.data.db.dao.DailyStatsDao
import com.quizapp.data.db.dao.ExamRecordDao
import com.quizapp.data.db.dao.FavoriteQuestionDao
import com.quizapp.data.db.dao.MarkedQuestionDao
import com.quizapp.data.db.dao.PracticeProgressDao
import com.quizapp.data.db.dao.PracticeRecordDao
import com.quizapp.data.db.dao.QuestionBankDao
import com.quizapp.data.db.dao.QuestionDao
import com.quizapp.data.db.dao.QuestionNoteDao
import com.quizapp.data.db.dao.QuestionTagDao
import com.quizapp.data.db.dao.ReviewScheduleDao
import com.quizapp.data.db.dao.TagDao
import com.quizapp.data.db.dao.WrongRecordDao
import com.quizapp.data.db.entity.AnsweredQuestionEntity
import com.quizapp.data.db.entity.DailyStatsEntity
import com.quizapp.data.db.entity.ExamRecordEntity
import com.quizapp.data.db.entity.FavoriteQuestionEntity
import com.quizapp.data.db.entity.MarkedQuestionEntity
import com.quizapp.data.db.entity.PracticeProgressEntity
import com.quizapp.data.db.entity.PracticeRecordEntity
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.data.db.entity.QuestionNoteEntity
import com.quizapp.data.db.entity.QuestionTagEntity
import com.quizapp.data.db.entity.ReviewScheduleEntity
import com.quizapp.data.db.entity.TagEntity
import com.quizapp.data.db.entity.WrongRecordEntity

@Database(
    entities = [
        QuestionBankEntity::class,
        QuestionEntity::class,
        WrongRecordEntity::class,
        ExamRecordEntity::class,
        AnsweredQuestionEntity::class,
        PracticeProgressEntity::class,
        PracticeRecordEntity::class,
        FavoriteQuestionEntity::class,
        MarkedQuestionEntity::class,
        QuestionNoteEntity::class,
        ReviewScheduleEntity::class,
        DailyStatsEntity::class,
        TagEntity::class,
        QuestionTagEntity::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionBankDao(): QuestionBankDao
    abstract fun questionDao(): QuestionDao
    abstract fun wrongRecordDao(): WrongRecordDao
    abstract fun examRecordDao(): ExamRecordDao
    abstract fun answeredQuestionDao(): AnsweredQuestionDao
    abstract fun practiceProgressDao(): PracticeProgressDao
    abstract fun practiceRecordDao(): PracticeRecordDao
    abstract fun favoriteQuestionDao(): FavoriteQuestionDao
    abstract fun markedQuestionDao(): MarkedQuestionDao
    abstract fun questionNoteDao(): QuestionNoteDao
    abstract fun reviewScheduleDao(): ReviewScheduleDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun tagDao(): TagDao
    abstract fun questionTagDao(): QuestionTagDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS review_schedule (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        questionId INTEGER NOT NULL,
                        stage INTEGER NOT NULL DEFAULT 0,
                        nextReviewDate INTEGER NOT NULL,
                        totalReviews INTEGER NOT NULL DEFAULT 0,
                        lastReviewDate INTEGER NOT NULL,
                        FOREIGN KEY (questionId) REFERENCES questions(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_review_schedule_questionId ON review_schedule(questionId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_review_schedule_nextReviewDate ON review_schedule(nextReviewDate)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_stats (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date TEXT NOT NULL,
                        questionsAnswered INTEGER NOT NULL DEFAULT 0,
                        targetMet INTEGER NOT NULL DEFAULT 0,
                        practiceDuration INTEGER NOT NULL DEFAULT 0
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_daily_stats_date ON daily_stats(date)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        color INTEGER NOT NULL,
                        createdDate INTEGER NOT NULL
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tags_name ON tags(name)")

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS question_tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        questionId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        FOREIGN KEY (questionId) REFERENCES questions(id) ON DELETE CASCADE,
                        FOREIGN KEY (tagId) REFERENCES tags(id) ON DELETE CASCADE
                    )
                """)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_question_tags_questionId_tagId ON question_tags(questionId, tagId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_question_tags_tagId ON question_tags(tagId)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_app.db"
                )
                    .addMigrations(MIGRATION_6_7)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
