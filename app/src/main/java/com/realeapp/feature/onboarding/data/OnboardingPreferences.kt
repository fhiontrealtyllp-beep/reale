package com.realeapp.feature.onboarding.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.realeapp.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persists first-launch onboarding state and, when the user is a guest,
 * the locally-saved address.
 */
class OnboardingPreferences(context: Context) {

    private companion object {
        const val PREFS_NAME = "onboarding"
        const val KEY_COMPLETED = "onboarding_completed"
        const val KEY_ADDRESS = "onboarding_address"
        const val KEY_CITY = "onboarding_city"
        const val KEY_LOCATION = "onboarding_location"
    }

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isCompleted = MutableStateFlow(loadCompleted())
    val isOnboardingCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    private val _address = MutableStateFlow(loadAddress())
    val address: StateFlow<String> = _address.asStateFlow()

    private val _city = MutableStateFlow(loadCity())
    val city: StateFlow<String> = _city.asStateFlow()

    private val _location = MutableStateFlow(loadLocation())
    val location: StateFlow<String> = _location.asStateFlow()

    fun setOnboardingCompleted(completed: Boolean) {
        preferences.edit { putBoolean(KEY_COMPLETED, completed) }
        _isCompleted.value = completed
    }

    fun setAddress(address: String) {
        preferences.edit { putString(KEY_ADDRESS, address) }
        _address.value = address
    }

    /**
     * Caches the city picked on the onboarding city screen so it can be shown
     * elsewhere (e.g. the guest profile card) without requiring login.
     */
    fun setCitySelection(city: String, location: String) {
        Logger.d("OnboardingPreferences", "setCitySelection: city='$city', location='$location'")
        preferences.edit {
            putString(KEY_CITY, city)
            putString(KEY_LOCATION, location)
        }
        _city.value = city
        _location.value = location
    }

    private fun loadCompleted(): Boolean =
        preferences.getBoolean(KEY_COMPLETED, false)

    private fun loadAddress(): String =
        preferences.getString(KEY_ADDRESS, "").also {
            Logger.d("OnboardingPreferences", "loadAddress: '$it'")
        } ?: ""

    private fun loadCity(): String =
        preferences.getString(KEY_CITY, "").also {
            Logger.d("OnboardingPreferences", "loadCity: '$it'")
        } ?: ""

    private fun loadLocation(): String =
        preferences.getString(KEY_LOCATION, "").also {
            Logger.d("OnboardingPreferences", "loadLocation: '$it'")
        } ?: ""
}
