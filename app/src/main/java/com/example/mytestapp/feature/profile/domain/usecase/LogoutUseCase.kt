package com.example.mytestapp.feature.profile.domain.usecase

import com.example.mytestapp.feature.profile.domain.repository.ProfileRepository
import com.example.mytestapp.feature.search.domain.utils.Result

interface LogoutUseCase {
    suspend operator fun invoke(sessionId: String): Result<Unit>
}

class LogoutUseCaseImpl(
    private val repository: ProfileRepository
) : LogoutUseCase {
    override suspend fun invoke(sessionId: String): Result<Unit> {
        return repository.logout(sessionId)
    }
}
