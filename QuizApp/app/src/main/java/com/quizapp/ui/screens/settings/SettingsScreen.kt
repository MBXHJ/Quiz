package com.quizapp.ui.screens.settings

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.data.ReminderHelper
import com.quizapp.data.SettingsManager
import com.quizapp.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Calendar

data class SettingsUiState(
    val darkMode: Int = 0,
    val fontScale: Float = 1.0f,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val dailyGoalTarget: Int = 50,
    val ttsEnabled: Boolean = false,
    val ttsAutoPlay: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { settingsManager.darkModeFlow.collect { _uiState.value = _uiState.value.copy(darkMode = it) } }
        viewModelScope.launch { settingsManager.fontScaleFlow.collect { _uiState.value = _uiState.value.copy(fontScale = it) } }
        viewModelScope.launch { settingsManager.reminderEnabledFlow.collect { _uiState.value = _uiState.value.copy(reminderEnabled = it) } }
        viewModelScope.launch { settingsManager.reminderHourFlow.collect { h -> _uiState.value = _uiState.value.copy(reminderHour = h) } }
        viewModelScope.launch { settingsManager.reminderMinuteFlow.collect { m -> _uiState.value = _uiState.value.copy(reminderMinute = m) } }
        viewModelScope.launch { settingsManager.dailyGoalTargetFlow.collect { _uiState.value = _uiState.value.copy(dailyGoalTarget = it) } }
        viewModelScope.launch { settingsManager.ttsEnabledFlow.collect { _uiState.value = _uiState.value.copy(ttsEnabled = it) } }
        viewModelScope.launch { settingsManager.ttsAutoPlayFlow.collect { _uiState.value = _uiState.value.copy(ttsAutoPlay = it) } }
    }

    fun setDarkMode(mode: Int) = viewModelScope.launch { settingsManager.setDarkMode(mode) }
    fun setFontScale(scale: Float) = viewModelScope.launch { settingsManager.setFontScale(scale) }
    fun updateReminder(enabled: Boolean, hour: Int, minute: Int) = viewModelScope.launch {
        settingsManager.setReminderEnabled(enabled)
        settingsManager.setReminderTime(hour, minute)
    }
    fun setDailyGoalTarget(target: Int) = viewModelScope.launch { settingsManager.setDailyGoalTarget(target) }
    fun setTtsEnabled(enabled: Boolean) = viewModelScope.launch { settingsManager.setTtsEnabled(enabled) }
    fun setTtsAutoPlay(enabled: Boolean) = viewModelScope.launch { settingsManager.setTtsAutoPlay(enabled) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val s by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dark Mode
            item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF6366F1).copy(alpha = 0.1f), modifier = Modifier.size(34.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.DarkMode, null, Modifier.size(20.dp), tint = Color(0xFF6366F1)) }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("护眼模式", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    listOf("跟随系统" to 0, "浅色" to 1, "深色" to 2).forEach { (label, value) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = s.darkMode == value, onClick = { viewModel.setDarkMode(value) })
                            Spacer(Modifier.width(4.dp))
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            }

            // Font Size
            item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF2563EB).copy(alpha = 0.1f), modifier = Modifier.size(34.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.TextFields, null, Modifier.size(20.dp), tint = Color(0xFF2563EB)) }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("字体大小", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("小", style = MaterialTheme.typography.bodySmall)
                        Slider(value = s.fontScale, onValueChange = { viewModel.setFontScale(it) }, valueRange = 0.8f..1.4f, steps = 5, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                        Text("大", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("${(s.fontScale * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
            }

            // Daily Goal Target
            item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF10B981).copy(alpha = 0.1f), modifier = Modifier.size(34.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.TrackChanges, null, Modifier.size(20.dp), tint = Color(0xFF10B981)) }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("每日目标", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("每天刷题目标", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { if (s.dailyGoalTarget > 10) viewModel.setDailyGoalTarget(s.dailyGoalTarget - 10) }) {
                            Icon(Icons.Default.Remove, "减少")
                        }
                        Surface(shape = RoundedCornerShape(10.dp), color = PrimaryContainer) {
                            Text(
                                "${s.dailyGoalTarget} 题",
                                Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                        IconButton(onClick = { if (s.dailyGoalTarget < 500) viewModel.setDailyGoalTarget(s.dailyGoalTarget + 10) }) {
                            Icon(Icons.Default.Add, "增加")
                        }
                    }
                }
            }
            }

            // TTS Settings
            item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF8B5CF6).copy(alpha = 0.1f), modifier = Modifier.size(34.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.VolumeUp, null, Modifier.size(20.dp), tint = Color(0xFF8B5CF6)) }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("语音朗读", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        Switch(checked = s.ttsEnabled, onCheckedChange = { viewModel.setTtsEnabled(it) })
                    }
                    if (s.ttsEnabled) {
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("自动朗读下一题", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.weight(1f))
                            Switch(checked = s.ttsAutoPlay, onCheckedChange = { viewModel.setTtsAutoPlay(it) })
                        }
                    }
                }
            }
            }

            // Reminder
            item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF59E0B).copy(alpha = 0.1f), modifier = Modifier.size(34.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Notifications, null, Modifier.size(20.dp), tint = Color(0xFFF59E0B)) }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("学习提醒", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        Switch(checked = s.reminderEnabled, onCheckedChange = {
                            viewModel.updateReminder(it, s.reminderHour, s.reminderMinute)
                            if (it) { ReminderHelper.createChannel(context); ReminderHelper.schedule(context, s.reminderHour, s.reminderMinute) }
                            else ReminderHelper.cancel(context)
                        })
                    }
                    if (s.reminderEnabled) {
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(onClick = {
                            val cal = Calendar.getInstance()
                            android.app.TimePickerDialog(context, { _, h, m ->
                                viewModel.updateReminder(true, h, m)
                                ReminderHelper.schedule(context, h, m)
                            }, s.reminderHour, s.reminderMinute, true).show()
                        }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.Schedule, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp))
                            Text("提醒时间: ${"%02d".format(s.reminderHour)}:${"%02d".format(s.reminderMinute)}")
                        }
                    }
                }
            }
            }
        }
    }
}
