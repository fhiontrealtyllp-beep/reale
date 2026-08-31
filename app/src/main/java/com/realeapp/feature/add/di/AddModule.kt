package com.realeapp.feature.add.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.realeapp.feature.add.data.remote.AddPropertyRemoteDataSource
import com.realeapp.feature.add.data.remote.AddPropertyRemoteDataSourceImpl
import com.realeapp.feature.add.data.repository.AddPropertyRepositoryImpl
import com.realeapp.feature.add.domain.repository.AddPropertyRepository
import com.realeapp.feature.add.domain.usecase.AddPropertyUseCase
import com.realeapp.feature.add.domain.usecase.AddPropertyUseCaseImpl
import com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCase
import com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCaseImpl
import com.realeapp.feature.add.domain.usecase.UploadImageUseCase
import com.realeapp.feature.add.domain.usecase.UploadImageUseCaseImpl
import com.realeapp.feature.add.presentation.AddViewModelFactory
import com.realeapp.feature.search.data.remote.AppWriteProvider
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.data.session.UserSessionImpl

object AddModule {

    fun init(context: Context) {
        AppWriteProvider.init(context.applicationContext)
    }

    private val userSession: UserSession by lazy {
        UserSessionImpl
    }

    private val addPropertyRemoteDataSource: AddPropertyRemoteDataSource by lazy {
        AddPropertyRemoteDataSourceImpl()
    }

    private val addPropertyRepository: AddPropertyRepository by lazy {
        AddPropertyRepositoryImpl(addPropertyRemoteDataSource)
    }

    private val addPropertyUseCase: AddPropertyUseCase by lazy {
        AddPropertyUseCaseImpl(addPropertyRepository)
    }

    private val uploadImageUseCase: UploadImageUseCase by lazy {
        UploadImageUseCaseImpl(addPropertyRepository)
    }

    private val getMyPropertiesUseCase: GetMyPropertiesUseCase by lazy {
        GetMyPropertiesUseCaseImpl(addPropertyRepository)
    }

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        AddViewModelFactory(addPropertyUseCase, uploadImageUseCase, getMyPropertiesUseCase, userSession)
    }
}
