package com.realeapp.feature.profile.domain.usecase

import com.realeapp.feature.profile.domain.repository.ProfileRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger

interface LogoutUseCase {
    suspend operator fun invoke(sessionId: String): Result<Unit>
}

private const val TAG = "LogoutUseCase"

class LogoutUseCaseImpl(
    private val repository: ProfileRepository
) : LogoutUseCase {
    override suspend fun invoke(sessionId: String): Result<Unit> {
        Logger.d(TAG, "invoke() called: sessionId=$sessionId")
        val result = repository.logout(sessionId)
        when (result) {
            is Result.Success -> Logger.d(TAG, "invoke() success")
            is Result.Error -> Logger.e(TAG, "invoke() error: ${result.message}")
        }
        return result
    }
}
