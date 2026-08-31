package com.realeapp.feature.auth.domain.usecase

import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.search.domain.utils.Result

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<User>
}

class LoginUseCaseImpl(
    private val repository: AuthRepository
) : LoginUseCase {
    override suspend fun invoke(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }
}
