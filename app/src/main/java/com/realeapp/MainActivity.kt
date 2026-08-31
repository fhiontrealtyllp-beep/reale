package com.realeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.realeapp.feature.add.di.AddModule
import com.realeapp.feature.auth.di.LoginModule
import com.realeapp.feature.profile.di.ProfileModule
import com.realeapp.feature.saved.di.SavedModule
import com.realeapp.feature.search.data.session.UserSessionImpl
import com.realeapp.feature.search.di.SearchModule
import com.realeapp.ui.MainApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserSessionImpl.init(applicationContext)
        SearchModule.init(applicationContext)
        SavedModule.init(applicationContext)
        AddModule.init(applicationContext)
        LoginModule.init(applicationContext)
        ProfileModule.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MainApp()
        }
    }
}