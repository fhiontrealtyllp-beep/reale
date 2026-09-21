package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result

interface SendEnquiryUseCase {
    suspend operator fun invoke(property: Property, message: String): Result<Unit>
}
