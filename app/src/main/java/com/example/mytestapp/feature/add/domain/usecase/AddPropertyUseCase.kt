package com.example.mytestapp.feature.add.domain.usecase

import com.example.mytestapp.feature.add.domain.model.PropertyForm
import com.example.mytestapp.feature.add.domain.repository.AddPropertyRepository
import com.example.mytestapp.feature.search.domain.utils.Result

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
