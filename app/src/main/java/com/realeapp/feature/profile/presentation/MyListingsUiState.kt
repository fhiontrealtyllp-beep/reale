package com.realeapp.feature.profile.presentation

import com.realeapp.feature.search.domain.model.Property

internal data class MyListingsUiState(
    val listings: List<MyListing> = emptyList(),
    val properties: List<Property> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
