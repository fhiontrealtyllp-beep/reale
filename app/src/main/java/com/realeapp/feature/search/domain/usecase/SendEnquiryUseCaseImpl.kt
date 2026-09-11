package com.realeapp.feature.search.domain.usecase

import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.repository.EnquiryRepository
import com.realeapp.feature.search.domain.utils.Result

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
