package com.fhiont.feature.search.data.remote

import com.fhiont.feature.search.domain.model.ChatMessage
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result

interface EnquiryRemoteDataSource {
    suspend fun sendEnquiry(
        property: Property,
        message: String,
        userId: String?
    ): Result<Unit>

    suspend fun getEnquiriesByUser(userId: String): Result<List<Enquiry>>

    suspend fun getEnquiriesByProperty(propertyId: String): Result<List<Enquiry>>

    suspend fun getEnquiryCountsForPropertyIds(propertyIds: List<String>): Result<Map<String, Int>>

    suspend fun getChatMessages(enquiryId: String): Result<List<ChatMessage>>

    suspend fun sendChatMessage(enquiryId: String, message: String): Result<ChatMessage>
}
