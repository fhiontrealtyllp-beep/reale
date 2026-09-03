package com.realeapp.feature.profile.presentation

import com.realeapp.feature.auth.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isImageUploading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val updateSuccessMessage: String? = null
)
