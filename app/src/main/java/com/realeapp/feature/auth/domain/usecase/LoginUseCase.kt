package com.realeapp.feature.auth.domain.usecase

import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger

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
