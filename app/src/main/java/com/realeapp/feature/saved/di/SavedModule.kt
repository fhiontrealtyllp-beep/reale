package com.realeapp.feature.saved.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.realeapp.core.like.LikeStateManager
import com.realeapp.feature.saved.data.remote.SavedRemoteDataSource
import com.realeapp.feature.saved.data.remote.SavedRemoteDataSourceImpl
import com.realeapp.feature.saved.data.repository.SavedRepositoryImpl
import com.realeapp.feature.saved.domain.repository.SavedRepository
import com.realeapp.feature.saved.domain.usecase.GetLikedPropertiesUseCase
import com.realeapp.feature.saved.domain.usecase.GetLikedPropertiesUseCaseImpl
import com.realeapp.feature.saved.presentation.SavedViewModelFactory
import com.realeapp.feature.search.data.remote.AppWriteProvider
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.data.session.UserSessionImpl
import com.realeapp.feature.search.di.SearchModule

object SavedModule {

    fun init(context: Context) {
        AppWriteProvider.init(context.applicationContext)
    }

    private val userSession: UserSession by lazy {
        UserSessionImpl
    }

    private val savedRemoteDataSource: SavedRemoteDataSource by lazy {
        SavedRemoteDataSourceImpl(userSession)
    }

    private val savedRepository: SavedRepository by lazy {
        SavedRepositoryImpl(savedRemoteDataSource)
    }

    private val getLikedPropertiesUseCase: GetLikedPropertiesUseCase by lazy {
        GetLikedPropertiesUseCaseImpl(savedRepository)
    }

    private val updatePropertyLikeUseCase by lazy {
        SearchModule.updatePropertyLikeUseCase
    }

    private val likeStateManager: LikeStateManager by lazy {
        LikeStateManager
    }

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        SavedViewModelFactory(
            getLikedPropertiesUseCase,
            updatePropertyLikeUseCase,
            userSession,
            likeStateManager
        )
    }
}
