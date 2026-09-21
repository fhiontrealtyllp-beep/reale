package com.fhiont.feature.auth.domain.repository

import android.app.Activity
import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.search.domain.utils.Result

interface AuthRepository {
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
