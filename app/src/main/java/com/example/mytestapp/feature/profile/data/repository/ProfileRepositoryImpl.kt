package com.example.mytestapp.feature.profile.data.repository

import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.profile.data.remote.ProfileRemoteDataSource
import com.example.mytestapp.feature.profile.domain.repository.ProfileRepository
import com.example.mytestapp.feature.search.domain.utils.Result

class ProfileRepositoryImpl(
    private val remoteDataSource: ProfileRemoteDataSource
) : ProfileRepository {
    override suspend fun getUserDetails(): Result<User> {
        return remoteDataSource.getUserDetails()
    }

    override suspend fun updateProfileField(
        userId: String,
        field: String,
        value: String
    ): Result<String> {
        return remoteDataSource.updateProfileField(userId, field, value)
    }

    override suspend fun logout(sessionId: String): Result<Unit> {
        return remoteDataSource.logout(sessionId)
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        return remoteDataSource.uploadImage(bytes, filename)
    }
}
