package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.repository.PropertyRepository
import com.realeapp.feature.search.domain.utils.Result

interface GetFeaturedPropertiesUseCase {
    suspend operator fun invoke(limit: Int): Result<List<Property>>
}

class GetFeaturedPropertiesUseCaseImpl(
    private val repository: PropertyRepository
) : GetFeaturedPropertiesUseCase {
    override suspend fun invoke(limit: Int): Result<List<Property>> {
        return repository.getFeaturedProperties(limit)
    }
}
