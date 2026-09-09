package com.realeapp.feature.auth.data.remote

import android.app.Activity
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.search.domain.utils.Result

interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun sendPhoneOtp(activity: Activity?, phone: String): Result<String>
    suspend fun verifyPhoneOtp(
        verificationId: String,
        secret: String,
        phone: String,
        name: String,
        dob: String
    ): Result<User>
    suspend fun signInWithGoogle(activity: Activity): Result<User>
}
