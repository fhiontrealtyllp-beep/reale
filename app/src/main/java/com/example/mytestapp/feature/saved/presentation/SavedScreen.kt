package com.example.mytestapp.feature.saved.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytestapp.feature.saved.di.SavedModule
import com.example.mytestapp.feature.saved.presentation.SavedViewModel
import com.example.mytestapp.ui.components.LoginPrompt
import com.example.mytestapp.feature.search.presentation.components.PropertyListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onLoginClick: () -> Unit,
    loginSuccessVersion: Int = 0,
    modifier: Modifier = Modifier,
    viewModel: SavedViewModel = viewModel(factory = SavedModule.viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(loginSuccessVersion) {
        if (loginSuccessVersion > 0) {
            viewModel.refresh()
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
            TopAppBar(
                title = {
                    Text(
                        text = "Saved Properties",
                        color = Color(0xFFFBFBFB),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF141C3D)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.properties.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFDD60D)
                    )
                }

                !uiState.isLoggedIn -> LoginPrompt(
                    title = "Please Login to View your properties",
                    onLoginClick = onLoginClick,
                    modifier = Modifier.align(Alignment.Center)
                )

                uiState.errorMessage != null -> ErrorContent(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = viewModel::refresh,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> SavedPropertyList(
                    properties = uiState.properties,
                    isLoading = uiState.isLoading,
                    onRefresh = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedPropertyList(
    properties: List<com.example.mytestapp.feature.search.domain.model.Property>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    PullToRefreshBox(
        isRefreshing = isLoading && properties.isNotEmpty(),
        onRefresh = onRefresh,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF141C3D))
    ) {
        if (properties.isEmpty()) {
            EmptySavedResults(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = properties,
                    key = { it.id }
                ) { property ->
                    PropertyListItem(
                        property = property.copy(isLiked = true),
                        onLike = { /* unlike from saved list if needed */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySavedResults(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = Color(0xFFFDD60D),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Liked Properties",
            color = Color(0xFFFBFBFB),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You haven't liked any properties yet.",
            color = Color(0xFFFBFBFB).copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = Color(0xFFFBFBFB),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = onRetry,
            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFDD60D))
        ) {
            Text(text = "Retry")
        }
    }
}
