package com.fhiont.feature.saved.domain.usecase

import com.fhiont.feature.saved.domain.repository.SavedRepository
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result

interface GetLikedPropertiesUseCase {
    suspend operator fun invoke(userId: String): Result<List<Property>>
}

class GetLikedPropertiesUseCaseImpl(
    private val repository: SavedRepository
) : GetLikedPropertiesUseCase {
    override suspend fun invoke(userId: String): Result<List<Property>> {
        return repository.getLikedProperties(userId)
    }
}
