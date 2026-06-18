package com.quizapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.quizapp.data.db.dao.AnsweredQuestionDao
import com.quizapp.data.db.dao.ExamRecordDao
import com.quizapp.data.db.dao.FavoriteQuestionDao
import com.quizapp.data.db.dao.MarkedQuestionDao
import com.quizapp.data.db.dao.PracticeProgressDao
import com.quizapp.data.db.dao.PracticeRecordDao
import com.quizapp.data.db.dao.QuestionBankDao
import com.quizapp.data.db.dao.QuestionDao
import com.quizapp.data.db.dao.WrongRecordDao
import com.quizapp.data.db.entity.AnsweredQuestionEntity
import com.quizapp.data.db.entity.ExamRecordEntity
import com.quizapp.data.db.entity.FavoriteQuestionEntity
import com.quizapp.data.db.entity.MarkedQuestionEntity
import com.quizapp.data.db.entity.PracticeProgressEntity
import com.quizapp.data.db.entity.PracticeRecordEntity
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.data.db.entity.QuestionEntity
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
        MarkedQuestionEntity::class
    ],
    version = 5,
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

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_app.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
