package com.realeapp.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.realeapp.feature.auth.domain.usecase.RegisterUseCase
import com.realeapp.feature.search.data.session.UserSession

class RegisterViewModelFactory(
    private val registerUseCase: RegisterUseCase,
    private val userSession: UserSession
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(registerUseCase, userSession) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
