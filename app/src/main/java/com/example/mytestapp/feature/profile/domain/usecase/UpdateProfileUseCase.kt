package com.example.mytestapp.feature.profile.domain.usecase

import com.example.mytestapp.feature.profile.domain.repository.ProfileRepository
import com.example.mytestapp.feature.search.domain.utils.Result

interface UpdateProfileUseCase {
    suspend operator fun invoke(userId: String, field: String, value: String): Result<String>
}

class UpdateProfileUseCaseImpl(
    private val repository: ProfileRepository
) : UpdateProfileUseCase {
    override suspend fun invoke(userId: String, field: String, value: String): Result<String> {
        return repository.updateProfileField(userId, field, value)
    }
}
