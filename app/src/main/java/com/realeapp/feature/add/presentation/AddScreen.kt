package com.realeapp.feature.add.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.realeapp.feature.add.di.AddModule
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.feature.search.presentation.components.PropertyList
import com.realeapp.ui.components.LoginPrompt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = viewModel(factory = AddModule.viewModelFactory)
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
        containerColor = Color(0xFF141C3D),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1C2755),
                    contentColor = Color(0xFFFBFBFB)
                )
            }
        },
        topBar = {
            if (uiState.isLoggedIn) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (uiState.isShowingAddForm) "Add Property" else "My Properties",
                            color = Color(0xFFFBFBFB),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        if (uiState.isShowingAddForm) {
                            IconButton(onClick = viewModel::onHideAddForm) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFFFBFBFB)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF141C3D)
                    )
                )
            }
        },
        floatingActionButton = {
            if (uiState.isLoggedIn && !uiState.isShowingAddForm) {
                FloatingActionButton(
                    onClick = viewModel::onShowAddForm,
                    containerColor = Color(0xFFFDD60D),
                    contentColor = Color(0xFF141C3D)
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
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFDD60D)
                    )
                }

                !uiState.isLoggedIn -> LoginPrompt(
                    title = "Please Login to Add a Property",
                    onLoginClick = onLoginClick,
                    modifier = Modifier.align(Alignment.Center)
                )

                uiState.isShowingAddForm -> AddPropertyForm(
                    uiState = uiState,
                    viewModel = viewModel
                )

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

    selectedProperty?.let { property ->
        Dialog(
            onDismissRequest = { selectedProperty = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF141C3D)
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
                color = Color(0xFFFF6B6B),
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
            modifier = Modifier.fillMaxSize()
        )
    }
}
