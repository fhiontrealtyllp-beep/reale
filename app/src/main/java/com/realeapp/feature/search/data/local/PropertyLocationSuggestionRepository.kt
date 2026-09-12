package com.realeapp.feature.search.data.local

import com.realeapp.core.firebase.FirebaseConstants
import com.realeapp.core.firebase.FirebaseProvider
import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.domain.repository.LocationSuggestionRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

private const val TAG = "PropertyLocationSuggestionRepository"
private const val SUGGESTION_LIMIT = 20
private const val CHIP_SUGGESTION_LIMIT = 40

class PropertyLocationSuggestionRepository(
    private val firebaseProvider: FirebaseProvider
) : LocationSuggestionRepository {

    private var cachedCities: List<String>? = null
    private var cachedLocalitiesByCity: Map<String, List<String>>? = null
    private val cityMutex = Mutex()
    private val localityMutex = Mutex()

    private suspend fun ensureCitiesLoaded(): List<String> {
        cachedCities?.let { return it }
        return cityMutex.withLock {
            cachedCities?.let { return it }
            Logger.d(TAG, "ensureCitiesLoaded: fetching from Firestore")
            val snapshot = firebaseProvider.firestore
                .collection(FirebaseConstants.CITIES_COLLECTION)
                .get()
                .await()
            val cities = snapshot.documents
                .mapNotNull { it.getString(FirebaseConstants.CITY_NAME_FIELD)?.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
            Logger.d(TAG, "ensureCitiesLoaded: cached ${cities.size} cities")
            cachedCities = cities
            cities
        }
    }

    private suspend fun ensureLocalitiesLoaded(): Map<String, List<String>> {
        cachedLocalitiesByCity?.let { return it }
        return localityMutex.withLock {
            cachedLocalitiesByCity?.let { return it }
            Logger.d(TAG, "ensureLocalitiesLoaded: fetching from Firestore")
            val snapshot = firebaseProvider.firestore
                .collection(FirebaseConstants.LOCALITIES_COLLECTION)
                .get()
                .await()
            val map = mutableMapOf<String, MutableList<String>>()
            snapshot.documents.forEach { doc ->
                val locality = doc.getString(FirebaseConstants.LOCALITY_NAME_FIELD)?.trim()
                val city = doc.getString(FirebaseConstants.LOCALITY_CITY_FIELD)?.trim()
                if (!locality.isNullOrBlank() && !city.isNullOrBlank()) {
                    val key = city.lowercase()
                    map.getOrPut(key) { mutableListOf() }.add(locality)
                }
            }
            val sorted = map.mapValues { (_, v) -> v.distinct().sorted() }
            val totalCount = sorted.values.sumOf { it.size }
            Logger.d(TAG, "ensureLocalitiesLoaded: cached $totalCount localities across ${sorted.size} cities")
            cachedLocalitiesByCity = sorted
            sorted
        }
    }

    override suspend fun getSuggestions(query: String): Result<List<LocationSuggestion>> {
        Logger.d(TAG, "getSuggestions: query=$query")
        if (query.isBlank()) {
            return Result.Success(emptyList())
        }

        return try {
            val cities = ensureCitiesLoaded()
            val localitiesByCity = ensureLocalitiesLoaded()
            val normalizedQuery = query.trim().lowercase()

            val suggestions = mutableListOf<LocationSuggestion>()

            cities.filter { it.contains(normalizedQuery, ignoreCase = true) }
                .forEach { city ->
                    suggestions.add(
                        LocationSuggestion(
                            placeId = city.lowercase(),
                            primaryText = city,
                            secondaryText = "",
                            fullText = city
                        )
                    )
                }

            localitiesByCity.forEach { (cityKey, localities) ->
                localities.filter { it.contains(normalizedQuery, ignoreCase = true) }
                    .forEach { locality ->
                        val cityDisplay = cities.firstOrNull { it.lowercase() == cityKey } ?: cityKey
                        suggestions.add(
                            LocationSuggestion(
                                placeId = locality.lowercase(),
                                primaryText = cityDisplay,
                                secondaryText = locality,
                                fullText = "$locality, $cityDisplay"
                            )
                        )
                    }
            }

            val result = suggestions
                .distinctBy { it.fullText.lowercase() }
                .sortedBy { it.fullText.lowercase() }
                .take(SUGGESTION_LIMIT)

            Logger.d(TAG, "getSuggestions: found=${result.size}")
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Logger.e(TAG, "getSuggestions: error=${e.message}", e)
            Result.Error(e.message ?: "Failed to load suggestions")
        }
    }

    override suspend fun getCities(query: String): Result<List<String>> {
        Logger.d(TAG, "getCities: query=$query")
        return try {
            val cities = ensureCitiesLoaded()
            val filtered = if (query.isBlank()) {
                cities
            } else {
                cities.filter { it.contains(query.trim(), ignoreCase = true) }
            }
            val result = filtered.take(CHIP_SUGGESTION_LIMIT)
            Logger.d(TAG, "getCities: found=${result.size}")
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Logger.e(TAG, "getCities: error=${e.message}", e)
            Result.Error(e.message ?: "Failed to load cities")
        }
    }

    override suspend fun getLocalities(city: String, query: String): Result<List<String>> {
        Logger.d(TAG, "getLocalities: city=$city, query=$query")
        if (city.isBlank()) {
            return Result.Success(emptyList())
        }
        return try {
            val localitiesByCity = ensureLocalitiesLoaded()
            val cityLocalities = localitiesByCity[city.trim().lowercase()].orEmpty()
            val filtered = if (query.isBlank()) {
                cityLocalities
            } else {
                cityLocalities.filter { it.contains(query.trim(), ignoreCase = true) }
            }
            val result = filtered.take(CHIP_SUGGESTION_LIMIT)
            Logger.d(TAG, "getLocalities: city=$city, found=${result.size}")
            Result.Success(result)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Logger.e(TAG, "getLocalities: error=${e.message}", e)
            Result.Error(e.message ?: "Failed to load localities")
        }
    }
}
