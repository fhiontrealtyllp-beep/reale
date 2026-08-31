package com.example.mytestapp.feature.profile.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mytestapp.feature.profile.data.remote.ProfileRemoteDataSource
import com.example.mytestapp.feature.profile.data.remote.ProfileRemoteDataSourceImpl
import com.example.mytestapp.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.mytestapp.feature.profile.domain.repository.ProfileRepository
import com.example.mytestapp.feature.profile.domain.usecase.GetUserDetailsUseCase
import com.example.mytestapp.feature.profile.domain.usecase.GetUserDetailsUseCaseImpl
import com.example.mytestapp.feature.profile.domain.usecase.LogoutUseCase
import com.example.mytestapp.feature.profile.domain.usecase.LogoutUseCaseImpl
import com.example.mytestapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.example.mytestapp.feature.profile.domain.usecase.UpdateProfileUseCaseImpl
import com.example.mytestapp.feature.profile.domain.usecase.UploadImageUseCase
import com.example.mytestapp.feature.profile.domain.usecase.UploadImageUseCaseImpl
import com.example.mytestapp.feature.profile.presentation.ProfileViewModelFactory
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.data.session.UserSessionImpl

object ProfileModule {

    fun init(context: Context) {
        AppWriteProvider.init(context.applicationContext)
    }

    private val userSession: UserSession by lazy {
        UserSessionImpl
    }

    private val profileRemoteDataSource: ProfileRemoteDataSource by lazy {
        ProfileRemoteDataSourceImpl(userSession)
    }

    private val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(profileRemoteDataSource)
    }

    private val getUserDetailsUseCase: GetUserDetailsUseCase by lazy {
        GetUserDetailsUseCaseImpl(profileRepository)
    }

    private val updateProfileUseCase: UpdateProfileUseCase by lazy {
        UpdateProfileUseCaseImpl(profileRepository)
    }

    private val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCaseImpl(profileRepository)
    }

    private val uploadImageUseCase: UploadImageUseCase by lazy {
        UploadImageUseCaseImpl(profileRepository)
    }

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        ProfileViewModelFactory(
            getUserDetailsUseCase,
            updateProfileUseCase,
            logoutUseCase,
            uploadImageUseCase,
            userSession
        )
    }
}
