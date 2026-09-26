package com.fhiont.feature.search.domain.model

data class ChatMessage(
    val id: String,
    val enquiryId: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val createdAt: String
)
