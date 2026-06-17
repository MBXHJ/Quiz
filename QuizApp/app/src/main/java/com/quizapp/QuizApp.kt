package com.quizapp

import android.app.Application
import com.quizapp.data.repository.ImportRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class QuizApp : Application() {

    @Inject
    lateinit var importRepository: ImportRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            if (importRepository.isDatabaseEmpty()) {
                importRepository.importFromAssets(
                    "人工智能训练师理论题1000题版本.txt",
                    "人工智能训练师(1000题)"
                )
                importRepository.importFromAssets(
                    "shiti_1432.txt",
                    "人工智能训练师(1432题)"
                )
                importRepository.importFromAssets(
                    "人工智能训练师理论题1200题.txt",
                    "人工智能训练师三级(1200题)"
                )
            }
        }
    }
}
