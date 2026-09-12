package com.realeapp.feature.search.domain.repository

import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.domain.utils.Result

/**
 * Repository contract for fetching city/locality autocomplete suggestions.
 */
interface LocationSuggestionRepository {
    suspend fun getSuggestions(query: String): Result<List<LocationSuggestion>>
    suspend fun getCities(query: String): Result<List<String>>
    suspend fun getLocalities(city: String, query: String): Result<List<String>>
}
