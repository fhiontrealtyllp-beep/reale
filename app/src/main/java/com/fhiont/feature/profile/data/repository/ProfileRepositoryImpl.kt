package com.fhiont.feature.profile.data.repository

import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.profile.data.remote.ProfileRemoteDataSource
import com.fhiont.feature.profile.domain.repository.ProfileRepository
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger

private const val TAG = "ProfileRepository"

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
        Logger.d(TAG, "logout() called: sessionId=$sessionId")
        return remoteDataSource.logout(sessionId).also { result ->
            when (result) {
                is Result.Success -> Logger.d(TAG, "logout() success")
                is Result.Error -> Logger.e(TAG, "logout() error: ${result.message}")
            }
        }
    }

    override suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String> {
        return remoteDataSource.uploadImage(bytes, filename)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return remoteDataSource.changePassword(currentPassword, newPassword)
    }
}
