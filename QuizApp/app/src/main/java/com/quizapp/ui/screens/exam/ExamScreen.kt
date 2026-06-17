package com.quizapp.ui.screens.exam

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quizapp.ui.theme.*
import androidx.compose.foundation.isSystemInDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    bankId: Long, onBack: () -> Unit,
    onFinish: (score: Int, total: Int, correct: Int) -> Unit,
    viewModel: ExamViewModel = hiltViewModel()
) {
    LaunchedEffect(bankId) { viewModel.loadExam(bankId) }
    val s by viewModel.uiState.collectAsState()
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("模拟考试", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } },
                actions = {
                    val answered = s.questions.count { q -> if (q.questionType == "MULTI") s.multiSelections.containsKey(q.id) else s.answers.containsKey(q.id) }
                    Surface(shape = RoundedCornerShape(20.dp), color = Primary.copy(alpha = 0.10f)) {
                        TextButton(onClick = { if (answered == s.questions.size) viewModel.finishExam(onFinish) else showConfirm = true }) {
                            Text("交卷 ($answered/${s.questions.size})", color = Primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { padding ->
        if (s.questions.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
        else {
            val q = s.questions[s.currentIndex]
            val answered = s.questions.count { qq -> if (qq.questionType == "MULTI") s.multiSelections.containsKey(qq.id) else s.answers.containsKey(qq.id) }

            Column(Modifier.fillMaxSize().padding(padding)) {
                val isDark = isSystemInDarkTheme()
                // ── Exam progress header ──
                Surface(color = PrimaryContainer.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(8.dp), color = Primary.copy(alpha = 0.12f)) { Text("考试", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Primary, fontWeight = FontWeight.SemiBold) }
                                Spacer(Modifier.width(8.dp))
                                Text("${s.currentIndex + 1}/${s.questions.size}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = Primary.copy(alpha = 0.10f)) { Text("已答 $answered/${s.questions.size}", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Primary, fontWeight = FontWeight.Medium) }
                        }
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(progress = { (s.currentIndex + 1).toFloat() / s.questions.size }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = Primary, trackColor = SurfaceVariant)
                    }
                }

                // ── Question body ──
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                    // Question card
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(q.content, style = MaterialTheme.typography.bodyLarge, lineHeight = MaterialTheme.typography.bodyLarge.lineHeight)
                            Spacer(Modifier.height(8.dp))
                            TypeTag(q.questionType)
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
                                    val sel = s.answers[q.id] == letter
                                    Card(
                                        onClick = { viewModel.selectAnswer(q.id, letter) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        border = if (sel) BorderStroke(2.dp, Primary) else null,
                                        colors = CardDefaults.cardColors(containerColor = if (sel) { if (isDark) Primary.copy(alpha = 0.20f) else PrimaryContainer } else { if (isDark) DarkCard else SurfaceVariant }),
                                        elevation = CardDefaults.cardElevation(defaultElevation = if (sel) 2.dp else 0.dp)
                                    ) {
                                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            LetterBadge(letter, if (sel) Primary.copy(alpha = 0.15f) else if (isDark) Color.White.copy(alpha = 0.12f) else Color.White, if (sel) Primary else if (isDark) DarkTextPrimary else TextSecondary)
                                            Spacer(Modifier.width(12.dp))
                                            RadioButton(selected = sel, onClick = { viewModel.selectAnswer(q.id, letter) }, colors = RadioButtonDefaults.colors(selectedColor = Primary))
                                            Spacer(Modifier.width(4.dp))
                                            Text(txt, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), color = if (isDark && !sel) DarkTextPrimary else Color.Unspecified)
                                        }
                                    }
                                }
                            }
                        }
                        "MULTI" -> {
                            val opts = q.options.split("|||").filter { it.isNotBlank() }
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                opts.forEach { opt ->
                                    val letter = opt.substringBefore(".")
                                    val txt = opt.substringAfter(".")
                                    val sel = (s.multiSelections[q.id] ?: emptySet()).contains(letter)
                                    Card(
                                        onClick = { viewModel.selectAnswer(q.id, letter) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        border = if (sel) BorderStroke(2.dp, Primary) else null,
                                        colors = CardDefaults.cardColors(containerColor = if (sel) { if (isDark) Primary.copy(alpha = 0.20f) else PrimaryContainer } else { if (isDark) DarkCard else SurfaceVariant }),
                                        elevation = CardDefaults.cardElevation(defaultElevation = if (sel) 2.dp else 0.dp)
                                    ) {
                                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = sel, onCheckedChange = { viewModel.selectAnswer(q.id, letter) }, colors = CheckboxDefaults.colors(checkedColor = Primary))
                                            Spacer(Modifier.width(4.dp))
                                            Text(txt, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), color = if (isDark && !sel) DarkTextPrimary else Color.Unspecified)
                                        }
                                    }
                                }
                            }
                        }
                        "JUDGE" -> {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                listOf("正确" to Icons.Default.ThumbUp, "错误" to Icons.Default.ThumbDown).forEach { (v, ic) ->
                                    val sel = s.answers[q.id] == v
                                    Card(
                                        onClick = { viewModel.selectAnswer(q.id, v) },
                                        modifier = Modifier.weight(1f).height(76.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        border = if (sel) BorderStroke(2.dp, Primary) else null,
                                        colors = CardDefaults.cardColors(containerColor = if (sel) { if (isDark) Primary.copy(alpha = 0.20f) else PrimaryContainer } else { if (isDark) DarkCard else SurfaceVariant }),
                                        elevation = CardDefaults.cardElevation(defaultElevation = if (sel) 2.dp else 0.dp)
                                    ) {
                                        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                            Icon(ic, null, Modifier.size(26.dp), tint = if (sel) Primary else if (isDark) DarkTextSecondary else TextSecondary)
                                            Spacer(Modifier.height(4.dp))
                                            Text(v, style = MaterialTheme.typography.titleSmall, fontWeight = if (sel) FontWeight.Bold else FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Bottom nav ──
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 6.dp, color = MaterialTheme.colorScheme.surface) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { viewModel.previousQuestion() }, enabled = s.currentIndex > 0,
                            shape = RoundedCornerShape(10.dp), modifier = Modifier.height(46.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                        ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("上一题") }
                        val last = s.currentIndex == s.questions.size - 1
                        Button(
                            onClick = {
                                val answered = s.questions.count { q -> if (q.questionType == "MULTI") s.multiSelections.containsKey(q.id) else s.answers.containsKey(q.id) }
                                if (answered == s.questions.size) viewModel.finishExam(onFinish) else showConfirm = true
                            },
                            shape = RoundedCornerShape(10.dp), modifier = Modifier.height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White)
                        ) { Icon(Icons.Default.CheckCircle, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("交卷", fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }

    if (showConfirm) {
        val unanswered = s.questions.size - s.questions.count { q -> if (q.questionType == "MULTI") s.multiSelections.containsKey(q.id) else s.answers.containsKey(q.id) }
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            shape = RoundedCornerShape(20.dp),
            icon = { Surface(shape = CircleShape, color = WarningOrange.copy(alpha = 0.10f), modifier = Modifier.size(48.dp)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Warning, null, tint = WarningOrange, modifier = Modifier.size(24.dp)) } } },
            title = { Text("确认交卷？", fontWeight = FontWeight.Bold) },
            text = { Text("还有 $unanswered 道题未作答，确定要交卷吗？") },
            confirmButton = { Button(onClick = { showConfirm = false; viewModel.finishExam(onFinish) }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) { Text("交卷") } },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("继续答题") } }
        )
    }
}
