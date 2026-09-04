package com.realeapp.feature.search.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.realeapp.feature.search.di.SearchModule
import com.realeapp.feature.search.presentation.components.EmptySearchResults
import com.realeapp.feature.search.presentation.components.FilterDialog
import com.realeapp.feature.search.presentation.components.MapViewContent
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.presentation.components.PropertyList
import com.realeapp.feature.search.presentation.components.SearchHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(factory = SearchModule.viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
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
        floatingActionButton = {
            // View toggle action switches the results UI between map and list modes.
            FloatingActionButton(
                onClick = viewModel::onToggleView,
                containerColor = Color(0xFFFDD60D),
                contentColor = Color(0xFF141C3D)
            ) {
                Icon(
                    imageVector = if (uiState.isMapView) Icons.AutoMirrored.Filled.List else Icons.Default.Map,
                    contentDescription = if (uiState.isMapView) "List view" else "Map view"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Search and filter controls remain visible above every results state.
            SearchHeader(
                filter = uiState.currentFilter,
                onOpenFilter = { showFilterDialog = true }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    // Full-screen loading UI while the initial search results are fetched.
                    uiState.isLoading && uiState.properties.isEmpty() -> {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFFDD60D)
                        )
                    }

                    // Error UI with an action to retry the search.
                    uiState.errorMessage != null -> ErrorContent(
                        message = uiState.errorMessage.orEmpty(),
                        onRetry = viewModel::refresh,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Map results UI with tappable property markers.
                    uiState.isMapView -> MapViewContent(
                        properties = uiState.properties,
                        onPropertyTap = { selectedProperty = it },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Paginated list results UI used as the default search view.
                    else -> PropertyList(
                        properties = uiState.properties,
                        isLoading = uiState.isLoading,
                        isLoadingMore = uiState.isLoadingMore,
                        hasReachedEnd = uiState.hasReachedEnd,
                        onRefresh = viewModel::refresh,
                        onLoadMore = viewModel::onLoadMore,
                        onLike = viewModel::onLikeClicked,
                        onPropertyClick = { selectedProperty = it },
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
        }
    }

    // Full-screen property details UI shown after selecting a result.
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
                    onLike = { viewModel.onLikeClicked(property.documentId ?: property.id) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Filter dialog overlay shown from the search header.
    if (showFilterDialog) {
        FilterDialog(
            filter = uiState.currentFilter,
            onDismiss = { showFilterDialog = false },
            onApply = { newFilter ->
                showFilterDialog = false
                viewModel.onFilterChanged(newFilter)
            },
            onReset = {
                showFilterDialog = false
                viewModel.onResetFilter()
            }
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = Color(0xFFFBFBFB),
            style = MaterialTheme.typography.bodyLarge
        )
        TextButton(
            onClick = onRetry,
            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFDD60D))
        ) {
            Text(text = "Retry")
        }
    }
}
