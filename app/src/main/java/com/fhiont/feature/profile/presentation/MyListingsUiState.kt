package com.fhiont.feature.profile.presentation

import com.fhiont.feature.search.domain.model.Property

internal data class MyListingsUiState(
    val listings: List<MyListing> = emptyList(),
    val properties: List<Property> = emptyList(),
    val isLoading: Boolean = false,
    val deletingPropertyId: String? = null,
    val errorMessage: String? = null
)
