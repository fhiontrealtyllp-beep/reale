package com.realeapp.feature.add.presentation

import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.search.domain.model.Property

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
