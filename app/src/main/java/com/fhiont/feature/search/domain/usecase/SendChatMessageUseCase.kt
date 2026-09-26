package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.domain.model.ChatMessage
import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.utils.Result

interface SendChatMessageUseCase {
    suspend operator fun invoke(enquiryId: String, message: String): Result<ChatMessage>
}

class SendChatMessageUseCaseImpl(
    private val repository: EnquiryRepository
) : SendChatMessageUseCase {
    override suspend fun invoke(enquiryId: String, message: String): Result<ChatMessage> {
        return repository.sendChatMessage(enquiryId, message)
    }
}
