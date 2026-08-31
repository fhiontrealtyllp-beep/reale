package com.example.mytestapp.feature.search.domain.usecase

import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.model.PropertyFilter
import com.example.mytestapp.feature.search.domain.repository.PropertyRepository
import com.example.mytestapp.feature.search.domain.utils.Result

interface GetAllPropertiesUseCase {
    suspend operator fun invoke(filter: PropertyFilter?, page: Int, limit: Int): Result<List<Property>>
}

class GetAllPropertiesUseCaseImpl(
    private val repository: PropertyRepository
) : GetAllPropertiesUseCase {
    override suspend fun invoke(filter: PropertyFilter?, page: Int, limit: Int): Result<List<Property>> {
        return repository.getAllProperties(filter, page, limit)
    }
}
