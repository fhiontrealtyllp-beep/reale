package com.example.mytestapp.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytestapp.feature.add.presentation.AddScreen
import com.example.mytestapp.feature.auth.presentation.LoginScreen
import com.example.mytestapp.feature.profile.presentation.ProfileScreen
import com.example.mytestapp.feature.saved.presentation.SavedScreen
import com.example.mytestapp.feature.search.presentation.SearchScreen
import com.example.mytestapp.ui.components.BottomNavBar
import com.example.mytestapp.ui.navigation.AppScreen
import com.example.mytestapp.ui.theme.MyTestAppTheme
import com.example.mytestapp.ui.viewmodel.MainViewModel

@Composable
fun MainApp(mainViewModel: MainViewModel = viewModel()) {
    val selectedTab by mainViewModel.selectedTab.collectAsState()
    var isLoginOpen by rememberSaveable { mutableStateOf(false) }
    var loginSuccessVersion by remember { mutableIntStateOf(0) }

    MyTestAppTheme {
        Crossfade(targetState = isLoginOpen, label = "main-login-crossfade") { showLogin ->
            if (showLogin) {
                LoginScreen(
                    onLoginSuccess = {
                        isLoginOpen = false
                        loginSuccessVersion++
                    },
                    onBack = { isLoginOpen = false }
                )
            } else {
                Scaffold(
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
                            onLoginClick = { isLoginOpen = true },
                            loginSuccessVersion = loginSuccessVersion
                        )
                        AppScreen.Add -> AddScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginClick = { isLoginOpen = true },
                            loginSuccessVersion = loginSuccessVersion
                        )
                        AppScreen.Profile -> ProfileScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginClick = { isLoginOpen = true },
                            loginSuccessVersion = loginSuccessVersion
                        )
                    }
                }
            }
        }
    }
}
