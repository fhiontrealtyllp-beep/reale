package com.realeapp.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

private enum class AuthScreen {
    Main,
    Login,
    Register
}

@Composable
fun MainApp(mainViewModel: MainViewModel = viewModel()) {
    val selectedTab by mainViewModel.selectedTab.collectAsStateWithLifecycle()
    var authScreen by rememberSaveable { mutableStateOf(AuthScreen.Main) }

    RealeTheme {
        Crossfade(targetState = authScreen, label = "auth-crossfade") { screen ->
            when (screen) {
                // Full-screen login UI shown above the main tab navigation.
                AuthScreen.Login -> LoginScreen(
                    onLoginSuccess = {
                        Logger.d(TAG, "onLoginSuccess: navigating to main")
                        authScreen = AuthScreen.Main
                    },
                    onBack = { authScreen = AuthScreen.Main },
                    onRegisterClick = { authScreen = AuthScreen.Register }
                )

                // Full-screen registration UI shown above the main tab navigation.
                AuthScreen.Register -> RegisterScreen(
                    onRegisterSuccess = {
                        authScreen = AuthScreen.Main
                    },
                    onBack = { authScreen = AuthScreen.Login },
                    onLoginClick = { authScreen = AuthScreen.Login }
                )

                // Main authenticated-or-guest UI containing bottom-tab navigation.
                AuthScreen.Main -> Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(
                            tabs = AppScreen.all,
                            selectedTab = selectedTab,
                            onTabSelected = mainViewModel::selectTab
                        )
                    }
                ) { innerPadding ->
                    // Active tab UI selected by the bottom navigation bar.
                    when (selectedTab) {
                        AppScreen.Search -> SearchScreen(modifier = Modifier.padding(innerPadding))
                        AppScreen.Saved -> SavedScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginClick = { authScreen = AuthScreen.Login }
                        )
                        AppScreen.Add -> AddScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginClick = { authScreen = AuthScreen.Login }
                        )
                        AppScreen.Profile -> ProfileScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginClick = { authScreen = AuthScreen.Login }
                        )
                    }
                }
            }
        }
    }
}
