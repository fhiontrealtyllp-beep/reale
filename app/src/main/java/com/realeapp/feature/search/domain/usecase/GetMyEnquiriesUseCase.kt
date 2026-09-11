package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.domain.model.Enquiry
import com.realeapp.feature.search.domain.utils.Result

interface GetMyEnquiriesUseCase {
    suspend operator fun invoke(userId: String): Result<List<Enquiry>>
}
