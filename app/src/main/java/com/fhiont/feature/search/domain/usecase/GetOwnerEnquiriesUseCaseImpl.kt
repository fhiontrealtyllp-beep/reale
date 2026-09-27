package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.utils.Result

class GetOwnerEnquiriesUseCaseImpl(
    private val enquiryRepository: EnquiryRepository
) : GetOwnerEnquiriesUseCase {
    override suspend fun invoke(userId: String): Result<List<Enquiry>> {
        return enquiryRepository.getEnquiriesByOwner(userId)
    }
}
