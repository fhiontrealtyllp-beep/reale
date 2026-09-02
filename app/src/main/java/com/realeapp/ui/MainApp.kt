package com.realeapp.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.realeapp.feature.add.presentation.AddScreen
import com.realeapp.feature.auth.presentation.LoginScreen
import com.realeapp.feature.auth.presentation.RegisterScreen
import com.realeapp.feature.profile.presentation.ProfileScreen
import com.realeapp.feature.saved.presentation.SavedScreen
import com.realeapp.feature.search.presentation.SearchScreen
import com.realeapp.ui.components.BottomNavBar
import com.realeapp.ui.navigation.AppScreen
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.viewmodel.MainViewModel
import com.realeapp.util.Logger

private const val TAG = "MainApp"

@Composable
fun MainApp(mainViewModel: MainViewModel = viewModel()) {
    val selectedTab by mainViewModel.selectedTab.collectAsState()
    var authScreen by rememberSaveable { mutableStateOf("main") }

    RealeTheme {
        Crossfade(targetState = authScreen, label = "auth-crossfade") { screen ->
            when (screen) {
                "login" -> LoginScreen(
                    onLoginSuccess = {
                        Logger.d(TAG, "onLoginSuccess: navigating to main")
                        authScreen = "main"
                    },
                    onBack = { authScreen = "main" },
                    onRegisterClick = { authScreen = "register" }
                )

                "register" -> RegisterScreen(
                    onRegisterSuccess = {
                        authScreen = "main"
                    },
                    onBack = { authScreen = "login" },
                    onLoginClick = { authScreen = "login" }
                )

                else -> Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(
                            tabs = AppScreen.all,
                            selectedTab = selectedTab,
                            onTabSelected = mainViewModel::selectTab
                        )
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        AppScreen.Search -> SearchScreen(modifier = Modifier.padding(innerPadding))
                        AppScreen.Saved -> SavedScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginClick = { authScreen = "login" }
                        )
                        AppScreen.Add -> AddScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginClick = { authScreen = "login" }
                        )
                        AppScreen.Profile -> ProfileScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginClick = { authScreen = "login" }
                        )
                    }
                }
            }
        }
    }
}
