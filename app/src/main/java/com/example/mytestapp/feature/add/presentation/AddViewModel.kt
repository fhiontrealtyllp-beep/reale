package com.example.mytestapp.feature.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytestapp.feature.search.data.session.UserSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddViewModel(
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<String>()
    val sideEffect: SharedFlow<String> = _sideEffect.asSharedFlow()

    init {
        load()
    }

    fun load() {
        val userId = userSession.getUserId()
        _uiState.value = AddUiState(
            isLoading = false,
            isLoggedIn = !userId.isNullOrEmpty(),
            dummyText = "Add Screen - dummy text"
        )
    }

    fun refresh() {
        load()
    }

    fun onDummyAction() {
        viewModelScope.launch {
            _sideEffect.emit("Add action triggered")
        }
    }
}
