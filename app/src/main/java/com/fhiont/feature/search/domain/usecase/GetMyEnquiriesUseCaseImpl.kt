package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.utils.Result

class GetMyEnquiriesUseCaseImpl(
    private val enquiryRepository: EnquiryRepository
) : GetMyEnquiriesUseCase {
    override suspend fun invoke(userId: String): Result<List<Enquiry>> {
        return enquiryRepository.getEnquiriesByUser(userId)
    }
}
