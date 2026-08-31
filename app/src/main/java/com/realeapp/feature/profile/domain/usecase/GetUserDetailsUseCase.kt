package com.realeapp.feature.profile.domain.usecase

import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.profile.domain.repository.ProfileRepository
import com.realeapp.feature.search.domain.utils.Result

interface GetUserDetailsUseCase {
    suspend operator fun invoke(): Result<User>
}

class GetUserDetailsUseCaseImpl(
    private val repository: ProfileRepository
) : GetUserDetailsUseCase {
    override suspend fun invoke(): Result<User> {
        return repository.getUserDetails()
    }
}
