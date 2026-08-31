package com.example.mytestapp.feature.search.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytestapp.feature.search.di.SearchModule
import com.example.mytestapp.feature.search.presentation.components.EmptySearchResults
import com.example.mytestapp.feature.search.presentation.components.FilterDialog
import com.example.mytestapp.feature.search.presentation.components.MapViewContent
import com.example.mytestapp.feature.search.presentation.components.PropertyList
import com.example.mytestapp.feature.search.presentation.components.SearchHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(factory = SearchModule.viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterDialog by remember { mutableStateOf(false) }

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
        floatingActionButton = {
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
            SearchHeader(
                filter = uiState.currentFilter,
                onOpenFilter = { showFilterDialog = true }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading && uiState.properties.isEmpty() -> {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFFDD60D)
                        )
                    }

                    uiState.errorMessage != null -> ErrorContent(
                        message = uiState.errorMessage.orEmpty(),
                        onRetry = viewModel::refresh,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    uiState.isMapView -> MapViewContent(
                        properties = uiState.properties,
                        onPropertyTap = { /* property detail navigation can be wired here */ },
                        modifier = Modifier.fillMaxSize()
                    )

                    else -> PropertyList(
                        properties = uiState.properties,
                        isLoading = uiState.isLoading,
                        isLoadingMore = uiState.isLoadingMore,
                        hasReachedEnd = uiState.hasReachedEnd,
                        onRefresh = viewModel::refresh,
                        onLoadMore = viewModel::onLoadMore,
                        onLike = viewModel::onLikeClicked,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

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
