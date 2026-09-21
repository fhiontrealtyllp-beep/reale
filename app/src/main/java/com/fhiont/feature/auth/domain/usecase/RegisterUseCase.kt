package com.fhiont.feature.auth.domain.usecase

import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.auth.domain.repository.AuthRepository
import com.fhiont.feature.search.domain.utils.Result

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
