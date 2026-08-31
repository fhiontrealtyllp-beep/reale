package com.realeapp.feature.auth.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.realeapp.feature.auth.data.remote.AuthRemoteDataSource
import com.realeapp.feature.auth.data.remote.AuthRemoteDataSourceImpl
import com.realeapp.feature.auth.data.repository.AuthRepositoryImpl
import com.realeapp.feature.auth.domain.repository.AuthRepository
import com.realeapp.feature.auth.domain.usecase.LoginUseCase
import com.realeapp.feature.auth.domain.usecase.LoginUseCaseImpl
import com.realeapp.feature.auth.presentation.LoginViewModelFactory
import com.realeapp.feature.search.data.remote.AppWriteProvider
import com.realeapp.feature.search.data.session.UserSession
import com.realeapp.feature.search.data.session.UserSessionImpl

object LoginModule {

    fun init(context: Context) {
        AppWriteProvider.init(context.applicationContext)
    }

    private val userSession: UserSession by lazy {
        UserSessionImpl
    }

    private val authRemoteDataSource: AuthRemoteDataSource by lazy {
        AuthRemoteDataSourceImpl()
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authRemoteDataSource)
    }

    private val loginUseCase: LoginUseCase by lazy {
        LoginUseCaseImpl(authRepository)
    }

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        LoginViewModelFactory(loginUseCase, userSession)
    }
}
