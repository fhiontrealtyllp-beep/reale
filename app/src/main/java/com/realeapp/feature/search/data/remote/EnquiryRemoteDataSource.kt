package com.realeapp.feature.search.data.remote

import com.realeapp.feature.search.domain.model.Enquiry
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.utils.Result

interface EnquiryRemoteDataSource {
    suspend fun sendEnquiry(
        property: Property,
        message: String,
        userId: String?
    ): Result<Unit>

    suspend fun getEnquiriesByUser(userId: String): Result<List<Enquiry>>
}
