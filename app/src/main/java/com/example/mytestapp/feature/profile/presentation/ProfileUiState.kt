package com.example.mytestapp.feature.profile.presentation

import com.example.mytestapp.feature.auth.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val updateSuccessMessage: String? = null
)
