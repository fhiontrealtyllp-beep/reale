package com.realeapp.feature.auth.domain.usecase

import android.app.Activity
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.search.domain.utils.Result
import com.realeapp.util.Logger

interface SignInWithGoogleUseCase {
    suspend operator fun invoke(activity: Activity): Result<User>
}

private const val TAG = "SignInWithGoogleUseCase"

class SignInWithGoogleUseCaseImpl(
    private val repository: AuthRepository
) : SignInWithGoogleUseCase {

    override suspend fun invoke(activity: Activity): Result<User> {
        Logger.d(TAG, "invoke() called")
        return repository.signInWithGoogle(activity)
    }
}
