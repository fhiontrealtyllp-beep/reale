package com.realeapp.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.realeapp.core.like.LikeStateManager
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase

class SearchViewModelFactory(
    private val getAllPropertiesUseCase: GetAllPropertiesUseCase,
    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase,
    private val likeStateManager: LikeStateManager = LikeStateManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(getAllPropertiesUseCase, updatePropertyLikeUseCase, likeStateManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
