package com.example.mytestapp.feature.auth.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mytestapp.feature.auth.data.remote.AuthRemoteDataSource
import com.example.mytestapp.feature.auth.data.remote.AuthRemoteDataSourceImpl
import com.example.mytestapp.feature.auth.data.repository.AuthRepositoryImpl
import com.example.mytestapp.feature.auth.domain.repository.AuthRepository
import com.example.mytestapp.feature.auth.domain.usecase.LoginUseCase
import com.example.mytestapp.feature.auth.domain.usecase.LoginUseCaseImpl
import com.example.mytestapp.feature.auth.presentation.LoginViewModelFactory
import com.example.mytestapp.feature.search.data.remote.AppWriteProvider
import com.example.mytestapp.feature.search.data.session.UserSession
import com.example.mytestapp.feature.search.data.session.UserSessionImpl

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
