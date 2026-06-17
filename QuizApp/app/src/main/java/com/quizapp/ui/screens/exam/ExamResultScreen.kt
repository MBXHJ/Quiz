package com.quizapp.ui.screens.exam

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.quizapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultScreen(score: Int, total: Int, correct: Int, onBack: () -> Unit) {
    val passed = score >= 60
    val wrong = total - correct
    val accuracy = if (total > 0) (correct * 100) / total else 0

    Scaffold(
        topBar = { TopAppBar(title = { Text("考试结果", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            // ── Score Ring ──
            Box(Modifier.size(170.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { if (total > 0) correct.toFloat() / total else 0f },
                    modifier = Modifier.size(170.dp),
                    color = if (passed) CorrectGreen else WrongRed,
                    trackColor = SurfaceVariant, strokeWidth = 10.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$score", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = if (passed) CorrectGreen else WrongRed)
                    Text("分", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = if (passed) CorrectGreen.copy(alpha = 0.10f) else WrongRed.copy(alpha = 0.10f)) {
                        Text(if (passed) "通过" else "未通过", Modifier.padding(horizontal = 14.dp, vertical = 4.dp), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = if (passed) CorrectGreen else WrongRed)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Message ──
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = if (passed) CorrectGreenBg else WrongRedBg) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(if (passed) Icons.Default.Star else Icons.Default.AutoFixHigh, null, Modifier.size(32.dp), tint = if (passed) CorrectGreen else WarningOrange)
                    Spacer(Modifier.height(8.dp))
                    Text(if (passed) "恭喜通过！" else "继续加油！", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(if (passed) "你做得很棒，继续保持！" else "多练习，查漏补缺，下次一定通过！", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Stat Grid ──
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Row(Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ResultStat(Icons.Default.ViewList, "$total", "总题数", Color(0xFF2563EB))
                    ResultStat(Icons.Default.CheckCircle, "$correct", "正确", CorrectGreen)
                    ResultStat(Icons.Default.Cancel, "$wrong", "错误", WrongRed)
                    ResultStat(Icons.Default.Grade, "$accuracy%", "正确率", if (passed) CorrectGreen else WrongRed)
                }
            }

            Spacer(Modifier.height(28.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                Icon(Icons.Default.ArrowBack, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("返回题库", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ResultStat(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = accent.copy(alpha = 0.10f)) { Box(contentAlignment = Alignment.Center) { Icon(icon, null, Modifier.size(20.dp), tint = accent) } }
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = accent)
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}
