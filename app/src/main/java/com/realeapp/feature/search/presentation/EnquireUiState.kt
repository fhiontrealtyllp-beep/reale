package com.realeapp.feature.search.presentation

data class EnquireUiState(
    val message: String = "",
    val messageError: String? = null,
    val submitError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
