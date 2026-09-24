package com.fhiont.feature.auth.presentation

data class LoginUiState(
    val email: String = "john@gmail.com",
    val password: String = "John@gmail.com",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
