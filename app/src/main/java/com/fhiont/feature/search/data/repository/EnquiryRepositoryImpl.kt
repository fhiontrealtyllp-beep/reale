package com.fhiont.feature.search.data.repository

import com.fhiont.feature.search.data.remote.EnquiryRemoteDataSource
import com.fhiont.feature.search.domain.model.ChatMessage
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.repository.EnquiryRepository
import com.fhiont.feature.search.domain.utils.Result

class EnquiryRepositoryImpl(
    private val remoteDataSource: EnquiryRemoteDataSource
) : EnquiryRepository {
    override suspend fun sendEnquiry(
        property: Property,
        message: String,
        userId: String?
    ): Result<Unit> {
        return remoteDataSource.sendEnquiry(property, message, userId)
    }

    override suspend fun getEnquiriesByUser(userId: String): Result<List<Enquiry>> {
        return remoteDataSource.getEnquiriesByUser(userId)
    }

    override suspend fun getEnquiriesByProperty(propertyId: String): Result<List<Enquiry>> {
        return remoteDataSource.getEnquiriesByProperty(propertyId)
    }

    override suspend fun getEnquiryCountsForPropertyIds(propertyIds: List<String>): Result<Map<String, Int>> {
        return remoteDataSource.getEnquiryCountsForPropertyIds(propertyIds)
    }

    override suspend fun getChatMessages(enquiryId: String): Result<List<ChatMessage>> {
        return remoteDataSource.getChatMessages(enquiryId)
    }

    override suspend fun sendChatMessage(enquiryId: String, message: String): Result<ChatMessage> {
        return remoteDataSource.sendChatMessage(enquiryId, message)
    }
}
