package com.example.mytestapp.feature.add.domain.usecase

import com.example.mytestapp.feature.add.domain.repository.AddPropertyRepository
import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.utils.Result

interface GetMyPropertiesUseCase {
    suspend operator fun invoke(userId: String): Result<List<Property>>
}

class GetMyPropertiesUseCaseImpl(
    private val repository: AddPropertyRepository
) : GetMyPropertiesUseCase {
    override suspend fun invoke(userId: String): Result<List<Property>> {
        return repository.getMyProperties(userId)
    }
}
