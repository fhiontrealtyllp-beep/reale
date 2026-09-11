package com.realeapp.feature.profile.presentation

internal data class MyListingsUiState(
    val listings: List<MyListing> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
