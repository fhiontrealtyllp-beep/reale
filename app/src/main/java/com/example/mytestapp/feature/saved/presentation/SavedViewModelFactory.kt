package com.example.mytestapp.feature.saved.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytestapp.feature.saved.domain.usecase.GetLikedPropertiesUseCase
import com.example.mytestapp.feature.search.data.session.UserSession

class SavedViewModelFactory(
    private val getLikedPropertiesUseCase: GetLikedPropertiesUseCase,
    private val userSession: UserSession
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedViewModel::class.java)) {
            return SavedViewModel(getLikedPropertiesUseCase, userSession) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
