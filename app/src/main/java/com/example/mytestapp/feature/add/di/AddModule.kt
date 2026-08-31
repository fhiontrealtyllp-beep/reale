package com.example.mytestapp.feature.add.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
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

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        AddViewModelFactory(userSession)
    }
}
