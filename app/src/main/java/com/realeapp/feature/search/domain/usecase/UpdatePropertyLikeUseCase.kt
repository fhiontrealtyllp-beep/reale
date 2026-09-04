package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.domain.repository.PropertyRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger

interface UpdatePropertyLikeUseCase {
    suspend operator fun invoke(propertyId: String, isLiked: Boolean): Result<Unit>
}

class UpdatePropertyLikeUseCaseImpl(
    private val repository: PropertyRepository
) : UpdatePropertyLikeUseCase {
    override suspend fun invoke(propertyId: String, isLiked: Boolean): Result<Unit> {
        Logger.d("UpdatePropertyLikeUseCase", "invoke: propertyId=$propertyId, isLiked=$isLiked")
        return repository.updateLikeStatus(propertyId, isLiked)
    }
}
