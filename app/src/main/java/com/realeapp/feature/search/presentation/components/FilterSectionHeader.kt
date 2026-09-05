package com.realeapp.feature.search.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.realeapp.ui.theme.TextPrimary

@Composable
fun FilterSectionHeader(title: String) {
    Text(
        text = title,
        color = TextPrimary,
        style = MaterialTheme.typography.titleSmall
    )
}
