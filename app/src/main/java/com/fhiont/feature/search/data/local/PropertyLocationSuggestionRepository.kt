package com.fhiont.feature.search.data.local

import com.fhiont.core.network.LocationCatalog
import com.fhiont.core.network.PhpPropertyApi
import com.fhiont.feature.search.domain.model.LocationSuggestion
import com.fhiont.feature.search.domain.repository.LocationSuggestionRepository
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val TAG = "PropertyLocationSuggestionRepository"
private const val SUGGESTION_LIMIT = 20
private const val CHIP_SUGGESTION_LIMIT = 40

class PropertyLocationSuggestionRepository(
    private val phpPropertyApi: PhpPropertyApi
) : LocationSuggestionRepository {

    private var cachedCities: List<String>? = null
    private var cachedLocalitiesByCity: Map<String, List<String>>? = null
    private val catalogMutex = Mutex()

    /** Clears the cached catalog so the next read refetches from the server. */
    override fun invalidateCache() {
        cachedCities = null
        cachedLocalitiesByCity = null
    }

    private suspend fun ensureCatalogLoaded(): LocationCatalog {
        val cities = cachedCities
        val localities = cachedLocalitiesByCity
        if (cities != null && localities != null) {
            return LocationCatalog(cities, localities)
        }
        return catalogMutex.withLock {
            val cachedCitiesNow = cachedCities
            val cachedLocalitiesNow = cachedLocalitiesByCity
            if (cachedCitiesNow != null && cachedLocalitiesNow != null) {
                return@withLock LocationCatalog(cachedCitiesNow, cachedLocalitiesNow)
            }
            Logger.d(TAG, "ensureCatalogLoaded: fetching from PHP locations API")
            when (val result = phpPropertyApi.getLocations()) {
                is Result.Success -> {
                    val catalog = LocationCatalog(
                        cities = result.data.cities
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .distinct()
                            .sorted(),
                        localitiesByCity = result.data.localitiesByCity
                            .mapValues { (_, v) ->
                                v.map { it.trim() }
                                    .filter { it.isNotBlank() }
                                    .distinct()
                                    .sorted()
                            }
                    )
                    Logger.d(
                        TAG,
                        "ensureCatalogLoaded: cached ${catalog.cities.size} cities, " +
                            "${catalog.localitiesByCity.values.sumOf { it.size }} localities"
                    )
                    cachedCities = catalog.cities
                    cachedLocalitiesByCity = catalog.localitiesByCity
                    catalog
                }
                is Result.Error -> {
                    Logger.e(TAG, "ensureCatalogLoaded: failed ${result.message}")
                    throw LocationCatalogException(result.message)
                }
            }
        }
    }

    override suspend fun getSuggestions(query: String): Result<List<LocationSuggestion>> {
        Logger.d(TAG, "getSuggestions: query=$query")
        if (query.isBlank()) {
            return Result.Success(emptyList())
        }

        return try {
            val catalog = ensureCatalogLoaded()
            val cities = catalog.cities
            val localitiesByCity = catalog.localitiesByCity
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
                                placeId = "$cityKey|${locality.lowercase()}",
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
            val cities = ensureCatalogLoaded().cities
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
            val localitiesByCity = ensureCatalogLoaded().localitiesByCity
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

private class LocationCatalogException(message: String?) : Exception(message)
