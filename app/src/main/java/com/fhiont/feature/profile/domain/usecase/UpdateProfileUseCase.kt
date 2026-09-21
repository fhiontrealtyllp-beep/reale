package com.fhiont.feature.profile.domain.usecase

import com.fhiont.feature.profile.domain.repository.ProfileRepository
import com.fhiont.feature.search.domain.utils.Result

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
