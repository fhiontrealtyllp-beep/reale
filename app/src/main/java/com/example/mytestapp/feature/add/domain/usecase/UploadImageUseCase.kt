package com.example.mytestapp.feature.add.domain.usecase

import com.example.mytestapp.feature.add.domain.repository.AddPropertyRepository
import com.example.mytestapp.feature.search.domain.utils.Result

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
