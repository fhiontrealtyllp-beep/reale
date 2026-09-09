package com.realeapp.feature.auth.domain.usecase

import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger

interface VerifyPhoneOtpUseCase {
    suspend operator fun invoke(
        verificationId: String,
        secret: String,
        phone: String,
        name: String,
        dob: String
    ): Result<User>
}

private const val TAG = "VerifyPhoneOtpUseCase"

class VerifyPhoneOtpUseCaseImpl(
    private val repository: AuthRepository
) : VerifyPhoneOtpUseCase {

    override suspend fun invoke(
        verificationId: String,
        secret: String,
        phone: String,
        name: String,
        dob: String
    ): Result<User> {
        Logger.d(TAG, "invoke() called: verificationId=$verificationId, phone=$phone")
        return repository.verifyPhoneOtp(verificationId, secret, phone, name, dob)
    }
}
