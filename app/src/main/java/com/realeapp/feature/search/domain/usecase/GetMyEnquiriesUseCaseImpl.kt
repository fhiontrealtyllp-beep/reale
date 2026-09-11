package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.domain.model.Enquiry
import com.realeapp.feature.search.domain.repository.EnquiryRepository
import com.realeapp.feature.search.domain.utils.Result

class GetMyEnquiriesUseCaseImpl(
    private val enquiryRepository: EnquiryRepository
) : GetMyEnquiriesUseCase {
    override suspend fun invoke(userId: String): Result<List<Enquiry>> {
        return enquiryRepository.getEnquiriesByUser(userId)
    }
}
