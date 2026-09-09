package com.realeapp.feature.auth.presentation

data class PhoneAuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pendingVerificationId: String = "",
    val pendingPhone: String = ""
)
