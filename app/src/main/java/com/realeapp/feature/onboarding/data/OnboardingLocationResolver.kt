package com.realeapp.feature.onboarding.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import com.realeapp.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

private const val TAG = "OnboardingLocationResolver"
private const val LOCATION_TIMEOUT_MS = 10_000L

/**
 * Fetches the device's current location and reverse-geocodes it into a
 * (city, location) pair suitable for display on the guest profile card.
 *
 * `city` is the primary place name (locality / town), `location` is the
 * broader region (state / country). Returns `null` when the location cannot
 * be resolved.
 */
suspend fun resolveCurrentCityAndLocation(context: Context): Pair<String, String>? {
    val location = getCurrentLocation(context) ?: run {
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

private suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.IO) {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!fineGranted && !coarseGranted) return@withContext null

    val client = LocationServices.getFusedLocationProviderClient(context)
    val token = CancellationTokenSource()

    try {
        val current = withTimeoutOrNull(LOCATION_TIMEOUT_MS) {
            suspendCancellableCoroutine<Location?> { cont ->
                client.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    token.token
                ).addOnCompleteListener { task ->
                    if (cont.isActive) {
                        cont.resume(
                            if (task.isSuccessful) task.result else null,
                            onCancellation = { _, _, _ -> }
                        )
                    }
                }
                cont.invokeOnCancellation { token.cancel() }
            }
        }
        token.cancel()
        current ?: Tasks.await(client.lastLocation)
    } catch (e: Exception) {
        Logger.e(TAG, "getCurrentLocation failed", e)
        null
    }
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
