package com.realeapp.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realeapp.feature.auth.presentation.PhoneAuthEffect
import androidx.compose.ui.Modifier
import com.realeapp.core.theme.ThemeMode
import com.realeapp.feature.add.presentation.AddScreen
import com.realeapp.feature.auth.presentation.EnterNumberScreen
import com.realeapp.feature.auth.presentation.LoginScreen
import com.realeapp.feature.auth.presentation.PhoneAuthViewModel
import com.realeapp.feature.auth.presentation.RegisterScreen
import com.realeapp.feature.auth.presentation.VerifyNumberScreen
import com.realeapp.feature.auth.presentation.WelcomeScreen
import com.realeapp.feature.city.presentation.CityScreen
import com.realeapp.feature.profile.presentation.MyEnquiriesScreen
import com.realeapp.feature.profile.presentation.MyListingsScreen
import com.realeapp.feature.profile.presentation.ProfileScreen
import com.realeapp.feature.home.presentation.HomeScreen
import com.realeapp.feature.saved.presentation.SavedScreen
import com.realeapp.feature.search.presentation.SearchScreen
import com.realeapp.feature.onboarding.presentation.OnboardingScreen
import com.realeapp.ui.components.BottomNavBar
import com.realeapp.ui.navigation.AppScreen
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.viewmodel.MainViewModel
import com.realeapp.util.Logger
import org.koin.androidx.compose.koinViewModel

private const val TAG = "MainApp"

private enum class AuthScreen {
    Main,
    Welcome,
    EnterNumber,
    VerifyNumber,
    Login,
    Register
}

@Composable
fun MainApp(mainViewModel: MainViewModel = koinViewModel()) {
    val selectedTab by mainViewModel.selectedTab.collectAsStateWithLifecycle()
    val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle()
    var authScreen by rememberSaveable { mutableStateOf(AuthScreen.Main) }
    val phoneAuthViewModel: PhoneAuthViewModel = koinViewModel()
    val showOnboarding by mainViewModel.showOnboarding.collectAsStateWithLifecycle()

    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    LaunchedEffect(phoneAuthViewModel) {
        phoneAuthViewModel.effect.collect { effect ->
            if (effect == PhoneAuthEffect.NavigateToMain) {
                authScreen = AuthScreen.Main
            }
        }
    }

    RealeTheme(darkTheme = darkTheme) {
        if (showOnboarding) {
            var showCityScreen by rememberSaveable { mutableStateOf(false) }

            BackHandler(enabled = showCityScreen) {
                showCityScreen = false
            }

            if (showCityScreen) {
                CityScreen(
                    onCitySelected = { city -> mainViewModel.selectCity(city) }
                )
            } else {
                OnboardingScreen(
                    onComplete = { mainViewModel.completeOnboarding() },
                    onSkipToCity = { showCityScreen = true },
                    onLocationResolved = { city, location ->
                        mainViewModel.saveOnboardingLocation(city, location)
                    }
                )
            }
        } else {
            Crossfade(targetState = authScreen, label = "auth-crossfade") { screen ->
                when (screen) {
                // Auth landing UI offering Google, mobile-number, or guest entry.
                AuthScreen.Welcome -> {
                    val activity = LocalActivity.current
                    BackHandler { authScreen = AuthScreen.Main }
                    WelcomeScreen(
                        onGoogleClick = {
                            phoneAuthViewModel.signInWithGoogle(activity)
                        },
                        onMobileClick = { authScreen = AuthScreen.EnterNumber },
                        onGuestClick = {
                            mainViewModel.selectTab(AppScreen.Home)
                            authScreen = AuthScreen.Main
                        }
                    )
                }

                // Phone-number entry UI reached from the welcome screen.
                AuthScreen.EnterNumber -> {
                    val activity = LocalActivity.current
                    BackHandler { authScreen = AuthScreen.Welcome }
                    EnterNumberScreen(
                        viewModel = phoneAuthViewModel,
                        onBack = { authScreen = AuthScreen.Welcome },
                        onSendOtpSuccess = { authScreen = AuthScreen.VerifyNumber },
                        onGoogleClick = {
                            phoneAuthViewModel.signInWithGoogle(activity)
                        }
                    )
                }

                // OTP verification + profile capture after Send OTP.
                AuthScreen.VerifyNumber -> {
                    BackHandler { authScreen = AuthScreen.EnterNumber }
                    VerifyNumberScreen(
                        viewModel = phoneAuthViewModel,
                        onBack = { authScreen = AuthScreen.EnterNumber },
                        onEditNumber = { authScreen = AuthScreen.EnterNumber },
                        onContinueSuccess = {
                            // Return to the previously active tab instead of forcing Home.
                            authScreen = AuthScreen.Main
                        }
                    )
                }

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
                    val activity = LocalActivity.current
                    var showExitDialog by remember { mutableStateOf(false) }
                    var showMyListings by rememberSaveable { mutableStateOf(false) }
                    var showMyEnquiries by rememberSaveable { mutableStateOf(false) }
                    var selectedEnquiryPropertyId by rememberSaveable { mutableStateOf<String?>(null) }
                    var showAddProperty by rememberSaveable { mutableStateOf(false) }
                    var showCityScreen by rememberSaveable { mutableStateOf(false) }

                    BackHandler(enabled = !showExitDialog && !showMyListings && !showMyEnquiries && !showAddProperty && !showCityScreen) {
                        showExitDialog = true
                    }

                    BackHandler(enabled = showAddProperty) {
                        showAddProperty = false
                    }

                    BackHandler(enabled = showMyEnquiries && !showAddProperty) {
                        showMyEnquiries = false
                        selectedEnquiryPropertyId = null
                    }

                    BackHandler(enabled = showMyListings && !showMyEnquiries && !showAddProperty) {
                        showMyListings = false
                    }

                    BackHandler(enabled = showCityScreen) {
                        showCityScreen = false
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = AppBackground,
                        contentWindowInsets = WindowInsets(0.dp),
                        bottomBar = {
                            BottomNavBar(
                                tabs = AppScreen.all,
                                selectedTab = selectedTab,
                                onTabSelected = { tab ->
                                    showMyListings = false
                                    showMyEnquiries = false
                                    selectedEnquiryPropertyId = null
                                    showAddProperty = false
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
                                    onProfileClick = { mainViewModel.selectTab(AppScreen.Profile) },
                                    onChangeCity = { showCityScreen = true },
                                    onLoginClick = { authScreen = AuthScreen.Welcome }
                                )
                                AppScreen.Search -> SearchScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onChangeCity = { showCityScreen = true },
                                    onLoginClick = { authScreen = AuthScreen.Welcome }
                                )
                                AppScreen.Saved -> SavedScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onLoginClick = { authScreen = AuthScreen.Welcome }
                                )
                               /* AppScreen.Add -> AddScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onLoginClick = { authScreen = AuthScreen.Login }
                                )*/
                                AppScreen.Profile -> {
                                    when {
                                        showAddProperty -> AddScreen(
                                            modifier = Modifier.fillMaxSize(),
                                            onLoginClick = { authScreen = AuthScreen.Welcome },
                                            startWithAddForm = true
                                        )
                                        showMyEnquiries -> MyEnquiriesScreen(
                                            modifier = Modifier.fillMaxSize(),
                                            filterPropertyId = selectedEnquiryPropertyId,
                                            onBack = {
                                                showMyEnquiries = false
                                                selectedEnquiryPropertyId = null
                                            }
                                        )
                                        showMyListings -> MyListingsScreen(
                                            modifier = Modifier.fillMaxSize(),
                                            onBack = { showMyListings = false },
                                            onAddProperty = { showAddProperty = true },
                                            onViewEnquiries = { propertyId ->
                                                Logger.d(TAG, "View enquiries clicked for property: $propertyId")
                                                selectedEnquiryPropertyId = propertyId
                                                showMyEnquiries = true
                                            }
                                        )
                                        else -> ProfileScreen(
                                            modifier = Modifier.fillMaxSize(),
                                            onLoginClick = { authScreen = AuthScreen.Welcome },
                                            onListPropertyClick = { showAddProperty = true },
                                            onMyListingsClick = {
                                                Logger.d(TAG, "My Listings clicked: opening MyListingsScreen")
                                                showMyListings = true
                                            },
                                            onMyEnquiriesClick = {
                                                Logger.d(TAG, "My Enquiries clicked: opening MyEnquiriesScreen")
                                                selectedEnquiryPropertyId = null
                                                showMyEnquiries = true
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

                    if (showCityScreen) {
                        CityScreen(
                            onCitySelected = { city ->
                                mainViewModel.selectCity(city)
                                showCityScreen = false
                            }
                        )
                    }
                }
            }
        }
        }
    }
}
