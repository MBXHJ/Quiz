package com.quizapp.ui.navigation

sealed class Screen(val route: String) {
    data object BankList : Screen("bank_list")
    data object Import : Screen("import")
    data object Practice : Screen("practice/{bankId}") {
        fun createRoute(bankId: Long) = "practice/$bankId"
    }
    data object Question : Screen("question/{bankId}/{mode}?restart={restart}&recordId={recordId}&count={count}") {
        fun createRoute(bankId: Long, mode: String, restart: Boolean = false, recordId: Long = -1L, count: Int = 0) =
            "question/$bankId/$mode?restart=$restart&recordId=$recordId&count=$count"
    }
    data object Exam : Screen("exam/{bankId}") {
        fun createRoute(bankId: Long) = "exam/$bankId"
    }
    data object ExamResult : Screen("exam_result/{bankId}/{score}/{total}/{correct}") {
        fun createRoute(bankId: Long, score: Int, total: Int, correct: Int): String {
            return "exam_result/$bankId/$score/$total/$correct"
        }
    }
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
}
