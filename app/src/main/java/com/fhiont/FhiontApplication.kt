package com.fhiont

import android.app.Application
import android.content.pm.ApplicationInfo
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.fhiont.core.seed.OneTimeUtils
import com.fhiont.core.notification.PushTokenManager
import com.fhiont.di.addModule
import com.fhiont.di.appModule
import com.fhiont.di.authModule
import com.fhiont.di.profileModule
import com.fhiont.di.savedModule
import com.fhiont.di.searchModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FhiontApplication : Application() {
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

        val koinApplication = startKoin {
            androidContext(this@FhiontApplication)
            modules(
                appModule,
                authModule,
                searchModule,
                savedModule,
                addModule,
                profileModule
            )
        }
        koinApplication.koin.get<PushTokenManager>().start()

        seedProperties()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun seedProperties() {
        GlobalScope.launch(Dispatchers.IO) {
            val oneTimeUtils = OneTimeUtils(this@FhiontApplication)
            //oneTimeUtils.seedPhpTestUserIfNeeded()
            oneTimeUtils.seedGoaPropertiesIfNeeded()
            oneTimeUtils.seedGoaPromotionalIfNeeded()
            oneTimeUtils.seedGoaFeaturedIfNeeded()

        }
    }
}
