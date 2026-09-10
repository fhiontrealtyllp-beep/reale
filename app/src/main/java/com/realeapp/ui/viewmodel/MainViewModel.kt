package com.realeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.core.theme.ThemeMode
import com.realeapp.core.theme.ThemePreferences
import com.realeapp.feature.city.presentation.City
import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository
import com.realeapp.ui.navigation.AppScreen
import com.realeapp.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    themePreferences: ThemePreferences,
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {
    private val _selectedTab = MutableStateFlow<AppScreen>(AppScreen.Home)
    val selectedTab: StateFlow<AppScreen> = _selectedTab.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode

    /**
     * Shows the onboarding flow only when it has not been completed yet.
     */
    val showOnboarding: StateFlow<Boolean> = onboardingRepository.isOnboardingCompleted
        .map { !it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            !onboardingRepository.isOnboardingCompleted.value
        )

    fun selectTab(screen: AppScreen) {
        _selectedTab.value = screen
    }

    fun completeOnboarding() {
        onboardingRepository.setOnboardingCompleted(true)
    }

    /**
     * Caches the city picked on the onboarding city screen, then finishes
     * onboarding. The cached values are shown on the guest profile card.
     */
    fun selectCity(city: City) {
        Logger.d("MainViewModel", "selectCity: name='${city.name}', region='${city.region}'")
        onboardingRepository.setCitySelection(city.name, city.region)
        completeOnboarding()
    }

    /**
     * Caches the city/location resolved from the device GPS during onboarding,
     * then finishes onboarding.
     */
    fun saveOnboardingLocation(city: String, location: String) {
        Logger.d("MainViewModel", "saveOnboardingLocation: city='$city', location='$location'")
        if (city.isNotBlank() || location.isNotBlank()) {
            onboardingRepository.setCitySelection(city, location)
        }
        completeOnboarding()
    }
}
