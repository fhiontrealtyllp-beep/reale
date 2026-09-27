package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.utils.Result

interface DeleteEnquiryThreadUseCase {
    suspend operator fun invoke(propertyId: String, userId: String): Result<Unit>
}

class DeleteEnquiryThreadUseCaseImpl(
    private val repository: EnquiryRepository
) : DeleteEnquiryThreadUseCase {
    override suspend fun invoke(propertyId: String, userId: String): Result<Unit> {
        return repository.deleteEnquiryThread(propertyId, userId)
    }
}
