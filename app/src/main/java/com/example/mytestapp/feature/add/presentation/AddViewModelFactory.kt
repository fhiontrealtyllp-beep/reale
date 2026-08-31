package com.example.mytestapp.feature.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytestapp.feature.search.data.session.UserSession

class AddViewModelFactory(
    private val userSession: UserSession
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(userSession) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
