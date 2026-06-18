package com.quizapp.di

import android.content.Context
import com.quizapp.data.db.AppDatabase
import com.quizapp.data.db.dao.AnsweredQuestionDao
import com.quizapp.data.db.dao.ExamRecordDao
import com.quizapp.data.db.dao.FavoriteQuestionDao
import com.quizapp.data.db.dao.MarkedQuestionDao
import com.quizapp.data.db.dao.PracticeProgressDao
import com.quizapp.data.db.dao.PracticeRecordDao
import com.quizapp.data.db.dao.QuestionBankDao
import com.quizapp.data.db.dao.QuestionDao
import com.quizapp.data.db.dao.WrongRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideQuestionBankDao(db: AppDatabase): QuestionBankDao = db.questionBankDao()

    @Provides
    fun provideQuestionDao(db: AppDatabase): QuestionDao = db.questionDao()

    @Provides
    fun provideWrongRecordDao(db: AppDatabase): WrongRecordDao = db.wrongRecordDao()

    @Provides
    fun provideExamRecordDao(db: AppDatabase): ExamRecordDao = db.examRecordDao()

    @Provides
    fun provideAnsweredQuestionDao(db: AppDatabase): AnsweredQuestionDao = db.answeredQuestionDao()

    @Provides
    fun providePracticeProgressDao(db: AppDatabase): PracticeProgressDao = db.practiceProgressDao()

    @Provides
    fun providePracticeRecordDao(db: AppDatabase): PracticeRecordDao = db.practiceRecordDao()

    @Provides
    fun provideFavoriteQuestionDao(db: AppDatabase): FavoriteQuestionDao = db.favoriteQuestionDao()

    @Provides
    fun provideMarkedQuestionDao(db: AppDatabase): MarkedQuestionDao = db.markedQuestionDao()
}
