package com.realeapp.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.usecase.SendEnquiryUseCase
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EnquireViewModel(
    private val sendEnquiryUseCase: SendEnquiryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnquireUiState())
    val uiState: StateFlow<EnquireUiState> = _uiState.asStateFlow()

    fun reset() {
        _uiState.value = EnquireUiState()
    }

    fun onMessageChanged(message: String) {
        _uiState.value = _uiState.value.copy(
            message = message,
            messageError = null,
            submitError = null
        )
    }

    fun sendEnquiry(property: Property) {
        val current = _uiState.value
        if (current.message.isBlank()) {
            _uiState.value = current.copy(messageError = EnquiryStrings.ERROR_MESSAGE_REQUIRED)
            return
        }

        _uiState.value = current.copy(
            isLoading = true,
            submitError = null,
            messageError = null
        )

        viewModelScope.launch {
            when (val result = sendEnquiryUseCase(property, _uiState.value.message)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submitError = result.message
                    )
                }
            }
        }
    }
}
