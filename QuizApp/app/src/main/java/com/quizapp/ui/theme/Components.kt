package com.quizapp.ui.theme

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════
// Reusable design atoms — use these instead of raw Card/Surface
// ═══════════════════════════════════════════════════════

// ── Hero gradient header used in PracticeScreen & ProfileScreen ──
@Composable
fun GradientHeader(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, Color(0xFF6366F1), Secondary),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) { Column(content = content) }
    }
}

// ── 3-value stat badge (icon + value + label) for header rows ──
@Composable
fun StatBadge(icon: ImageVector, value: String, label: String, tint: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, label, modifier = Modifier.size(20.dp), tint = tint.copy(alpha = 0.85f))
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = tint)
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint.copy(alpha = 0.75f))
    }
}

// ── Practice-mode card ──
@Composable
fun ModeCard(icon: ImageVector, title: String, desc: String, accent: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = accent.copy(alpha = 0.12f), modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = accent, modifier = Modifier.size(24.dp)) }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = TextTertiary, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Question type tag ──
@Composable
fun TypeTag(type: String) {
    val (label, color) = when (type) {
        "SINGLE" -> "单选题" to Color(0xFF2563EB)
        "MULTI" -> "多选题" to Color(0xFF10B981)
        "JUDGE" -> "判断题" to Color(0xFFF59E0B)
        else -> type to TextSecondary
    }
    val ic = when (type) {
        "SINGLE" -> Icons.Default.RadioButtonChecked
        "MULTI" -> Icons.Default.CheckBox
        "JUDGE" -> Icons.Default.Balance
        else -> Icons.Default.Help
    }
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.10f)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(ic, null, modifier = Modifier.size(14.dp), tint = color)
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}

// ── Circular letter badge (A/B/C/D) for options ──
@Composable
fun LetterBadge(letter: String, bg: Color, fg: Color) {
    Surface(modifier = Modifier.size(34.dp), shape = CircleShape, color = bg) {
        Box(contentAlignment = Alignment.Center) {
            Text(letter, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = fg)
        }
    }
}

// ── Animated option card for Single/Multi/Judge ──
// States: Default | Selected | Correct | Wrong
@Composable
fun OptionCard(
    selected: Boolean,
    correct: Boolean,
    showResult: Boolean,
    letter: String,
    text: String,
    isCheckbox: Boolean = false,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bg: Color = when {
        showResult && correct    -> if (isDark) CorrectGreen.copy(alpha = 0.15f) else CorrectGreenBg
        showResult && selected && !correct -> if (isDark) WrongRed.copy(alpha = 0.15f) else WrongRedBg
        selected                -> if (isDark) Primary.copy(alpha = 0.20f) else PrimaryContainer
        else                    -> if (isDark) DarkCard else SurfaceVariant
    }
    val border: BorderStroke? = when {
        showResult && correct   -> BorderStroke(2.dp, CorrectGreen.copy(alpha = 0.6f))
        showResult && selected && !correct -> BorderStroke(2.dp, WrongRed.copy(alpha = 0.6f))
        selected && !showResult -> BorderStroke(2.dp, Primary)
        else                    -> null
    }
    val badgeBg: Color = when {
        showResult && correct   -> CorrectGreen.copy(alpha = 0.15f)
        showResult && selected && !correct -> WrongRed.copy(alpha = 0.15f)
        selected                -> Primary.copy(alpha = 0.15f)
        else                    -> if (isDark) Color.White.copy(alpha = 0.12f) else Color.White
    }
    val badgeFg: Color = when {
        showResult && correct   -> CorrectGreen
        showResult && selected && !correct -> WrongRed
        selected                -> Primary
        else                    -> if (isDark) DarkTextPrimary else TextSecondary
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = border,
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            LetterBadge(letter, badgeBg, badgeFg)
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, modifier = Modifier.weight(1f))
            if (isCheckbox) {
                Checkbox(checked = selected, onCheckedChange = { onClick() }, colors = CheckboxDefaults.colors(checkedColor = Primary))
            }
            if (showResult && correct) Icon(Icons.Default.CheckCircle, null, tint = CorrectGreen, modifier = Modifier.size(22.dp))
            else if (showResult && selected && !correct) Icon(Icons.Default.Cancel, null, tint = WrongRed, modifier = Modifier.size(22.dp))
        }
    }
}

// ── Empty state placeholder ──
@Composable
fun EmptyPlaceholder(icon: ImageVector, title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = PrimaryContainer) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, modifier = Modifier.size(40.dp), tint = Primary.copy(alpha = 0.6f)) }
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (subtitle != null) { Spacer(Modifier.height(4.dp)); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
    }
}
