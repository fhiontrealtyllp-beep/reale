package com.realeapp.feature.profile.domain.usecase

import com.realeapp.feature.profile.domain.repository.ProfileRepository
import com.realeapp.feature.search.domain.utils.Result

interface UploadImageUseCase {
    suspend operator fun invoke(bytes: ByteArray, filename: String): Result<String>
}

class UploadImageUseCaseImpl(
    private val repository: ProfileRepository
) : UploadImageUseCase {
    override suspend fun invoke(bytes: ByteArray, filename: String): Result<String> {
        return repository.uploadImage(bytes, filename)
    }
}
