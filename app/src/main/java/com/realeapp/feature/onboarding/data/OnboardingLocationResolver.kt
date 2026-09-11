package com.realeapp.feature.onboarding.data

import android.content.Context
import android.location.Geocoder
import com.realeapp.core.location.CurrentLocationProvider
import com.realeapp.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

private const val TAG = "OnboardingLocationResolver"

/**
 * Fetches the device's current location and reverse-geocodes it into a
 * (city, location) pair suitable for display on the guest profile card.
 *
 * `city` is the primary place name (locality / town), `location` is the
 * broader region (state / country). Returns `null` when the location cannot
 * be resolved.
 */
suspend fun resolveCurrentCityAndLocation(context: Context): Pair<String, String>? {
    val location = CurrentLocationProvider.getCurrentLocation(context) ?: run {
        Logger.w(TAG, "resolveCurrentCityAndLocation: no location fix")
        return null
    }

    Logger.d(TAG, "resolveCurrentCityAndLocation: lat=${location.latitude}, lng=${location.longitude}")

    val address = reverseGeocode(context, location.latitude, location.longitude) ?: run {
        Logger.w(TAG, "resolveCurrentCityAndLocation: geocoder returned no address")
        return null
    }
    Logger.d("address: $address")

    val city = address.locality ?: address.subAdminArea ?: address.adminArea
    val region = address.adminArea ?: address.countryName

    if (city.isNullOrBlank() && region.isNullOrBlank()) {
        Logger.w(TAG, "resolveCurrentCityAndLocation: geocoder returned empty city/location")
        return null
    }

    Logger.d(TAG, "resolveCurrentCityAndLocation: city='$city', location='$region'")
    return (city.orEmpty() to region.orEmpty())
}


private suspend fun reverseGeocode(
    context: Context,
    latitude: Double,
    longitude: Double
): android.location.Address? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
    } catch (e: Exception) {
        Logger.e(TAG, "reverseGeocode failed", e)
        null
    }
}
