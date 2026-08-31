package com.example.mytestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SavedUiState(val dummyText: String = "Saved Screen - dummy text")

class SavedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()
}
