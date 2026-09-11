package com.realeapp.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.realeapp.util.Logger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

private const val TAG = "CurrentLocationProvider"

// Timeouts
private const val QUICK_LOCATION_TIMEOUT_MS = 5_000L
private const val UPDATES_TIMEOUT_MS = 10_000L
private const val LAST_LOCATION_TIMEOUT_MS = 3_000L

// Freshness / accuracy thresholds
private const val FRESH_AGE_MS = 30_000L
private const val ACCEPTABLE_AGE_MS = 120_000L
private const val GOOD_ACCURACY_METERS = 50f
private const val ACCEPTABLE_ACCURACY_METERS = 200f

object CurrentLocationProvider {

    suspend fun getCurrentLocation(context: Context): Location? = withContext(Dispatchers.IO) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            Logger.w(TAG, "getCurrentLocation: no location permission")
            return@withContext null
        }

        val priority = if (fineGranted) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }
        Logger.d(TAG, "getCurrentLocation: fine=$fineGranted, coarse=$coarseGranted, priority=$priority")

        val client = LocationServices.getFusedLocationProviderClient(context)

        val quick = getCurrentLocationQuick(client, priority)
        if (quick != null && isGood(quick)) {
            return@withContext quick
        }

        val fromUpdates = getLocationFromUpdates(client, priority)
        if (fromUpdates != null) {
            Logger.d(TAG, "getCurrentLocation: returning location from updates: $fromUpdates")
            return@withContext fromUpdates
        }

        val last = getLastKnownLocation(client)
        if (last != null && isAcceptable(last)) {
            Logger.d(TAG, "getCurrentLocation: returning last known location: $last")
            return@withContext last
        }

        quick?.takeIf { isAcceptable(it) }?.also {
            Logger.d(TAG, "getCurrentLocation: returning quick location: $it")
        }
    }

    private suspend fun getCurrentLocationQuick(
        client: FusedLocationProviderClient,
        priority: Int
    ): Location? {
        Logger.d(TAG, "getCurrentLocationQuick: starting (timeout=${QUICK_LOCATION_TIMEOUT_MS}ms)")
        val token = CancellationTokenSource()
        return try {
            withTimeoutOrNull(QUICK_LOCATION_TIMEOUT_MS) {
                suspendCancellableCoroutine<Location?> { cont ->
                    client.getCurrentLocation(
                        priority,
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
        } finally {
            token.cancel()
        }
    }

    private suspend fun getLocationFromUpdates(
        client: FusedLocationProviderClient,
        priority: Int
    ): Location? {
        Logger.d(TAG, "getLocationFromUpdates: starting (timeout=${UPDATES_TIMEOUT_MS}ms)")
        val request = LocationRequest.Builder(
            priority,
            100L
        ).apply {
            setMinUpdateIntervalMillis(0L)
            setMinUpdateDistanceMeters(0f)
            setWaitForAccurateLocation(true)
            setDurationMillis(UPDATES_TIMEOUT_MS)
        }.build()

        val deferred = CompletableDeferred<Location?>()
        val best = AtomicReference<Location?>(null)

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                Logger.d(TAG, "getLocationFromUpdates: received ${result.locations.size} locations")
                for (location in result.locations) {
                    if (!isAcceptable(location)) {
                        Logger.d(TAG, "getLocationFromUpdates: discarding stale/inaccurate location: $location")
                        continue
                    }

                    if (isGood(location)) {
                        if (deferred.isActive) {
                            deferred.complete(location)
                        }
                        return
                    }

                    val currentBest = best.get()
                    if (currentBest == null || isBetterLocation(location, currentBest)) {
                        best.set(location)
                    }
                }
            }
        }

        return try {
            client.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            ).await()
            Logger.d(TAG, "getLocationFromUpdates: request registered")

            val result = withTimeoutOrNull(UPDATES_TIMEOUT_MS) { deferred.await() }
            result ?: best.get()
        } catch (e: Exception) {
            Logger.e(TAG, "getLocationFromUpdates failed", e)
            best.get()?.also { Logger.d(TAG, "getLocationFromUpdates: returning best after error: $it") }
        } finally {
            try {
                client.removeLocationUpdates(callback).await()
            } catch (e: Exception) {
                Logger.e(TAG, "removeLocationUpdates failed", e)
            }
        }
    }

    private suspend fun getLastKnownLocation(client: FusedLocationProviderClient): Location? {
        Logger.d(TAG, "getLastKnownLocation: starting")
        return try {
            withTimeoutOrNull(LAST_LOCATION_TIMEOUT_MS) {
                client.lastLocation.await()
            }?.also { Logger.d(TAG, "getLastKnownLocation: got $it") }
        } catch (e: Exception) {
            Logger.e(TAG, "lastLocation failed", e)
            null
        }
    }

    private fun isGood(location: Location): Boolean {
        val age = System.currentTimeMillis() - location.time
        return age < FRESH_AGE_MS && accuracyIsGoodEnough(location, GOOD_ACCURACY_METERS)
    }

    private fun isAcceptable(location: Location): Boolean {
        val age = System.currentTimeMillis() - location.time
        return age < ACCEPTABLE_AGE_MS && accuracyIsGoodEnough(location, ACCEPTABLE_ACCURACY_METERS)
    }

    private fun accuracyIsGoodEnough(location: Location, threshold: Float): Boolean {
        return !location.hasAccuracy() || location.accuracy <= threshold
    }

    private fun isBetterLocation(newLocation: Location, currentBest: Location): Boolean {
        if (newLocation.hasAccuracy() && currentBest.hasAccuracy()) {
            if (newLocation.accuracy < currentBest.accuracy) return true
            if (newLocation.accuracy > currentBest.accuracy) return false
        }
        return newLocation.time > currentBest.time
    }
}
