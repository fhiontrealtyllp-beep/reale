package com.realeapp.feature.profile.presentation

import com.realeapp.feature.search.domain.model.Enquiry

data class MyEnquiriesUiState(
    val enquiries: List<Enquiry> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
