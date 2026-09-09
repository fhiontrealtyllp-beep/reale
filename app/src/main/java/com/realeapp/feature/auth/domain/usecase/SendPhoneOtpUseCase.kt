package com.realeapp.feature.auth.domain.usecase

import android.app.Activity
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger

interface SendPhoneOtpUseCase {
    suspend operator fun invoke(activity: Activity?, phone: String): Result<String>
}

private const val TAG = "SendPhoneOtpUseCase"

class SendPhoneOtpUseCaseImpl(
    private val repository: AuthRepository
) : SendPhoneOtpUseCase {

    override suspend fun invoke(activity: Activity?, phone: String): Result<String> {
        Logger.d(TAG, "invoke() called: phone=$phone")
        return repository.sendPhoneOtp(activity, phone)
    }
}
