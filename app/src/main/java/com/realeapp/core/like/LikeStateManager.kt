package com.realeapp.core.like

import com.realeapp.feature.search.domain.model.Property
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared in-memory source of truth for the current user's liked property IDs and a cache of the
 * corresponding [Property] objects. It lets Search and Saved screens stay in sync without each
 * ViewModel having to know about the other.
 *
 * The repository/remote layer remains the single source of truth for persistence; this manager only
 * reflects the local optimistic state and is initialized from the server responses.
 */
object LikeStateManager {

    private val _likedIds = MutableStateFlow<Set<String>>(emptySet())
    val likedIds: StateFlow<Set<String>> = _likedIds.asStateFlow()

    private val _likedPropertyCache = MutableStateFlow<Map<String, Property>>(emptyMap())
    val likedPropertyCache: StateFlow<Map<String, Property>> = _likedPropertyCache.asStateFlow()

    /**
     * Replaces the current liked IDs with [ids]. Call after loading the canonical list from the
     * server.
     */
    fun syncLikedIds(ids: Set<String>) {
        _likedIds.value = ids
    }

    /**
     * Replaces the cached liked properties with [properties]. Call after loading the canonical liked
     * list from the server so newly liked search items can be shown in the Saved list immediately.
     */
    fun syncLikedProperties(properties: List<Property>) {
        _likedPropertyCache.value = properties.associateBy { it.documentId ?: it.id }
    }

    /**
     * Optimistically records a like/unlike for [property]. Returns the resolved property ID used as
     * the key.
     */
    fun setLiked(property: Property, isLiked: Boolean): String {
        val id = property.documentId ?: property.id
        _likedIds.value = if (isLiked) {
            _likedIds.value + id
        } else {
            _likedIds.value - id
        }
        _likedPropertyCache.value = if (isLiked) {
            _likedPropertyCache.value + (id to property.copy(isLiked = true))
        } else {
            _likedPropertyCache.value - id
        }
        return id
    }

    fun isLiked(propertyId: String): Boolean = _likedIds.value.contains(propertyId)
}
