package com.fhiont.feature.profile.domain.usecase

import com.fhiont.feature.profile.domain.repository.ProfileRepository
import com.fhiont.feature.search.domain.utils.Result

interface ChangePasswordUseCase {
    suspend operator fun invoke(currentPassword: String, newPassword: String): Result<Unit>
}

class ChangePasswordUseCaseImpl(
    private val repository: ProfileRepository
) : ChangePasswordUseCase {
    override suspend fun invoke(currentPassword: String, newPassword: String): Result<Unit> {
        return repository.changePassword(currentPassword, newPassword)
    }
}
