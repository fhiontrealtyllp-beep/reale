package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.utils.Result

interface GetEnquiryCountsForPropertiesUseCase {
    suspend operator fun invoke(propertyIds: List<String>): Result<Map<String, Int>>
}

class GetEnquiryCountsForPropertiesUseCaseImpl(
    private val repository: EnquiryRepository
) : GetEnquiryCountsForPropertiesUseCase {
    override suspend fun invoke(propertyIds: List<String>): Result<Map<String, Int>> {
        return repository.getEnquiryCountsForPropertyIds(propertyIds)
    }
}
