package com.realeapp.feature.add.domain.usecase

import com.realeapp.feature.add.domain.repository.AddPropertyRepository
import com.realeapp.feature.search.domain.utils.Result

interface UploadImageUseCase {
    suspend operator fun invoke(bytes: ByteArray, filename: String): Result<String>
}

class UploadImageUseCaseImpl(
    private val repository: AddPropertyRepository
) : UploadImageUseCase {

    override suspend fun invoke(bytes: ByteArray, filename: String): Result<String> {
        return repository.uploadImage(bytes, filename)
    }
}
