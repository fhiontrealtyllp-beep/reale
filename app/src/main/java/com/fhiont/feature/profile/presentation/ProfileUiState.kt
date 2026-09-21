package com.fhiont.feature.profile.presentation

import com.fhiont.feature.auth.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isImageUploading: Boolean = false,
    val updatingField: String? = null,
    val isLoggedIn: Boolean = false,
    val hasDraft: Boolean = false,
    val errorMessage: String? = null,
    val updateSuccessMessage: String? = null
)
