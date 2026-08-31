package com.realeapp.feature.profile.data.remote

import android.net.Uri
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.search.domain.utils.Result

interface ProfileRemoteDataSource {
    suspend fun getUserDetails(): Result<User>
    suspend fun updateProfileField(userId: String, field: String, value: String): Result<String>
    suspend fun logout(sessionId: String): Result<Unit>
    suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String>
}
