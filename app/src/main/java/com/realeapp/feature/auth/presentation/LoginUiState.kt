package com.realeapp.feature.auth.presentation

data class LoginUiState(
    val email: String = "john@test.com",
    val password: String = "john@test.com",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)
