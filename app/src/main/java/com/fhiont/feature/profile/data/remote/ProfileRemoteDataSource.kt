package com.fhiont.feature.profile.data.remote

import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.search.domain.utils.Result

interface ProfileRemoteDataSource {
    suspend fun getUserDetails(): Result<User>
    suspend fun updateProfileField(userId: String, field: String, value: String): Result<String>
    suspend fun logout(sessionId: String): Result<Unit>
    suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
}
