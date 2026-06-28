package com.quizapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quizapp.ui.screens.exam.ExamResultScreen
import com.quizapp.ui.screens.exam.ExamScreen
import com.quizapp.ui.screens.home.BankListScreen
import com.quizapp.ui.screens.importt.ImportScreen
import com.quizapp.ui.screens.practice.PracticeScreen
import com.quizapp.ui.screens.profile.ProfileScreen
import com.quizapp.ui.screens.question.QuestionScreen
import com.quizapp.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.BankList.route
    ) {
        composable(Screen.BankList.route) {
            BankListScreen(
                onBankClick = { bankId ->
                    navController.navigate(Screen.Practice.createRoute(bankId))
                },
                onImportClick = {
                    navController.navigate(Screen.Import.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.Import.route) {
            ImportScreen(
                onBack = { navController.popBackStack() },
                onImportComplete = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Practice.route,
            arguments = listOf(navArgument("bankId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bankId = backStackEntry.arguments?.getLong("bankId") ?: return@composable
            PracticeScreen(
                bankId = bankId,
                onBack = { navController.popBackStack() },
                onStartPractice = { mode ->
                    if (mode == "restart_sequential") {
                        navController.navigate(Screen.Question.createRoute(bankId, "sequential", restart = true))
                    } else {
                        navController.navigate(Screen.Question.createRoute(bankId, mode))
                    }
                },
                onStartRandom = { count ->
                    navController.navigate(Screen.Question.createRoute(bankId, "random", count = count))
                },
                onStartExam = {
                    navController.navigate(Screen.Exam.createRoute(bankId))
                }
            )
        }

        composable(
            route = Screen.Question.route,
            arguments = listOf(
                navArgument("bankId") { type = NavType.LongType },
                navArgument("mode") { type = NavType.StringType },
                navArgument("restart") { type = NavType.BoolType; defaultValue = false },
                navArgument("recordId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("count") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val bankId = backStackEntry.arguments?.getLong("bankId") ?: return@composable
            val mode = backStackEntry.arguments?.getString("mode") ?: return@composable
            val restart = backStackEntry.arguments?.getBoolean("restart") ?: false
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: -1L
            val count = backStackEntry.arguments?.getInt("count") ?: 0
            QuestionScreen(
                bankId = bankId,
                mode = mode,
                restart = restart,
                recordId = recordId,
                count = count,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Exam.route,
            arguments = listOf(navArgument("bankId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bankId = backStackEntry.arguments?.getLong("bankId") ?: return@composable
            ExamScreen(
                bankId = bankId,
                onBack = { navController.popBackStack() },
                onFinish = { score, total, correct, examRecordId ->
                    navController.navigate(Screen.ExamResult.createRoute(bankId, score, total, correct, examRecordId)) {
                        popUpTo(Screen.Exam.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.ExamResult.route,
            arguments = listOf(
                navArgument("bankId") { type = NavType.LongType },
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("correct") { type = NavType.IntType },
                navArgument("examRecordId") { type = NavType.LongType; defaultValue = 0L }
            )
        ) { backStackEntry ->
            val bankId = backStackEntry.arguments?.getLong("bankId") ?: return@composable
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            val correct = backStackEntry.arguments?.getInt("correct") ?: 0
            val examRecordId = backStackEntry.arguments?.getLong("examRecordId") ?: 0L
            ExamResultScreen(
                score = score,
                total = total,
                correct = correct,
                bankId = bankId,
                examRecordId = examRecordId,
                onBack = {
                    navController.popBackStack()
                },
                onReviewWrong = {
                    navController.navigate(Screen.Question.createRoute(bankId, "exam_review_$examRecordId")) {
                        popUpTo(Screen.BankList.route)
                    }
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onRestartPractice = { bankId, mode, recordId ->
                    navController.navigate(Screen.Question.createRoute(bankId, mode, recordId = recordId)) {
                        popUpTo(Screen.BankList.route)
                    }
                },
                onExamRecordClick = { bankId, score, total, correct, examRecordId ->
                    navController.navigate(Screen.ExamResult.createRoute(bankId, score, total, correct, examRecordId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
