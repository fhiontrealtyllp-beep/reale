package com.fhiont.feature.search.data.remote

import com.fhiont.core.network.PhpEnquiryApi
import com.fhiont.feature.search.data.session.UserSession
import com.fhiont.feature.search.domain.model.ChatMessage
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.utils.Result
import com.fhiont.util.Logger

private const val TAG = "EnquiryRemoteDataSource"

class EnquiryRemoteDataSourceImpl(
    private val phpEnquiryApi: PhpEnquiryApi,
    private val userSession: UserSession
) : EnquiryRemoteDataSource {

    override suspend fun sendEnquiry(
        property: Property,
        message: String,
        userId: String?
    ): Result<Unit> {
        Logger.d(TAG, "sendEnquiry: propertyId=${property.id}, userId=$userId")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "sendEnquiry: no session token")
            return Result.Error("User not logged in")
        }
        return phpEnquiryApi.sendEnquiry(property, message, token)
    }

    override suspend fun getEnquiriesByUser(userId: String): Result<List<Enquiry>> {
        Logger.d(TAG, "getEnquiriesByUser: userId=$userId")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "getEnquiriesByUser: no session token")
            return Result.Error("User not logged in")
        }
        return phpEnquiryApi.getEnquiriesByUser(token)
    }

    override suspend fun getEnquiriesByProperty(propertyId: String): Result<List<Enquiry>> {
        Logger.d(TAG, "getEnquiriesByProperty: propertyId=$propertyId")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "getEnquiriesByProperty: no session token")
            return Result.Error("User not logged in")
        }
        return phpEnquiryApi.getEnquiriesByProperty(token, propertyId)
    }

    override suspend fun getEnquiryCountsForPropertyIds(
        propertyIds: List<String>
    ): Result<Map<String, Int>> {
        Logger.d(TAG, "getEnquiryCountsForPropertyIds: propertyIds=${propertyIds.size}")
        if (propertyIds.isEmpty()) {
            return Result.Success(emptyMap())
        }
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "getEnquiryCountsForPropertyIds: no session token")
            return Result.Error("User not logged in")
        }
        return phpEnquiryApi.getEnquiryCountsForPropertyIds(token, propertyIds)
    }

    override suspend fun getChatMessages(enquiryId: String): Result<List<ChatMessage>> {
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "getChatMessages: no session token")
            return Result.Error("User not logged in")
        }
        return phpEnquiryApi.getChatMessages(token, enquiryId)
    }

    override suspend fun sendChatMessage(enquiryId: String, message: String): Result<ChatMessage> {
        Logger.d(TAG, "sendChatMessage: enquiryId=$enquiryId")
        val token = userSession.getUser()?.sessionId
        if (token.isNullOrBlank()) {
            Logger.e(TAG, "sendChatMessage: no session token")
            return Result.Error("User not logged in")
        }
        return phpEnquiryApi.sendChatMessage(token, enquiryId, message)
    }
}
