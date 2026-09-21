package com.fhiont.feature.profile.domain.usecase

import com.fhiont.feature.auth.domain.model.User
import com.fhiont.feature.profile.domain.repository.ProfileRepository
import com.fhiont.feature.search.domain.utils.Result

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
