package com.realeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.core.theme.ThemeMode
import com.realeapp.core.theme.ThemePreferences
import com.realeapp.feature.onboarding.data.OnboardingPreferences
import com.realeapp.ui.navigation.AppScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    themePreferences: ThemePreferences,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {
    private val _selectedTab = MutableStateFlow<AppScreen>(AppScreen.Home)
    val selectedTab: StateFlow<AppScreen> = _selectedTab.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode

    /**
     * Shows the onboarding flow only when it has not been completed yet.
     */
    val showOnboarding: StateFlow<Boolean> = onboardingPreferences.isOnboardingCompleted
        .map { !it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            !onboardingPreferences.isOnboardingCompleted.value
        )

    fun selectTab(screen: AppScreen) {
        _selectedTab.value = screen
    }

    fun completeOnboarding() {
        onboardingPreferences.setOnboardingCompleted(true)
    }
}
