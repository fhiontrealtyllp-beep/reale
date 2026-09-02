package com.realeapp.feature.auth.domain.usecase

import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.search.domain.utils.Result

interface RegisterUseCase {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User>
}

class RegisterUseCaseImpl(
    private val repository: AuthRepository
) : RegisterUseCase {
    override suspend fun invoke(name: String, email: String, password: String): Result<User> {
        return repository.register(name, email, password)
    }
}
