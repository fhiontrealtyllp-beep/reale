package com.fhiont.feature.auth.domain.usecase

import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.auth.domain.repository.AuthRepository
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<User>
}

private const val TAG = "LoginUseCase"

class LoginUseCaseImpl(
    private val repository: AuthRepository
) : LoginUseCase {
    override suspend fun invoke(email: String, password: String): Result<User> {
        Logger.d(TAG, "invoke() called: email=$email")
        val result = repository.login(email, password)
        when (result) {
            is Result.Success -> Logger.d(TAG, "invoke() success: userId=${result.data.id}")
            is Result.Error -> Logger.e(TAG, "invoke() error: ${result.message}")
        }
        return result
    }
}
