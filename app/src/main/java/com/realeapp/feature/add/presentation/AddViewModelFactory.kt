package com.realeapp.feature.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.realeapp.feature.add.domain.usecase.AddPropertyUseCase
import com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCase
import com.realeapp.feature.add.domain.usecase.UploadImageUseCase
import com.realeapp.feature.search.data.session.UserSession

class AddViewModelFactory(
    private val addPropertyUseCase: AddPropertyUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val getMyPropertiesUseCase: GetMyPropertiesUseCase,
    private val userSession: UserSession
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(addPropertyUseCase, uploadImageUseCase, getMyPropertiesUseCase, userSession) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
