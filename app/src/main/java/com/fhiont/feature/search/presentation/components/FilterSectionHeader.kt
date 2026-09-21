package com.fhiont.feature.search.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.fhiont.ui.theme.Black

@Composable
fun FilterSectionHeader(title: String) {
    Text(
        text = title,
        color = Black,
        style = MaterialTheme.typography.titleMedium
    )
}
