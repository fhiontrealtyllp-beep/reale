package com.fhiont.feature.add.domain.usecase

import com.fhiont.feature.add.domain.repository.AddPropertyRepository
import com.fhiont.feature.search.domain.utils.Result

interface DeletePropertyUseCase {
    suspend operator fun invoke(userId: String, propertyId: String): Result<Unit>
}

class DeletePropertyUseCaseImpl(
    private val repository: AddPropertyRepository
) : DeletePropertyUseCase {
    override suspend fun invoke(userId: String, propertyId: String): Result<Unit> {
        return repository.deleteProperty(userId, propertyId)
    }
}
