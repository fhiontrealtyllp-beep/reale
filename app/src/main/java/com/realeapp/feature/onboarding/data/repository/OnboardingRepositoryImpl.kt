package com.realeapp.feature.onboarding.data.repository

import com.realeapp.feature.onboarding.data.OnboardingPreferences
import com.realeapp.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.StateFlow

class OnboardingRepositoryImpl(
    private val preferences: OnboardingPreferences
) : OnboardingRepository {
    override val isOnboardingCompleted: StateFlow<Boolean> = preferences.isOnboardingCompleted
    override val address: StateFlow<String> = preferences.address
    override val city: StateFlow<String> = preferences.city
    override val location: StateFlow<String> = preferences.location

    override fun setOnboardingCompleted(completed: Boolean) {
        preferences.setOnboardingCompleted(completed)
    }

    override fun setAddress(address: String) {
        preferences.setAddress(address)
    }

    override fun setCitySelection(city: String, location: String) {
        preferences.setCitySelection(city, location)
    }
}
