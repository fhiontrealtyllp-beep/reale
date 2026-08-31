package com.example.mytestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AddUiState(val dummyText: String = "Add Screen - dummy text")

class AddViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()
}
