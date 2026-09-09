package com.realeapp.feature.auth.data.repository

import android.app.Activity
import com.realeapp.feature.auth.data.remote.AuthRemoteDataSource
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger

private const val TAG = "AuthRepository"

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        Logger.d(TAG, "login() called: email=$email")
        return remoteDataSource.login(email, password).also { result ->
            when (result) {
                is Result.Success -> Logger.d(TAG, "login() success: userId=${result.data.id}")
                is Result.Error -> Logger.e(TAG, "login() error: ${result.message}")
            }
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        Logger.d(TAG, "register() called: email=$email")
        return remoteDataSource.register(name, email, password).also { result ->
            when (result) {
                is Result.Success -> Logger.d(TAG, "register() success: userId=${result.data.id}")
                is Result.Error -> Logger.e(TAG, "register() error: ${result.message}")
            }
        }
    }

    override suspend fun sendPhoneOtp(activity: Activity?, phone: String): Result<String> {
        Logger.d(TAG, "sendPhoneOtp() called: phone=$phone")
        return remoteDataSource.sendPhoneOtp(activity, phone).also { result ->
            when (result) {
                is Result.Success -> Logger.d(TAG, "sendPhoneOtp() success: verificationId=${result.data}")
                is Result.Error -> Logger.e(TAG, "sendPhoneOtp() error: ${result.message}")
            }
        }
    }

    override suspend fun verifyPhoneOtp(
        verificationId: String,
        secret: String,
        phone: String,
        name: String,
        dob: String
    ): Result<User> {
        Logger.d(TAG, "verifyPhoneOtp() called: verificationId=$verificationId, phone=$phone")
        return remoteDataSource.verifyPhoneOtp(verificationId, secret, phone, name, dob).also { result ->
            when (result) {
                is Result.Success -> Logger.d(TAG, "verifyPhoneOtp() success: userId=${result.data.id}")
                is Result.Error -> Logger.e(TAG, "verifyPhoneOtp() error: ${result.message}")
            }
        }
    }

    override suspend fun signInWithGoogle(activity: Activity): Result<User> {
        Logger.d(TAG, "signInWithGoogle() called")
        return remoteDataSource.signInWithGoogle(activity).also { result ->
            when (result) {
                is Result.Success -> Logger.d(TAG, "signInWithGoogle() success: userId=${result.data.id}")
                is Result.Error -> Logger.e(TAG, "signInWithGoogle() error: ${result.message}")
            }
        }
    }
}
