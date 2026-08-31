package com.realeapp.feature.saved.domain.usecase

import com.realeapp.feature.saved.domain.repository.SavedRepository
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

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
