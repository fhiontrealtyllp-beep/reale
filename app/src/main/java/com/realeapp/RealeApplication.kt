package com.realeapp

import android.app.Application
import com.realeapp.di.addModule
import com.realeapp.di.appModule
import com.realeapp.di.authModule
import com.realeapp.di.profileModule
import com.realeapp.di.savedModule
import com.realeapp.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RealeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RealeApplication)
            modules(
                appModule,
                authModule,
                searchModule,
                savedModule,
                addModule,
                profileModule
            )
        }
    }
}
