package com.realeapp.feature.saved.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realeapp.feature.saved.domain.usecase.GetLikedPropertiesUseCase
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedViewModel(
    private val getLikedPropertiesUseCase: GetLikedPropertiesUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    init {
        load()
        viewModelScope.launch {
            var wasLoggedIn = userSession.user.value != null
            userSession.user.collect { user ->
                val isLoggedIn = user != null
                if (isLoggedIn != wasLoggedIn) {
                    wasLoggedIn = isLoggedIn
                    if (isLoggedIn) {
                        load()
                    } else {
                        _uiState.value = SavedUiState(isLoading = false, isLoggedIn = false)
                    }
                }
            }
        }
    }

    fun load() {
        val userId = userSession.getUserId()
        if (userId.isNullOrEmpty()) {
            _uiState.value = SavedUiState(
                isLoading = false,
                isLoggedIn = false
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            when (val result = getLikedPropertiesUseCase(userId)) {
                is Result.Success -> {
                    _uiState.value = SavedUiState(
                        isLoading = false,
                        properties = result.data,
                        errorMessage = null,
                        isLoggedIn = true
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun refresh() {
        load()
    }
}
