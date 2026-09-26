package com.fhiont.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.fhiont.AppStrings
import com.fhiont.ui.components.ConfirmationDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fhiont.feature.auth.presentation.PhoneAuthEffect
import androidx.compose.ui.Modifier
import com.fhiont.core.theme.ThemeMode
import com.fhiont.feature.add.presentation.AddScreen
import com.fhiont.feature.auth.presentation.EnterNumberScreen
import com.fhiont.feature.auth.presentation.LoginScreen
import com.fhiont.feature.auth.presentation.PhoneAuthViewModel
import com.fhiont.feature.auth.presentation.RegisterScreen
import com.fhiont.feature.auth.presentation.VerifyNumberScreen
import com.fhiont.feature.auth.presentation.WelcomeScreen
import com.fhiont.feature.city.presentation.CityScreen
import com.fhiont.feature.profile.presentation.MyEnquiriesScreen
import com.fhiont.feature.profile.presentation.MyListingsScreen
import com.fhiont.feature.profile.presentation.ProfileScreen
import com.fhiont.feature.home.presentation.HomeScreen
import com.fhiont.feature.saved.presentation.SavedScreen
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.presentation.ChatScreen
import com.fhiont.feature.search.presentation.PropertyDetailScreen
import com.fhiont.feature.search.presentation.SearchScreen
import com.fhiont.feature.onboarding.presentation.OnboardingScreen
import com.fhiont.ui.components.BottomNavBar
import com.fhiont.ui.navigation.AppScreen
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.theme.FhiontTheme
import com.fhiont.ui.viewmodel.MainViewModel
import com.fhiont.util.Logger
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
fun MainApp(
    mainViewModel: MainViewModel = koinViewModel(),
    pendingPropertyId: String? = null,
    onPendingPropertyHandled: () -> Unit = {}
) {
    val selectedTab by mainViewModel.selectedTab.collectAsStateWithLifecycle()
    val themeMode by mainViewModel.themeMode.collectAsStateWithLifecycle()
    var authScreen by rememberSaveable { mutableStateOf(AuthScreen.Main) }
    val phoneAuthViewModel: PhoneAuthViewModel = koinViewModel()
    val showOnboarding by mainViewModel.showOnboarding.collectAsStateWithLifecycle()

    // Property resolved from a tapped enquiry push notification.
    // Stage 1: move the incoming ID into local state without suspending, so
    // clearing pendingPropertyId can't cancel the lookup launched in stage 2.
    var deepLinkPropertyId by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(pendingPropertyId) {
        val id = pendingPropertyId ?: return@LaunchedEffect
        onPendingPropertyHandled()
        deepLinkPropertyId = id
    }

    // Stage 2: resolve the property. Keyed only on the local ID, so the
    // coroutine survives pendingPropertyId being cleared above.
    var deepLinkProperty by remember { mutableStateOf<Property?>(null) }
    LaunchedEffect(deepLinkPropertyId) {
        val id = deepLinkPropertyId ?: return@LaunchedEffect
        Logger.d(TAG, "Deep link: resolving property $id")
        deepLinkProperty = mainViewModel.getPropertyById(id)
    }

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

    FhiontTheme(darkTheme = darkTheme) {
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
                // Auth landing UI offering Google, mobile-number, login, sign-up, or guest entry.
                AuthScreen.Welcome -> {
                    val activity = LocalActivity.current
                    BackHandler { authScreen = AuthScreen.Main }
                    WelcomeScreen(
                        onGoogleClick = {
                            Logger.d(TAG, "Google sign-in clicked from WelcomeScreen")
                            phoneAuthViewModel.signInWithGoogle(activity)
                        },
                        onMobileClick = { authScreen = AuthScreen.EnterNumber },
                        onLoginClick = { authScreen = AuthScreen.Login },
                        onSignUpClick = { authScreen = AuthScreen.Register },
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
                            Logger.d(TAG, "Google sign-in clicked from EnterNumberScreen")
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
                    var activeChatEnquiry by remember { mutableStateOf<Enquiry?>(null) }

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

                    BackHandler(enabled = deepLinkProperty != null || deepLinkPropertyId != null) {
                        deepLinkProperty = null
                        deepLinkPropertyId = null
                    }

                    BackHandler(enabled = activeChatEnquiry != null) {
                        activeChatEnquiry = null
                    }

                    // The glass nav capsule overlays content (screens scroll
                    // behind it) instead of living in Scaffold's bottomBar.
                    Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = AppBackground,
                        contentWindowInsets = WindowInsets(0.dp)
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
                            val openPropertyChats: (Property) -> Unit = { property ->
                                selectedEnquiryPropertyId = property.id
                                showMyEnquiries = true
                                mainViewModel.selectTab(AppScreen.Profile)
                            }
                            val openChat: (Enquiry) -> Unit = { enquiry ->
                                activeChatEnquiry = enquiry
                            }

                            when (selectedTab) {
                                AppScreen.Home -> HomeScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onSearchClick = { mainViewModel.selectTab(AppScreen.Search) },
                                    onSavedClick = { mainViewModel.selectTab(AppScreen.Saved) },
                                    //onAddClick = { mainViewModel.selectTab(AppScreen.Add) },
                                    onProfileClick = { mainViewModel.selectTab(AppScreen.Profile) },
                                    onChangeCity = { showCityScreen = true },
                                    onLoginClick = { authScreen = AuthScreen.Welcome },
                                    onViewChats = openPropertyChats,
                                    onOpenChat = openChat
                                )
                                AppScreen.Search -> SearchScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onChangeCity = { showCityScreen = true },
                                    onLoginClick = { authScreen = AuthScreen.Welcome },
                                    onViewChats = openPropertyChats,
                                    onOpenChat = openChat
                                )
                                AppScreen.Saved -> SavedScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    onLoginClick = { authScreen = AuthScreen.Welcome },
                                    onViewChats = openPropertyChats,
                                    onOpenChat = openChat
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
                                            onViewChats = openPropertyChats,
                                            onOpenChat = openChat,
                                            startWithAddForm = true,
                                            onExitForm = { showAddProperty = false }
                                        )
                                        showMyEnquiries -> MyEnquiriesScreen(
                                            modifier = Modifier.fillMaxSize(),
                                            filterPropertyId = selectedEnquiryPropertyId,
                                            onBack = {
                                                showMyEnquiries = false
                                                selectedEnquiryPropertyId = null
                                            },
                                            onChatClick = { enquiry -> activeChatEnquiry = enquiry }
                                        )
                                        showMyListings -> MyListingsScreen(
                                            modifier = Modifier.fillMaxSize(),
                                            onBack = { showMyListings = false },
                                            onAddProperty = { showAddProperty = true },
                                            onViewEnquiries = { propertyId ->
                                                Logger.d(TAG, "View enquiries clicked for property: $propertyId")
                                                selectedEnquiryPropertyId = propertyId
                                                showMyEnquiries = true
                                            },
                                            onOpenChat = { enquiry -> activeChatEnquiry = enquiry }
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

                    if (!showMyListings && !showMyEnquiries && !showAddProperty) {
                        BottomNavBar(
                            tabs = AppScreen.all,
                            selectedTab = selectedTab,
                            onTabSelected = { tab ->
                                showMyListings = false
                                showMyEnquiries = false
                                selectedEnquiryPropertyId = null
                                showAddProperty = false
                                mainViewModel.selectTab(tab)
                            },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                    }

                    if (showExitDialog) {
                        ConfirmationDialog(
                            title = AppStrings.EXIT_DIALOG_TITLE,
                            message = AppStrings.EXIT_DIALOG_MESSAGE,
                            confirmText = AppStrings.EXIT_CONFIRM,
                            dismissText = AppStrings.DIALOG_CANCEL,
                            onConfirm = {
                                showExitDialog = false
                                activity?.finishAffinity()
                            },
                            onDismiss = { showExitDialog = false }
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

                    // Full-screen enquiry chat overlay (buyer <-> owner thread).
                    activeChatEnquiry?.let { enquiry ->
                        ChatScreen(
                            enquiry = enquiry,
                            onBack = { activeChatEnquiry = null },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Full-screen property details opened from a push notification.
                    deepLinkProperty?.let { property ->
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = AppBackground
                        ) {
                            PropertyDetailScreen(
                                property = property,
                                onClose = {
                                    deepLinkProperty = null
                                    deepLinkPropertyId = null
                                },
                                onViewChats = {
                                    deepLinkProperty = null
                                    deepLinkPropertyId = null
                                    selectedEnquiryPropertyId = property.id
                                    showMyEnquiries = true
                                    mainViewModel.selectTab(AppScreen.Profile)
                                },
                                onOpenChat = { enquiry ->
                                    deepLinkProperty = null
                                    deepLinkPropertyId = null
                                    activeChatEnquiry = enquiry
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
        }
    }
}
