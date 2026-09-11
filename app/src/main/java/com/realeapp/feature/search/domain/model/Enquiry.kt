package com.realeapp.feature.search.domain.model

data class Enquiry(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val propertyLocation: String,
    val agentPhone: String,
    val message: String,
    val userId: String?,
    val status: String,
    val createdAt: String
)
