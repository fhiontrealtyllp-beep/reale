package com.realeapp.feature.auth.data.repository

import com.realeapp.feature.auth.data.remote.AuthRemoteDataSource
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.search.domain.utils.Result

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return remoteDataSource.login(email, password)
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return remoteDataSource.register(name, email, password)
    }
}
