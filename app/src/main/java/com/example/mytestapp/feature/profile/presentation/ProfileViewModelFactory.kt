package com.example.mytestapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytestapp.feature.profile.domain.usecase.GetUserDetailsUseCase
import com.example.mytestapp.feature.profile.domain.usecase.LogoutUseCase
import com.example.mytestapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.example.mytestapp.feature.profile.domain.usecase.UploadImageUseCase
import com.example.mytestapp.feature.search.data.session.UserSession

class ProfileViewModelFactory(
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val userSession: UserSession
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                getUserDetailsUseCase,
                updateProfileUseCase,
                logoutUseCase,
                uploadImageUseCase,
                userSession
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
