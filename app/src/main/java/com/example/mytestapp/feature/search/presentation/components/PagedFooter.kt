package com.example.mytestapp.feature.search.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PagedFooter(
    showLoader: Boolean,
    showEndOfResults: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            showLoader -> CircularProgressIndicator(color = Color(0xFFFDD60D))
            showEndOfResults -> Text(
                text = "End of results",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}
