package com.realeapp.feature.search.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.realeapp.feature.search.data.remote.AppWriteProvider
import com.realeapp.feature.search.data.remote.PropertyRemoteDataSource
import com.realeapp.feature.search.data.remote.PropertyRemoteDataSourceImpl
import com.realeapp.feature.search.data.repository.PropertyRepositoryImpl
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.data.session.UserSessionImpl
import com.realeapp.feature.search.domain.repository.PropertyRepository
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCase
import com.realeapp.feature.search.domain.usecase.GetAllPropertiesUseCaseImpl
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCase
import com.realeapp.feature.search.domain.usecase.UpdatePropertyLikeUseCaseImpl
import com.realeapp.feature.search.presentation.SearchViewModelFactory

object SearchModule {

    fun init(context: Context) {
        AppWriteProvider.init(context.applicationContext)
    }

    private val userSession: UserSession by lazy {
        UserSessionImpl
    }

    private val remoteDataSource: PropertyRemoteDataSource by lazy {
        PropertyRemoteDataSourceImpl(userSession)
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
