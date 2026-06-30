package com.quizapp.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.quizapp.data.db.entity.QuestionBankEntity
import com.quizapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankListScreen(
    onBankClick: (Long) -> Unit,
    onImportClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: BankListViewModel = hiltViewModel()
) {
    val banks by viewModel.banks.collectAsState()
    val homeState by viewModel.homeState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Primary, modifier = Modifier.size(32.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(18.dp)) }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("刷题助手", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    Surface(
                        onClick = onProfileClick,
                        shape = RoundedCornerShape(20.dp),
                        color = Primary.copy(alpha = 0.08f)
                    ) {
                        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("我的", style = MaterialTheme.typography.labelLarge, color = Primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onImportClick,
                containerColor = Primary, contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("导入题库", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        if (banks.isEmpty()) {
            EmptyPlaceholder(Icons.Default.MenuBook, "还没有题库", "点击下方按钮导入题库文件", Modifier.fillMaxSize().padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ═══ Daily progress card ═══
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (homeState.todayTargetMet)
                                Color(0xFF10B981).copy(alpha = 0.06f)
                            else
                                PrimaryContainer
                        )
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (homeState.todayTargetMet) Icons.Default.EmojiEvents else Icons.Default.Whatshot,
                                    null,
                                    tint = if (homeState.todayTargetMet) Color(0xFF10B981) else Primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("今日进度", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.weight(1f))
                                if (homeState.currentStreak > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = WarningOrange.copy(alpha = 0.12f)
                                    ) {
                                        Row(
                                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.LocalFireDepartment, null, tint = WarningOrange, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                "连续 ${homeState.currentStreak} 天",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = WarningOrange,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            LinearProgressIndicator(
                                progress = { homeState.todayProgress },
                                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                                color = if (homeState.todayTargetMet) Color(0xFF10B981) else Primary,
                                trackColor = if (homeState.todayTargetMet) Color(0xFF10B981).copy(alpha = 0.1f) else Primary.copy(alpha = 0.1f)
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    "${homeState.todayAnswered}/${homeState.dailyGoalTarget} 题",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    if (homeState.todayTargetMet) "🎉 目标达成！"
                                    else if (homeState.todayAnswered > 0) "还差 ${homeState.dailyGoalTarget - homeState.todayAnswered} 题"
                                    else "今天还没开始刷题",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                item {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Bottom) {
                        Column {
                            Text("我的题库", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text("${banks.size} 个题库", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                items(banks, key = { it.id }) { bank -> BankCard(bank, { onBankClick(bank.id) }, { viewModel.deleteBank(bank) }) }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun BankCard(bank: QuestionBankEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val accents = listOf(Color(0xFF2563EB), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF8B5CF6), Color(0xFFEC4899))
    val accent = accents[(bank.id % accents.size).toInt()]
    val icos = listOf(Icons.Default.MenuBook, Icons.Default.School, Icons.Default.AutoStories, Icons.Default.LibraryBooks, Icons.Default.Bookmark)
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(bank.importDate))

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(5.dp).fillMaxHeight().defaultMinSize(110.dp).clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)).background(accent))
            Row(Modifier.weight(1f).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp), color = accent.copy(alpha = 0.1f)) {
                    Box(contentAlignment = Alignment.Center) { Icon(icos[(bank.id % icos.size).toInt()], null, Modifier.size(24.dp), tint = accent) }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(bank.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(6.dp), color = accent.copy(alpha = 0.08f)) {
                            Text("${bank.questionCount}题", Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.CalendarMonth, null, Modifier.size(14.dp), tint = TextTertiary)
                        Spacer(Modifier.width(4.dp))
                        Text(date, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    }
                }
                IconButton(onClick = { showDialog = true }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Delete, "删除", tint = WrongRed.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(20.dp),
            icon = { Surface(shape = CircleShape, color = WrongRed.copy(alpha = 0.08f), modifier = Modifier.size(48.dp)) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Warning, null, tint = WrongRed, modifier = Modifier.size(24.dp)) } } },
            title = { Text("删除题库", fontWeight = FontWeight.Bold) },
            text = { Text("确定要删除「${bank.name}」吗？\n所有题目和记录将被永久清除。") },
            confirmButton = { Button(onClick = { onDelete(); showDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = WrongRed)) { Text("删除") } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("取消") } }
        )
    }
}
