package com.example.mytestapp.feature.search.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.data.remote.PropertyRemoteDataSource
import com.example.mytestapp.feature.search.data.remote.PropertyRemoteDataSourceImpl
import com.example.mytestapp.feature.search.data.repository.PropertyRepositoryImpl
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.data.session.UserSessionImpl
import com.example.mytestapp.feature.search.domain.repository.PropertyRepository
import com.example.mytestapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.example.mytestapp.feature.search.domain.usecase.GetAllPropertiesUseCaseImpl
import com.example.mytestapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase
import com.example.mytestapp.feature.search.domain.usecase.UpdatePropertyLikeUseCaseImpl
import com.example.mytestapp.feature.search.presentation.SearchViewModelFactory

object SearchModule {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val userSession: UserSession by lazy {
        UserSessionImpl()
    }

    private val appWriteProvider: AppWriteProvider by lazy {
        AppWriteProvider(appContext)
    }

    private val remoteDataSource: PropertyRemoteDataSource by lazy {
        PropertyRemoteDataSourceImpl(appWriteProvider, userSession)
    }

    private val propertyRepository: PropertyRepository by lazy {
        PropertyRepositoryImpl(remoteDataSource)
    }

    private val getAllPropertiesUseCase: GetAllPropertiesUseCase by lazy {
        GetAllPropertiesUseCaseImpl(propertyRepository)
    }

    private val updatePropertyLikeUseCase: UpdatePropertyLikeUseCase by lazy {
        UpdatePropertyLikeUseCaseImpl(propertyRepository)
    }

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        SearchViewModelFactory(getAllPropertiesUseCase, updatePropertyLikeUseCase)
    }
}
