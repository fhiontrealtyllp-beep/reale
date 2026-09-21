package com.fhiont.feature.saved.presentation

import com.fhiont.feature.search.domain.model.Property

data class SavedUiState(
    val isLoading: Boolean = true,
    val properties: List<Property> = emptyList(),
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)
