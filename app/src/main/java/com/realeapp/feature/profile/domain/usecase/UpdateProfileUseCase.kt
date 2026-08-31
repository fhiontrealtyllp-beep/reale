package com.realeapp.feature.profile.domain.usecase

import com.realeapp.feature.profile.domain.repository.ProfileRepository
import com.realeapp.feature.search.domain.utils.Result

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
