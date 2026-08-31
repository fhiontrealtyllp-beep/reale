package com.example.mytestapp.feature.add.presentation

import com.example.mytestapp.feature.add.domain.model.PropertyForm
import com.example.mytestapp.feature.search.domain.model.Property

data class AddUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val form: PropertyForm = PropertyForm(),
    val fieldErrors: List<String> = emptyList(),
    val isSubmitting: Boolean = false,
    val isSubmitSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isUploadingImage: Boolean = false,
    val imageUploadError: String? = null,
    val myProperties: List<Property> = emptyList(),
    val isLoadingMyProperties: Boolean = false,
    val myPropertiesError: String? = null,
    val isShowingAddForm: Boolean = false
)
