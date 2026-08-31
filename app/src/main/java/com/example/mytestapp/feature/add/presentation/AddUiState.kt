package com.example.mytestapp.feature.add.presentation

data class AddUiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val dummyText: String = "Add Screen - dummy text",
    val errorMessage: String? = null
)
