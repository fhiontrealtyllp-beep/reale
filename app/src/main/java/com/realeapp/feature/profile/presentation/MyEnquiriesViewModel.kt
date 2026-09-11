package com.realeapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.usecase.GetEnquiriesByPropertyUseCase
import com.realeapp.feature.search.domain.usecase.GetMyEnquiriesUseCase
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyEnquiriesViewModel(
    private val getMyEnquiriesUseCase: GetMyEnquiriesUseCase,
    private val getEnquiriesByPropertyUseCase: GetEnquiriesByPropertyUseCase,
    private val userSession: UserSession,
    private val filterPropertyId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEnquiriesUiState())
    val uiState: StateFlow<MyEnquiriesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = if (filterPropertyId != null) {
                getEnquiriesByPropertyUseCase(filterPropertyId)
            } else {
                val userId = userSession.getUserId()
                if (userId.isNullOrBlank()) {
                    _uiState.value = MyEnquiriesUiState(
                        isLoading = false,
                        errorMessage = MyEnquiriesStrings.ERROR_NOT_LOGGED_IN
                    )
                    return@launch
                }
                getMyEnquiriesUseCase(userId)
            }

            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        enquiries = result.data,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}
