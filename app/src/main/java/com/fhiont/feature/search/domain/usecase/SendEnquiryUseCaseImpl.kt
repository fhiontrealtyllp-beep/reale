package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.utils.Result

class SendEnquiryUseCaseImpl(
    private val enquiryRepository: EnquiryRepository,
    private val userSession: UserSession
) : SendEnquiryUseCase {
    override suspend fun invoke(property: Property, message: String): Result<Unit> {
        return enquiryRepository.sendEnquiry(
            property = property,
            message = message,
            userId = userSession.getUserId()
        )
    }
}
