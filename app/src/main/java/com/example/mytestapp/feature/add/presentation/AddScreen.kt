package com.example.mytestapp.feature.add.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytestapp.feature.add.di.AddModule
import com.example.mytestapp.ui.components.LoginPrompt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onLoginClick: () -> Unit,
    loginSuccessVersion: Int = 0,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = viewModel(factory = AddModule.viewModelFactory)
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
                        text = "Add Property",
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
                uiState.isLoading -> {
                    // Placeholder for loading state
                }

                !uiState.isLoggedIn -> LoginPrompt(
                    title = "Please Login to Add a Property",
                    onLoginClick = onLoginClick,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> {
                    Text(
                        text = uiState.dummyText,
                        color = Color(0xFFFBFBFB),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
