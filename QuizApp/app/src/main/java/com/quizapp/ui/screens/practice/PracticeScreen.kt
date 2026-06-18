package com.quizapp.ui.screens.practice

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import com.quizapp.ui.theme.*
import com.quizapp.data.db.entity.PracticeProgressEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    bankId: Long,
    onBack: () -> Unit,
    onStartPractice: (mode: String) -> Unit,
    onStartRandom: (count: Int) -> Unit,
    onStartExam: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    LaunchedEffect(bankId) { viewModel.loadBank(bankId) }
    val s by viewModel.uiState.collectAsState()
    var showTypeDlg by remember { mutableStateOf(false) }
    var showRandomDlg by remember { mutableStateOf(false) }
    var randomCount by remember { mutableStateOf(100) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.bank?.name ?: "刷题", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // ═══ Gradient Header with stats ═══
            GradientHeader(modifier = Modifier.padding(horizontal = 0.dp)) {
                Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(s.bank?.name ?: "", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            StatBadge(Icons.Default.MenuBook, "${s.questionCount}", "总题数")
                        }
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            StatBadge(Icons.Default.CheckCircle, "${s.answeredQuestionCount}", "已完成", Color(0xFFA7F3D0), onClick = { onStartPractice("sequential") })
                        }
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            StatBadge(Icons.Default.ErrorOutline, "${s.wrongQuestionCount}", "错题", Color(0xFFFFCDD2), onClick = { onStartPractice("wrong") })
                        }
                    }
                    if (s.questionCount > 0) {
                        Spacer(Modifier.height(16.dp))
                        val pct = s.answeredQuestionCount.toFloat() / s.questionCount
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Text("学习进度", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                                Text("${(pct * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(progress = { pct }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = Color(0xFFA7F3D0), trackColor = Color.White.copy(alpha = 0.22f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // ═══ Mode cards (scrollable) ═══
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
            ) {
                // ═══ Search field ═══
                var searchQuery by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    placeholder = { Text("搜索题目内容...") },
                    leadingIcon = { Icon(Icons.Default.Search, "搜索") },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "清除")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchQuery.isNotBlank()) {
                                onStartPractice("search_$searchQuery")
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border
                    )
                )

                Text("选择刷题模式", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))

                // Sequential mode with resume support
                val seqProg = s.sequentialProgress
                if (seqProg != null && seqProg.answeredCount > 0 && seqProg.currentIndex < seqProg.totalQuestions) {
                    // Show resume card
                    Card(
                        onClick = { onStartPractice("sequential") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = WarningOrange.copy(alpha = 0.06f))
                    ) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(12.dp), color = WarningOrange.copy(alpha = 0.15f), modifier = Modifier.size(48.dp)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.PlayArrow, null, tint = WarningOrange, modifier = Modifier.size(24.dp)) }
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text("继续顺序练习", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = WarningOrange)
                                Text("已做 ${seqProg.answeredCount}/${seqProg.totalQuestions} 题，正确 ${seqProg.correctCount} 题", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                        }
                    }
                    // Show restart option
                    ModeCard(Icons.Default.Refresh, "重新开始练习", "清除当前进度，从头开始", Color(0xFF64748B)) {
                        onStartPractice("restart_sequential")
                    }
                } else {
                    ModeCard(Icons.Default.ListAlt, "顺序练习", "按顺序逐题练习，查看答案与解析", Color(0xFF2563EB)) { onStartPractice("sequential") }
                }

                ModeCard(Icons.Default.Assignment, "模拟考试", "按比例随机抽题，模拟真实考场", CorrectGreen) { onStartExam() }
                ModeCard(Icons.Default.Shuffle, "随机刷题", "随机抽取题目，灵活练习", Color(0xFF8B5CF6)) { showRandomDlg = true }
                ModeCard(Icons.Default.Category, "题型分类", "按单选、多选、判断题分类刷题", WarningOrange) { showTypeDlg = true }
                AnimatedVisibility(s.wrongQuestionCount > 0, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
                    ModeCard(Icons.Default.AutoFixHigh, "错题重做", "共 ${s.wrongQuestionCount} 道错题，针对性巩固提分", WrongRed) { onStartPractice("wrong") }
                }
            }
        }
    }

    // ═══ Type dialog ═══
    if (showTypeDlg) {
        AlertDialog(
            onDismissRequest = { showTypeDlg = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("选择题型", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        Triple("单选题", "SINGLE", Color(0xFF2563EB)),
                        Triple("多选题", "MULTI", CorrectGreen),
                        Triple("判断题", "JUDGE", WarningOrange)
                    ).forEach { (label, type, col) ->
                        Surface(
                            onClick = { showTypeDlg = false; onStartPractice("type_$type") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp), color = col.copy(alpha = 0.06f)
                        ) {
                            Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(8.dp), color = col.copy(alpha = 0.12f), modifier = Modifier.size(30.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text(type.take(1), fontWeight = FontWeight.Bold, color = col, style = MaterialTheme.typography.labelLarge) }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showTypeDlg = false }) { Text("取消") } }
        )
    }

    // ═══ Random count dialog ═══
    if (showRandomDlg) {
        val maxCount = s.questionCount
        AlertDialog(
            onDismissRequest = { showRandomDlg = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("随机刷题", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("选择题数（最多 $maxCount 题）", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { randomCount = maxOf(10, randomCount - 10) }) {
                            Icon(Icons.Default.Remove, "减少")
                        }
                        Surface(shape = RoundedCornerShape(12.dp), color = PrimaryContainer, modifier = Modifier.width(80.dp)) {
                            Text("$randomCount", modifier = Modifier.padding(vertical = 8.dp), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Primary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                        IconButton(onClick = { randomCount = minOf(maxCount, randomCount + 10) }) {
                            Icon(Icons.Default.Add, "增加")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Quick select chips
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(10, 20, 50, 100, maxCount).filter { it <= maxCount }.forEach { n ->
                            FilterChip(
                                selected = randomCount == n,
                                onClick = { randomCount = n },
                                label = { Text(if (n == maxCount) "全部" else "$n", style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showRandomDlg = false; onStartRandom(randomCount) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) {
                    Text("开始刷题", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = { TextButton(onClick = { showRandomDlg = false }) { Text("取消") } }
        )
    }
}
