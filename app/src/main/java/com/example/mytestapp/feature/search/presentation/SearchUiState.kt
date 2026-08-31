package com.example.mytestapp.feature.search.presentation

import com.example.mytestapp.feature.search.domain.model.Property
import com.example.mytestapp.feature.search.domain.model.PropertyFilter

data class SearchUiState(
    val properties: List<Property> = emptyList(),
    val currentFilter: PropertyFilter? = null,
    val isMapView: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasReachedEnd: Boolean = false,
    val errorMessage: String? = null
)
