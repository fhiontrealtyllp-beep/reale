package com.fhiont.core.notification

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationSettings(
    val enabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

class NotificationPreferences(context: Context) {
    private companion object {
        const val PREFS_NAME = "notification_settings"
        const val KEY_ENABLED = "enabled"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val KEY_VIBRATION_ENABLED = "vibration_enabled"
    }

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<NotificationSettings> = _settings.asStateFlow()

    fun setEnabled(enabled: Boolean) = update { copy(enabled = enabled) }

    fun setSoundEnabled(enabled: Boolean) = update { copy(soundEnabled = enabled) }

    fun setVibrationEnabled(enabled: Boolean) = update { copy(vibrationEnabled = enabled) }

    private fun update(transform: NotificationSettings.() -> NotificationSettings) {
        val updated = _settings.value.transform()
        preferences.edit {
            putBoolean(KEY_ENABLED, updated.enabled)
            putBoolean(KEY_SOUND_ENABLED, updated.soundEnabled)
            putBoolean(KEY_VIBRATION_ENABLED, updated.vibrationEnabled)
        }
        _settings.value = updated
    }

    private fun loadSettings() = NotificationSettings(
        enabled = preferences.getBoolean(KEY_ENABLED, true),
        soundEnabled = preferences.getBoolean(KEY_SOUND_ENABLED, true),
        vibrationEnabled = preferences.getBoolean(KEY_VIBRATION_ENABLED, true)
    )
}
