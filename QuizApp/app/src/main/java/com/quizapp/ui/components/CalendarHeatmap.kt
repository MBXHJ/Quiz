package com.quizapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quizapp.data.db.entity.DailyStatsEntity
import com.quizapp.ui.theme.TextSecondary
import com.quizapp.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarHeatmap(
    stats: List<DailyStatsEntity>,
    days: Int = 90,
    goalTarget: Int = 50,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val statsMap = stats.associateBy { it.date }
    val cal = Calendar.getInstance()

    // Generate last N days
    val daysList = (days - 1 downTo 0).map { offset ->
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -offset)
        val dateKey = sdf.format(cal.time)
        dateKey to statsMap[dateKey]
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Legend
        Row(
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("少", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            Spacer(Modifier.width(4.dp))
            listOf(0, 25, 50, 75, 100).map { level ->
                Box(
                    Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(heatmapColor(level, 100))
                )
                Spacer(Modifier.width(2.dp))
            }
            Spacer(Modifier.width(4.dp))
            Text("多", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
        }

        // Grid: 7 columns (days of week)
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth().height(((days / 7 + 1) * 18).dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            userScrollEnabled = false
        ) {
            items(daysList) { (date, stat) ->
                val questions = stat?.questionsAnswered ?: 0
                val intensity = if (goalTarget > 0) {
                    ((questions.toFloat() / goalTarget) * 100f).toInt().coerceIn(0, 100)
                } else 0

                Box(
                    Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(heatmapColor(intensity, goalTarget))
                )
            }
        }
    }
}

fun heatmapColor(intensity: Int, max: Int): Color {
    return when {
        intensity <= 0 -> Color(0xFFE5E7EB) // light gray - no activity
        intensity <= 25 -> Color(0xFFC7D2FE) // light indigo
        intensity <= 50 -> Color(0xFFA5B4FC)
        intensity <= 75 -> Color(0xFF818CF8)
        intensity < 100 -> Color(0xFF6366F1)
        else -> Color(0xFF10B981) // green - target met/exceeded
    }
}
