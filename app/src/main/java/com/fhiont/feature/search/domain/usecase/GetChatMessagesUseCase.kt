package com.fhiont.feature.search.domain.usecase

import com.fhiont.feature.search.domain.model.ChatMessage
import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.utils.Result

interface GetChatMessagesUseCase {
    suspend operator fun invoke(enquiryId: String): Result<List<ChatMessage>>
}

class GetChatMessagesUseCaseImpl(
    private val repository: EnquiryRepository
) : GetChatMessagesUseCase {
    override suspend fun invoke(enquiryId: String): Result<List<ChatMessage>> {
        return repository.getChatMessages(enquiryId)
    }
}
