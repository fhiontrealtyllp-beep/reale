package com.example.mytestapp.feature.search.domain.repository

import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.model.PropertyFilter
import com.example.mytestapp.feature.search.domain.utils.Result

interface PropertyRepository {
    suspend fun getAllProperties(filter: PropertyFilter?, page: Int, limit: Int): Result<List<Property>>
    suspend fun updateLikeStatus(propertyId: String, isLiked: Boolean): Result<Unit>
}
