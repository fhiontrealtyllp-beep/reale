package com.realeapp.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.realeapp.feature.add.presentation.AddScreen
import com.realeapp.feature.auth.presentation.LoginScreen
import com.realeapp.feature.auth.presentation.RegisterScreen
import com.realeapp.feature.profile.presentation.MyListingsScreen
import com.realeapp.feature.profile.presentation.ProfileScreen
import com.realeapp.feature.home.presentation.HomeScreen
import com.realeapp.feature.saved.presentation.SavedScreen
import com.realeapp.feature.search.presentation.SearchScreen
import com.realeapp.ui.components.BottomNavBar
import com.realeapp.ui.navigation.AppScreen
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White
import com.realeapp.ui.viewmodel.MainViewModel
import com.realeapp.util.Logger
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainApp"

private enum class AuthScreen {
    Main,
    Login,
    Register
}

@Composable
fun MainApp(mainViewModel: MainViewModel = koinViewModel()) {
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
                AuthScreen.Main -> {
                    val activity = LocalContext.current as? Activity
                    var showExitDialog by remember { mutableStateOf(false) }
                    var showMyListings by rememberSaveable { mutableStateOf(false) }

                    BackHandler(enabled = !showExitDialog && !showMyListings) {
                        showExitDialog = true
                    }

                    BackHandler(enabled = showMyListings) {
                        showMyListings = false
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = White,
                        contentWindowInsets = WindowInsets(0.dp),
                        bottomBar = {
                            BottomNavBar(
                                tabs = AppScreen.all,
                                selectedTab = selectedTab,
                                onTabSelected = { tab ->
                                    showMyListings = false
                                    mainViewModel.selectTab(tab)
                                }
                            )
                        }
                    ) { innerPadding ->
                        // Active tab UI selected by the bottom navigation bar.
                        // Status bars are applied here once; child screens opt out
                        // of automatic insets and fill the remaining area.
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .statusBarsPadding()
                        ) {
                            when (selectedTab) {
                                AppScreen.Home -> HomeScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onSearchClick = { mainViewModel.selectTab(AppScreen.Search) },
                                    onSavedClick = { mainViewModel.selectTab(AppScreen.Saved) },
                                    //onAddClick = { mainViewModel.selectTab(AppScreen.Add) },
                                    onProfileClick = { mainViewModel.selectTab(AppScreen.Profile) }
                                )
                                AppScreen.Search -> SearchScreen(modifier = Modifier.fillMaxSize())
                                AppScreen.Saved -> SavedScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onLoginClick = { authScreen = AuthScreen.Login }
                                )
                               /* AppScreen.Add -> AddScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onLoginClick = { authScreen = AuthScreen.Login }
                                )*/
                                AppScreen.Profile -> {
                                    if (showMyListings) {
                                        MyListingsScreen(
                                            modifier = Modifier.fillMaxSize(),
                                            onBack = { showMyListings = false }
                                        )
                                    } else {
                                        ProfileScreen(
                                            modifier = Modifier.fillMaxSize(),
                                            onLoginClick = { authScreen = AuthScreen.Login },
                                            onMyListingsClick = {
                                                Logger.d(TAG, "My Listings clicked: opening MyListingsScreen")
                                                showMyListings = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showExitDialog) {
                        AlertDialog(
                            onDismissRequest = { showExitDialog = false },
                            title = { Text("Exit") },
                            text = { Text("Do you want to exit the app?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showExitDialog = false
                                        activity?.finishAffinity()
                                    }
                                ) {
                                    Text("Exit")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showExitDialog = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
