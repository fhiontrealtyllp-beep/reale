package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.domain.model.Enquiry
import com.realeapp.feature.search.domain.repository.EnquiryRepository
import com.realeapp.feature.search.domain.utils.Result

interface GetEnquiriesByPropertyUseCase {
    suspend operator fun invoke(propertyId: String): Result<List<Enquiry>>
}

class GetEnquiriesByPropertyUseCaseImpl(
    private val repository: EnquiryRepository
) : GetEnquiriesByPropertyUseCase {
    override suspend fun invoke(propertyId: String): Result<List<Enquiry>> {
        return repository.getEnquiriesByProperty(propertyId)
    }
}
