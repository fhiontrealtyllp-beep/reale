package com.realeapp.feature.add.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.TextPrimary
import androidx.compose.ui.window.DialogProperties
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.feature.search.presentation.components.PropertyList
import com.realeapp.ui.components.LoginPrompt
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MainBackground,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CardBackground,
                    contentColor = TextPrimary
                )
            }
        },
        topBar = {
            // Logged-in navigation UI; title changes between the property list and add form.
            if (uiState.isLoggedIn) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (uiState.isShowingAddForm) "Add Property" else "My Properties",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        if (uiState.isShowingAddForm) {
                            IconButton(onClick = viewModel::onHideAddForm) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MainBackground
                    )
                )
            }
        },
        floatingActionButton = {
            // Add-property action shown only from the logged-in property list UI.
            if (uiState.isLoggedIn && !uiState.isShowingAddForm) {
                FloatingActionButton(
                    onClick = viewModel::onShowAddForm,
                    containerColor = Accent,
                    contentColor = OnAccent
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add property"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                // Full-screen loading UI while the add feature initializes.
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Accent
                    )
                }

                // Logged-out UI prompting the user to open the login flow.
                !uiState.isLoggedIn -> LoginPrompt(
                    title = "Please Login to Add a Property",
                    onLoginClick = onLoginClick,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Property creation form UI.
                uiState.isShowingAddForm -> AddPropertyForm(
                    uiState = uiState,
                    viewModel = viewModel
                )

                // Logged-in UI listing properties created by the current user.
                else -> MyPropertiesContent(
                    properties = uiState.myProperties,
                    isLoading = uiState.isLoadingMyProperties,
                    errorMessage = uiState.myPropertiesError,
                    onRefresh = viewModel::refresh,
                    onPropertyClick = { selectedProperty = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Full-screen property details UI shown after selecting a property.
    selectedProperty?.let { property ->
        Dialog(
            onDismissRequest = { selectedProperty = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MainBackground
            ) {
                PropertyDetailScreen(
                    property = property,
                    onClose = { selectedProperty = null },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun MyPropertiesContent(
    properties: List<Property>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = Error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        PropertyList(
            properties = properties,
            isLoading = isLoading,
            isLoadingMore = false,
            hasReachedEnd = true,
            onRefresh = onRefresh,
            onLoadMore = {},
            onLike = {},
            onPropertyClick = onPropertyClick,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 8.dp,
                end = 12.dp,
                bottom = 80.dp
            )
        )
    }
}
