package com.realeapp.feature.saved.presentation

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.androidx.compose.koinViewModel
import com.realeapp.feature.saved.presentation.SavedViewModel
import com.realeapp.ui.components.LoginPrompt
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.feature.search.presentation.components.PropertyListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedViewModel = koinViewModel()
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
                // Full-screen loading UI while the initial saved properties are fetched.
                uiState.isLoading && uiState.properties.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFDD60D)
                    )
                }

                // Logged-out UI prompting the user to open the login flow.
                !uiState.isLoggedIn -> LoginPrompt(
                    title = "Please Login to View your properties",
                    onLoginClick = onLoginClick,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Error UI with an action to retry loading saved properties.
                uiState.errorMessage != null -> ErrorContent(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = viewModel::refresh,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Logged-in UI listing the user's saved properties.
                else -> SavedPropertyList(
                    properties = uiState.properties,
                    isLoading = uiState.isLoading,
                    onRefresh = viewModel::refresh,
                    onPropertyClick = { selectedProperty = it },
                    onLike = viewModel::onLikeClicked,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Full-screen property details UI shown after selecting a saved property.
    selectedProperty?.let { selected ->
        val property = uiState.properties.find {
            it.documentId == selected.documentId || it.id == selected.id
        } ?: selected.copy(isLiked = true)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedPropertyList(
    properties: List<com.realeapp.feature.search.domain.model.Property>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onPropertyClick: (Property) -> Unit,
    onLike: (String) -> Unit,
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
                        onLike = { onLike(property.documentId ?: property.id) },
                        onClick = { onPropertyClick(property) },
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
