package com.realeapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.usecase.GetMyEnquiriesUseCase
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyEnquiriesViewModel(
    private val getMyEnquiriesUseCase: GetMyEnquiriesUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEnquiriesUiState())
    val uiState: StateFlow<MyEnquiriesUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        val userId = userSession.getUserId()
        if (userId.isNullOrBlank()) {
            _uiState.value = MyEnquiriesUiState(
                isLoading = false,
                errorMessage = MyEnquiriesStrings.ERROR_NOT_LOGGED_IN
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val result = getMyEnquiriesUseCase(userId)) {
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
