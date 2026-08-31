package com.example.mytestapp.feature.profile.domain.usecase

import com.example.mytestapp.feature.auth.domain.model.User
import com.example.mytestapp.feature.profile.domain.repository.ProfileRepository
import com.example.mytestapp.feature.search.domain.utils.Result

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
