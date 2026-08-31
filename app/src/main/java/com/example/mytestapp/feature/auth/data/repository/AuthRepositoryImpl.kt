package com.example.mytestapp.feature.auth.data.repository

import com.example.mytestapp.feature.auth.data.remote.AuthRemoteDataSource
import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.auth.domain.repository.AuthRepository
import com.example.mytestapp.feature.search.domain.utils.Result

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return remoteDataSource.login(email, password)
    }
}
