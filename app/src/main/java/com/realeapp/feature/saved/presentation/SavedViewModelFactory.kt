package com.realeapp.feature.saved.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.realeapp.core.like.LikeStateManager
import com.realeapp.feature.saved.domain.usecase.GetLikedPropertiesUseCase
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase

class SavedViewModelFactory(
    private val getLikedPropertiesUseCase: GetLikedPropertiesUseCase,
    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase,
    private val userSession: UserSession,
    private val likeStateManager: LikeStateManager = LikeStateManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedViewModel::class.java)) {
            return SavedViewModel(
                getLikedPropertiesUseCase,
                updatePropertyLikeUseCase,
                userSession,
                likeStateManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
