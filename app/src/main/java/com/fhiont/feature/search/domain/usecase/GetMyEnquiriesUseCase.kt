package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.utils.Result

interface GetMyEnquiriesUseCase {
    suspend operator fun invoke(userId: String): Result<List<Enquiry>>
}
