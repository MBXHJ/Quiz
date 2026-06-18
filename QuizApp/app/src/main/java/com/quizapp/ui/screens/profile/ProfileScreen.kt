package com.quizapp.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quizapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onRestartPractice: (bankId: Long, mode: String, recordId: Long) -> Unit = { _, _, _ -> },
    viewModel: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadStats() }
    val s by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("我的", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }) }
    ) { padding ->
        if (s.isLoading) Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
        else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // ═══ Gradient Stats Header ═══
                item {
                    GradientHeader {
                        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(modifier = Modifier.size(52.dp), shape = androidx.compose.foundation.shape.CircleShape, color = Color.White.copy(alpha = 0.20f)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.BarChart, null, Modifier.size(26.dp), tint = Color.White) }
                            }
                            Spacer(Modifier.height(10.dp))
                            Text("学习统计", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                StatBadge(Icons.Default.MenuBook, "${s.totalBanks}", "题库")
                                StatBadge(Icons.Default.CheckCircle, "${s.totalQuestions}", "总题数")
                                StatBadge(Icons.Default.Done, "${s.totalAnswered}", "已完成", Color(0xFFA7F3D0))
                                StatBadge(Icons.Default.ErrorOutline, "${s.totalWrong}", "错题", Color(0xFFFFCDD2))
                            }
                        }
                    }
                }

                // ═══ Practice Records ═══
                item {
                    Card(modifier = Modifier.fillMaxWidth().animateContentSize(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(34.dp), shape = RoundedCornerShape(10.dp), color = Color(0xFF2563EB).copy(alpha = 0.1f)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.ListAlt, null, Modifier.size(20.dp), tint = Color(0xFF2563EB)) }
                                }
                                Spacer(Modifier.width(10.dp))
                                Text("练习记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.weight(1f))
                                Surface(shape = RoundedCornerShape(12.dp), color = PrimaryContainer) {
                                    Text("${s.practiceRecords.size}条", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = OnPrimaryContainer)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            if (s.practiceRecords.isEmpty()) {
                                Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.ListAlt, null, Modifier.size(36.dp), tint = TextTertiary)
                                    Spacer(Modifier.height(6.dp))
                                    Text("暂无练习记录", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                                    Text("完成一次顺序练习或题型练习后将在这里显示", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                                }
                            } else {
                                s.practiceRecords.take(5).forEach { rec ->
                                    val name = s.bankNames[rec.bankId] ?: "未知题库"
                                    val date = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(rec.startTime))
                                    val accuracy = if (rec.answeredCount > 0) (rec.correctCount * 100) / rec.answeredCount else 0
                                    var showDelPractice by remember { mutableStateOf(false) }
                                    Surface(
                                        onClick = { onRestartPractice(rec.bankId, rec.mode, rec.id) },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        color = SurfaceVariant.copy(alpha = 0.5f)
                                    ) {
                                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Column(Modifier.weight(1f)) {
                                                Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(rec.modeLabel.ifEmpty { rec.mode }, style = MaterialTheme.typography.bodySmall, color = Color(0xFF2563EB))
                                                    Text(" · $date", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                }
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("$accuracy%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = if (accuracy >= 80) CorrectGreen else if (accuracy >= 60) WarningOrange else WrongRed)
                                                Text("${rec.correctCount}/${rec.answeredCount}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                                if (rec.wrongQuestionIds.isNotBlank()) {
                                                    Spacer(Modifier.height(2.dp))
                                                    Surface(
                                                        onClick = { onRestartPractice(rec.bankId, "record_wrong_${rec.id}", rec.id) },
                                                        shape = RoundedCornerShape(6.dp), color = WrongRed.copy(alpha = 0.1f)
                                                    ) {
                                                        Text("错题重做", Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = WrongRed, fontWeight = FontWeight.SemiBold)
                                                    }
                                                }
                                            }
                                            Spacer(Modifier.width(4.dp))
                                            IconButton(onClick = { showDelPractice = true }, modifier = Modifier.size(32.dp)) {
                                                Icon(Icons.Default.Delete, "删除", tint = WrongRed.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    if (showDelPractice) {
                                        AlertDialog(
                                            onDismissRequest = { showDelPractice = false },
                                            shape = RoundedCornerShape(20.dp),
                                            title = { Text("删除记录", fontWeight = FontWeight.Bold) },
                                            text = { Text("确定删除这条练习记录吗？") },
                                            confirmButton = { Button(onClick = { viewModel.deletePracticeRecord(rec.id); showDelPractice = false }, colors = ButtonDefaults.buttonColors(containerColor = WrongRed)) { Text("删除") } },
                                            dismissButton = { TextButton(onClick = { showDelPractice = false }) { Text("取消") } }
                                        )
                                    }
                                }
                                if (s.practiceRecords.size > 5) {
                                    Spacer(Modifier.height(4.dp))
                                    Text("还有 ${s.practiceRecords.size - 5} 条记录...", style = MaterialTheme.typography.bodySmall, color = TextTertiary, modifier = Modifier.align(Alignment.CenterHorizontally))
                                }
                            }
                        }
                    }
                }

                // ═══ Exam Records ═══
                item {
                    Card(modifier = Modifier.fillMaxWidth().animateContentSize(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(34.dp), shape = RoundedCornerShape(10.dp), color = WarningOrange.copy(alpha = 0.1f)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.History, null, Modifier.size(20.dp), tint = WarningOrange) }
                                }
                                Spacer(Modifier.width(10.dp))
                                Text("考试记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.weight(1f))
                                Surface(shape = RoundedCornerShape(12.dp), color = PrimaryContainer) {
                                    Text("${s.totalExamCount}次", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = OnPrimaryContainer)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            if (s.examRecords.isEmpty()) {
                                Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Assignment, null, Modifier.size(36.dp), tint = TextTertiary)
                                    Spacer(Modifier.height(6.dp))
                                    Text("暂无考试记录", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                                    Text("完成一次模拟考试后将在这里显示", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                                }
                            } else {
                                s.examRecords.forEach { rec ->
                                    val name = s.bankNames[rec.bankId] ?: "未知题库"
                                    val date = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(rec.examDate))
                                    val pc = if (rec.score >= 60) CorrectGreen else WrongRed
                                    var showDelExam by remember { mutableStateOf(false) }
                                    Surface(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), shape = RoundedCornerShape(10.dp), color = SurfaceVariant.copy(alpha = 0.5f)) {
                                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Column(Modifier.weight(1f)) { Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium); Text(date, style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                                            Column(horizontalAlignment = Alignment.End) { Text("${rec.score}分", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = pc); Text("${rec.correctCount}/${rec.totalCount}", style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                                            Spacer(Modifier.width(4.dp))
                                            IconButton(onClick = { showDelExam = true }, modifier = Modifier.size(32.dp)) {
                                                Icon(Icons.Default.Delete, "删除", tint = WrongRed.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    if (showDelExam) {
                                        AlertDialog(
                                            onDismissRequest = { showDelExam = false },
                                            shape = RoundedCornerShape(20.dp),
                                            title = { Text("删除记录", fontWeight = FontWeight.Bold) },
                                            text = { Text("确定删除这条考试记录吗？") },
                                            confirmButton = { Button(onClick = { viewModel.deleteExamRecord(rec.id); showDelExam = false }, colors = ButtonDefaults.buttonColors(containerColor = WrongRed)) { Text("删除") } },
                                            dismissButton = { TextButton(onClick = { showDelExam = false }) { Text("取消") } }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ═══ About & Copyright ═══
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(34.dp), shape = RoundedCornerShape(10.dp), color = Primary.copy(alpha = 0.1f)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Info, null, Modifier.size(20.dp), tint = Primary) } }
                                Spacer(Modifier.width(10.dp))
                                Text("关于", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(10.dp))
                            Text("刷题助手 v2.0", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("支持导入 txt/md/docx/xlsx 题库\n顺序练习 · 模拟考试 · 题型分类 · 错题重做", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Spacer(Modifier.height(10.dp)); HorizontalDivider(color = BorderLight)
                            Spacer(Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Copyright, null, Modifier.size(16.dp), tint = TextSecondary); Spacer(Modifier.width(4.dp)); Text("著作权声明", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
                            Text("本软件著作权人：唐家俊\n本软件仅用于分享学习使用，不可用于任何商业行为。", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
