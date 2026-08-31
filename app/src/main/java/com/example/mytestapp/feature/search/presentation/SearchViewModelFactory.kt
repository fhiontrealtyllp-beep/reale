package com.example.mytestapp.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mytestapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.example.mytestapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase

class SearchViewModelFactory(
    private val getAllPropertiesUseCase: GetAllPropertiesUseCase,
    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(getAllPropertiesUseCase, updatePropertyLikeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
