package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.domain.repository.LocationSuggestionRepository
import com.realeapp.feature.search.domain.utils.Result

interface GetLocationSuggestionsUseCase {
    suspend operator fun invoke(query: String): Result<List<LocationSuggestion>>
}

class GetLocationSuggestionsUseCaseImpl(
    private val repository: LocationSuggestionRepository
) : GetLocationSuggestionsUseCase {
    override suspend fun invoke(query: String): Result<List<LocationSuggestion>> {
        return repository.getSuggestions(query)
    }
}
