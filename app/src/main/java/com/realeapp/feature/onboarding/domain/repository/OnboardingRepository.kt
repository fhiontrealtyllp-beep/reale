package com.realeapp.feature.onboarding.domain.repository

import kotlinx.coroutines.flow.StateFlow

/**
 * Repository contract for onboarding state and locally cached guest location
 * data (address, city, location).
 */
interface OnboardingRepository {
    val isOnboardingCompleted: StateFlow<Boolean>
    val address: StateFlow<String>
    val city: StateFlow<String>
    val location: StateFlow<String>

    fun setOnboardingCompleted(completed: Boolean)
    fun setAddress(address: String)
    fun setCitySelection(city: String, location: String)
}
