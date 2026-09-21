package com.fhiont.feature.add.domain.usecase

import com.fhiont.feature.add.domain.model.PropertyForm
import com.fhiont.feature.add.domain.repository.AddPropertyRepository
import com.fhiont.feature.search.domain.utils.Result

interface AddPropertyUseCase {
    suspend operator fun invoke(userId: String, form: PropertyForm): Result<String>
}

class AddPropertyUseCaseImpl(
    private val repository: AddPropertyRepository
) : AddPropertyUseCase {

    override suspend fun invoke(userId: String, form: PropertyForm): Result<String> {
        return repository.addProperty(userId, form)
    }
}
