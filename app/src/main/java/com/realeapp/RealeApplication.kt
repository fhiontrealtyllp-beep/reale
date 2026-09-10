package com.realeapp

import android.app.Application
import android.content.pm.ApplicationInfo
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.realeapp.core.seed.OneTimeUtils
import com.realeapp.di.addModule
import com.realeapp.di.appModule
import com.realeapp.di.authModule
import com.realeapp.di.profileModule
import com.realeapp.di.savedModule
import com.realeapp.di.searchModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RealeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            FirebaseApp.initializeApp(this)

            val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            if (isDebug) {
                FirebaseAppCheck.getInstance()
                    .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
            }
        } catch (e: Exception) {
            // Firebase is not configured yet (no google-services.json). The app will still
            // build/run, but Firebase features (OTP) will not work until the config is added.
            e.printStackTrace()
        }

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

        seedProperties()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun seedProperties() {
        GlobalScope.launch(Dispatchers.IO) {
            val oneTimeUtils = OneTimeUtils(this@RealeApplication)
            //oneTimeUtils.seedPropertiesIfNeeded()
            //oneTimeUtils.seedFeaturedAndPromotionalPanajiIfNeeded()
            oneTimeUtils.seedNagpurPropertiesIfNeeded()
        }
    }
}
