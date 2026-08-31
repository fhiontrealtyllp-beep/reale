package com.example.mytestapp.feature.saved.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mytestapp.feature.saved.data.remote.SavedRemoteDataSource
import com.example.mytestapp.feature.saved.data.remote.SavedRemoteDataSourceImpl
import com.example.mytestapp.feature.saved.data.repository.SavedRepositoryImpl
import com.example.mytestapp.feature.saved.domain.repository.SavedRepository
import com.example.mytestapp.feature.saved.domain.usecase.GetLikedPropertiesUseCase
import com.example.mytestapp.feature.saved.domain.usecase.GetLikedPropertiesUseCaseImpl
import com.example.mytestapp.feature.saved.presentation.SavedViewModelFactory
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.data.session.UserSessionImpl

object SavedModule {

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

    private val savedRemoteDataSource: SavedRemoteDataSource by lazy {
        SavedRemoteDataSourceImpl(appWriteProvider, userSession)
    }

    private val savedRepository: SavedRepository by lazy {
        SavedRepositoryImpl(savedRemoteDataSource)
    }

    private val getLikedPropertiesUseCase: GetLikedPropertiesUseCase by lazy {
        GetLikedPropertiesUseCaseImpl(savedRepository)
    }

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        SavedViewModelFactory(getLikedPropertiesUseCase, userSession)
    }
}
