package com.fhiont.feature.auth.domain.usecase

import android.app.Activity
import com.fhiont.feature.auth.domain.repository.AuthRepository
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger

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
