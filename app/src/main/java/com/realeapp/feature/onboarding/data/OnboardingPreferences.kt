package com.realeapp.feature.onboarding.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists whether the user has completed the first-launch onboarding.
 */
class OnboardingPreferences(context: Context) {

    private companion object {
        const val PREFS_NAME = "onboarding"
        const val KEY_COMPLETED = "onboarding_completed"
    }

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isCompleted = MutableStateFlow(loadCompleted())
    val isOnboardingCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    fun setOnboardingCompleted(completed: Boolean) {
        preferences.edit { putBoolean(KEY_COMPLETED, completed) }
        _isCompleted.value = completed
    }

    private fun loadCompleted(): Boolean =
        preferences.getBoolean(KEY_COMPLETED, false)
}
