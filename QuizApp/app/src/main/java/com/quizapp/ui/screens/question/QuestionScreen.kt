package com.quizapp.ui.screens.question

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quizapp.data.db.entity.QuestionEntity
import com.quizapp.ui.theme.*
import androidx.compose.foundation.isSystemInDarkTheme

/**
 * Formats elapsed seconds into MM:SS display string.
 */
private fun formatElapsed(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    bankId: Long, mode: String, restart: Boolean = false, recordId: Long = -1L, count: Int = 0, onBack: () -> Unit,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    LaunchedEffect(bankId, mode, restart) { viewModel.loadQuestions(bankId, mode, restart, recordId, count) }
    val s by viewModel.uiState.collectAsState()

    val modeTitle = when {
        mode.startsWith("record_wrong_") -> "错题重做"
        mode == "sequential" -> "顺序练习"
        mode == "random" -> "随机刷题"
        mode == "wrong" -> "错题重做"
        mode.startsWith("type_") -> "题型练习"
        else -> "刷题"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(modeTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = {
                    viewModel.finishPractice(onFinished = onBack)
                }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } },
                actions = {
                    // Favorite toggle
                    if (s.questions.isNotEmpty()) {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                if (s.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                "收藏",
                                tint = if (s.isFavorite) Color(0xFFFFC107) else LocalContentColor.current
                            )
                        }
                    }
                    // Mark toggle
                    if (s.questions.isNotEmpty()) {
                        IconButton(onClick = { viewModel.toggleMark() }) {
                            Icon(
                                if (s.isMarked) Icons.Default.Flag else Icons.Default.OutlinedFlag,
                                "标记",
                                tint = if (s.isMarked) Color(0xFFFF9800) else LocalContentColor.current
                            )
                        }
                    }
                    // Question navigator button
                    if (s.questions.isNotEmpty()) {
                        IconButton(onClick = { viewModel.toggleJumpDialog() }) {
                            Icon(Icons.Default.GridView, "题目列表")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            s.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            s.questions.isEmpty() -> EmptyPlaceholder(Icons.Default.CheckCircle, "暂无题目", modifier = Modifier.fillMaxSize().padding(padding))
            else -> {
                val q = s.questions[s.currentIndex]
                Column(Modifier.fillMaxSize().padding(padding)) {
                    // Progress bar
                    LinearProgressIndicator(
                        progress = { (s.currentIndex + 1).toFloat() / s.questions.size },
                        modifier = Modifier.fillMaxWidth().height(3.dp),
                        color = Primary, trackColor = BorderLight
                    )

                    // Stats bar + timer
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), Arrangement.SpaceBetween) {
                            Text("已答 ${s.answeredCount}/${s.questions.size}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            if (s.answeredCount > 0) {
                                Text("正确 ${s.correctCount}/${s.answeredCount}", style = MaterialTheme.typography.labelSmall, color = CorrectGreen)
                            }
                        }
                    }

                    // Timer display
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)) {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                null,
                                Modifier.size(14.dp),
                                tint = TextSecondary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                formatElapsed(s.elapsedSeconds),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    // Scrollable content
                    Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                        // Meta bar
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(10.dp), color = PrimaryContainer) {
                                Text("${s.currentIndex + 1} / ${s.questions.size}", Modifier.padding(horizontal = 12.dp, vertical = 5.dp), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = OnPrimaryContainer)
                            }
                            TypeTag(q.questionType)
                        }
                        Spacer(Modifier.height(14.dp))
                        // Question card
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                            Column(Modifier.padding(16.dp)) {
                                Text(q.content, style = MaterialTheme.typography.bodyLarge, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight)
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        // Options
                        when (q.questionType) {
                            "SINGLE" -> {
                                val opts = q.options.split("|||").filter { it.isNotBlank() }
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    opts.forEach { opt ->
                                        val letter = opt.substringBefore(".")
                                        val txt = opt.substringAfter(".")
                                        OptionCard(
                                            selected = s.selectedAnswer == letter,
                                            correct = letter == q.answer,
                                            showResult = s.showResult,
                                            letter = letter, text = txt,
                                            onClick = { viewModel.selectAnswer(letter) }
                                        )
                                    }
                                }
                            }
                            "MULTI" -> {
                                val opts = q.options.split("|||").filter { it.isNotBlank() }
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    opts.forEach { opt ->
                                        val letter = opt.substringBefore(".")
                                        val txt = opt.substringAfter(".")
                                        OptionCard(
                                            selected = letter in s.isMultiSelected,
                                            correct = letter in q.answer,
                                            showResult = s.showResult,
                                            letter = letter, text = txt,
                                            isCheckbox = true,
                                            onClick = { if (!s.showResult) viewModel.selectAnswer(letter) }
                                        )
                                    }
                                    if (!s.showResult && s.isMultiSelected.isNotEmpty()) {
                                        Spacer(Modifier.height(4.dp))
                                        Button(
                                            onClick = { viewModel.confirmMultiSelect() },
                                            modifier = Modifier.fillMaxWidth().height(50.dp),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                                        ) { Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("确认提交", fontWeight = FontWeight.SemiBold) }
                                    }
                                }
                            }
                            "JUDGE" -> {
                                val isDark = isSystemInDarkTheme()
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                    listOf("正确" to Icons.Default.ThumbUp, "错误" to Icons.Default.ThumbDown).forEach { (v, ic) ->
                                        val sel = s.selectedAnswer == v
                                        val bg = when { s.showResult && v == q.answer -> CorrectGreenBg; s.showResult && sel && v != q.answer -> WrongRedBg; sel -> PrimaryContainer; else -> if (isDark) DarkCard else SurfaceVariant }
                                        val bd = when { s.showResult && v == q.answer -> BorderStroke(2.dp, CorrectGreen.copy(alpha = 0.6f)); s.showResult && sel && v != q.answer -> BorderStroke(2.dp, WrongRed.copy(alpha = 0.6f)); sel && !s.showResult -> BorderStroke(2.dp, Primary); else -> null }
                                        Card(
                                            onClick = { viewModel.selectAnswer(v) },
                                            modifier = Modifier.weight(1f).height(76.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            border = bd,
                                            colors = CardDefaults.cardColors(containerColor = bg),
                                            elevation = CardDefaults.cardElevation(defaultElevation = if (sel) 2.dp else 0.dp)
                                        ) {
                                            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                                Icon(ic, null, Modifier.size(26.dp), tint = if (sel) Primary else if (isDark) DarkTextSecondary else TextSecondary)
                                                Spacer(Modifier.height(4.dp))
                                                Text(v, style = MaterialTheme.typography.titleSmall, fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium, color = if (isDark && !sel) DarkTextPrimary else Color.Unspecified)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // Result panel
                        AnimatedVisibility(s.showResult, enter = fadeIn() + slideInVertically { it / 4 } + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                            ResultPanel(q, s)
                        }
                    }
                    // Bottom nav
                    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 6.dp, color = MaterialTheme.colorScheme.surface) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { viewModel.previousQuestion() }, enabled = s.currentIndex > 0,
                                shape = RoundedCornerShape(10.dp), modifier = Modifier.height(46.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("上一题") }
                            Button(
                                onClick = { viewModel.nextQuestion() },
                                enabled = s.currentIndex < s.questions.size - 1,
                                shape = RoundedCornerShape(10.dp), modifier = Modifier.height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
                            ) { Text("下一题"); Spacer(Modifier.width(4.dp)); Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp)) }
                        }
                    }
                }
            }
        }
    }

    // ═══ Jump-to-question dialog ═══
    if (s.showJumpDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleJumpDialog() },
            shape = RoundedCornerShape(20.dp),
            title = {
                Column {
                    Text("题目列表", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("已答 ${s.answeredCount}/${s.questions.size} · 正确 ${s.correctCount}",
                        style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            },
            text = {
                val columns = 5
                val rows = (s.questions.size + columns - 1) / columns
                val gridHeight = (rows * 52 + (rows - 1) * 8).dp
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.height(gridHeight).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(s.questions.indices.toList()) { idx ->
                        val isAnswered = idx in s.answeredSet
                        val isCurrent = idx == s.currentIndex
                        Surface(
                            onClick = { viewModel.jumpToQuestion(idx) },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = when {
                                isCurrent -> Primary
                                isAnswered -> CorrectGreen.copy(alpha = 0.15f)
                                else -> SurfaceVariant
                            },
                            border = if (isCurrent) null else if (isAnswered) BorderStroke(1.5.dp, CorrectGreen.copy(alpha = 0.4f)) else null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${idx + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) Color.White else if (isAnswered) CorrectGreen else TextSecondary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.finishPractice(onFinished = onBack) }) {
                    Text("完成练习并保存", color = Primary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = { TextButton(onClick = { viewModel.toggleJumpDialog() }) { Text("关闭") } }
        )
    }
}

@Composable
private fun ResultPanel(q: QuestionEntity, s: QuestionUiState) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = if (s.isCorrect) CorrectGreenBg else WrongRedBg),
        border = BorderStroke(1.dp, if (s.isCorrect) CorrectGreen.copy(alpha = 0.25f) else WrongRed.copy(alpha = 0.25f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = if (s.isCorrect) CorrectGreen.copy(alpha = 0.12f) else WrongRed.copy(alpha = 0.12f), modifier = Modifier.size(34.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(if (s.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel, null, tint = if (s.isCorrect) CorrectGreen else WrongRed, modifier = Modifier.size(20.dp)) }
                }
                Spacer(Modifier.width(10.dp))
                Text(if (s.isCorrect) "回答正确！" else "回答错误", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (s.isCorrect) CorrectGreen else WrongRed)
            }
            Spacer(Modifier.height(10.dp)); HorizontalDivider(color = Border)
            Spacer(Modifier.height(10.dp))
            Row {
                Surface(shape = RoundedCornerShape(6.dp), color = CorrectGreen.copy(alpha = 0.1f)) { Text("正确答案", Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = CorrectGreen, fontWeight = FontWeight.SemiBold) }
                Spacer(Modifier.width(8.dp))
                Text(q.answer, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            if (q.analysis.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), color = Background) {
                    Row(Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Lightbulb, null, Modifier.size(16.dp), tint = WarningOrange)
                        Spacer(Modifier.width(8.dp))
                        Text(q.analysis, style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f))
                    }
                }
            }
            if (s.wrongCount > 0) {
                Spacer(Modifier.height(6.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = WrongRed.copy(alpha = 0.06f)) { Text("本题累计错 ${s.wrongCount} 次", Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = WrongRed) }
            }
        }
    }
}
