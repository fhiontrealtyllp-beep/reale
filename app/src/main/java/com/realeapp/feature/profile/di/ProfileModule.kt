package com.realeapp.feature.profile.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.realeapp.feature.profile.data.remote.ProfileRemoteDataSource
import com.realeapp.feature.profile.data.remote.ProfileRemoteDataSourceImpl
import com.realeapp.feature.profile.data.repository.ProfileRepositoryImpl
import com.realeapp.feature.profile.domain.repository.ProfileRepository
import com.realeapp.feature.profile.domain.usecase.GetUserDetailsUseCase
import com.realeapp.feature.profile.domain.usecase.GetUserDetailsUseCaseImpl
import com.realeapp.feature.profile.domain.usecase.LogoutUseCase
import com.realeapp.feature.profile.domain.usecase.LogoutUseCaseImpl
import com.realeapp.feature.profile.domain.usecase.UpdateProfileUseCase
import com.realeapp.feature.profile.domain.usecase.UpdateProfileUseCaseImpl
import com.realeapp.feature.profile.domain.usecase.UploadImageUseCase
import com.realeapp.feature.profile.domain.usecase.UploadImageUseCaseImpl
import com.realeapp.feature.profile.presentation.ProfileViewModelFactory
import com.realeapp.feature.search.data.remote.AppWriteProvider
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.data.session.UserSessionImpl

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
