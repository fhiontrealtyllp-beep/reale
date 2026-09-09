package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.repository.PropertyRepository
import com.realeapp.feature.search.domain.utils.Result

interface GetPromotionalPropertiesUseCase {
    suspend operator fun invoke(limit: Int): Result<List<Property>>
}

class GetPromotionalPropertiesUseCaseImpl(
    private val repository: PropertyRepository
) : GetPromotionalPropertiesUseCase {
    override suspend fun invoke(limit: Int): Result<List<Property>> {
        return repository.getPromotionalProperties(limit)
    }
}
