package com.fhiont.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhiont.core.network.PhpPropertyApi
import com.fhiont.core.theme.ThemeMode
import com.fhiont.core.theme.ThemePreferences
import com.fhiont.feature.city.presentation.City
import com.fhiont.feature.onboarding.domain.usecase.GetOnboardingCompletedUseCase
import com.fhiont.feature.onboarding.domain.usecase.SetOnboardingCityUseCase
import com.fhiont.feature.onboarding.domain.usecase.SetOnboardingCompletedUseCase
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.ui.navigation.AppScreen
import com.fhiont.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    themePreferences: ThemePreferences,
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    private val setOnboardingCityUseCase: SetOnboardingCityUseCase,
    private val phpPropertyApi: PhpPropertyApi,
    private val userSession: UserSession
) : ViewModel() {
    private val _selectedTab = MutableStateFlow<AppScreen>(AppScreen.Home)
    val selectedTab: StateFlow<AppScreen> = _selectedTab.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode

    /**
     * Shows the onboarding flow only when it has not been completed yet.
     */
    val showOnboarding: StateFlow<Boolean> = getOnboardingCompletedUseCase()
        .map { !it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            !getOnboardingCompletedUseCase().value
        )

    fun selectTab(screen: AppScreen) {
        _selectedTab.value = screen
    }

    /**
     * Loads a property by ID for a push-notification deep link. Returns null
     * when the user is not signed in or the lookup fails.
     */
    suspend fun getPropertyById(propertyId: String): Property? {
        val token = userSession.getUser()?.sessionId?.takeIf { it.isNotBlank() } ?: return null
        return when (val result = phpPropertyApi.getPropertyById(token, propertyId)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Logger.e("MainViewModel", "getPropertyById failed for $propertyId: ${result.message}")
                null
            }
        }
    }

    fun completeOnboarding() {
        setOnboardingCompletedUseCase(true)
    }

    /**
     * Caches the city picked on the onboarding city screen, then finishes
     * onboarding. The cached values are shown on the guest profile card.
     */
    fun selectCity(city: City) {
        Logger.d("MainViewModel", "selectCity: name='${city.name}', region='${city.region}'")
        setOnboardingCityUseCase(city.name, city.region)
        completeOnboarding()
    }

    /**
     * Caches the city/location resolved from the device GPS during onboarding,
     * then finishes onboarding.
     */
    fun saveOnboardingLocation(city: String, location: String) {
        Logger.d("MainViewModel", "saveOnboardingLocation: city='$city', location='$location'")
        if (city.isNotBlank() || location.isNotBlank()) {
            setOnboardingCityUseCase(city, location)
        }
        completeOnboarding()
    }
}
