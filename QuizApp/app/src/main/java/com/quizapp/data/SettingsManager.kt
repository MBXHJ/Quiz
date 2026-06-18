package com.quizapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val DARK_MODE = intPreferencesKey("dark_mode") // 0=auto, 1=light, 2=dark
        val FONT_SCALE = floatPreferencesKey("font_scale") // 0.8f to 1.4f
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
    }

    val darkModeFlow: Flow<Int> = context.dataStore.data.map { it[DARK_MODE] ?: 0 }
    val fontScaleFlow: Flow<Float> = context.dataStore.data.map { it[FONT_SCALE] ?: 1.0f }
    val reminderEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[REMINDER_ENABLED] ?: false }
    val reminderHourFlow: Flow<Int> = context.dataStore.data.map { it[REMINDER_HOUR] ?: 20 }
    val reminderMinuteFlow: Flow<Int> = context.dataStore.data.map { it[REMINDER_MINUTE] ?: 0 }

    suspend fun setDarkMode(mode: Int) { context.dataStore.edit { it[DARK_MODE] = mode } }
    suspend fun setFontScale(scale: Float) { context.dataStore.edit { it[FONT_SCALE] = scale } }
    suspend fun setReminderEnabled(enabled: Boolean) { context.dataStore.edit { it[REMINDER_ENABLED] = enabled } }
    suspend fun setReminderTime(hour: Int, minute: Int) { context.dataStore.edit { it[REMINDER_HOUR] = hour; it[REMINDER_MINUTE] = minute } }
}
