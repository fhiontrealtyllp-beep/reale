package com.example.mytestapp.feature.add.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mytestapp.feature.add.data.remote.AddPropertyRemoteDataSource
import com.example.mytestapp.feature.add.data.remote.AddPropertyRemoteDataSourceImpl
import com.example.mytestapp.feature.add.data.repository.AddPropertyRepositoryImpl
import com.example.mytestapp.feature.add.domain.repository.AddPropertyRepository
import com.example.mytestapp.feature.add.domain.usecase.AddPropertyUseCase
import com.example.mytestapp.feature.add.domain.usecase.AddPropertyUseCaseImpl
import com.example.mytestapp.feature.add.presentation.AddViewModelFactory
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.data.session.UserSessionImpl

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

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        AddViewModelFactory(addPropertyUseCase, userSession)
    }
}
