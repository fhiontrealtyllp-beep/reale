package com.realeapp.feature.search.domain.repository

import com.realeapp.feature.search.domain.model.Enquiry
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

interface EnquiryRepository {
    suspend fun sendEnquiry(property: Property, message: String, userId: String?): Result<Unit>
    suspend fun getEnquiriesByUser(userId: String): Result<List<Enquiry>>
}
