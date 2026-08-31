package com.example.mytestapp.feature.auth.domain.usecase

import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.auth.domain.repository.AuthRepository
import com.example.mytestapp.feature.search.domain.utils.Result

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
