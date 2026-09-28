package com.fhiont.feature.add.domain.usecase

import com.fhiont.feature.add.domain.model.PropertyForm
import com.fhiont.feature.add.domain.repository.AddPropertyRepository
import com.fhiont.feature.search.domain.utils.Result

interface UpdatePropertyUseCase {
    suspend operator fun invoke(userId: String, propertyId: String, form: PropertyForm): Result<String>
}

class UpdatePropertyUseCaseImpl(
    private val repository: AddPropertyRepository
) : UpdatePropertyUseCase {

    override suspend fun invoke(userId: String, propertyId: String, form: PropertyForm): Result<String> {
        return repository.updateProperty(userId, propertyId, form)
    }
}
