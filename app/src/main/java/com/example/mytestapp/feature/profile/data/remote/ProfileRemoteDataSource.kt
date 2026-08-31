package com.example.mytestapp.feature.profile.data.remote

import android.net.Uri
import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.search.domain.utils.Result

interface ProfileRemoteDataSource {
    suspend fun getUserDetails(): Result<User>
    suspend fun updateProfileField(userId: String, field: String, value: String): Result<String>
    suspend fun logout(sessionId: String): Result<Unit>
    suspend fun uploadImage(bytes: ByteArray, filename: String): Result<String>
}
