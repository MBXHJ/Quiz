package com.quizapp.di

import com.quizapp.data.repository.ImportRepository
import com.quizapp.data.repository.QuizRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule
