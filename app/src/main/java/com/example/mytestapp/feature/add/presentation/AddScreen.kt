package com.example.mytestapp.feature.add.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytestapp.ui.viewmodel.AddViewModel

@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = uiState.dummyText,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
