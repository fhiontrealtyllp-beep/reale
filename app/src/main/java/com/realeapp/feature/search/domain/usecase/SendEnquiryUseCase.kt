package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

interface SendEnquiryUseCase {
    suspend operator fun invoke(property: Property, message: String): Result<Unit>
}
