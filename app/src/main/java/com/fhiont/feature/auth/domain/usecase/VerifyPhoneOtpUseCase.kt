package com.fhiont.feature.auth.domain.usecase

import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.auth.domain.repository.AuthRepository
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger

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
