package com.realeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.ControlAccent
import com.realeapp.ui.theme.RealeTheme

private object LoaderDimens {
    val SIZE = 48.dp
    val STROKE_WIDTH = 4.dp
    const val BACKGROUND_ALPHA = 0.7f
}

@Composable
fun GenericLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground.copy(alpha = LoaderDimens.BACKGROUND_ALPHA)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(LoaderDimens.SIZE),
            color = ControlAccent,
            strokeWidth = LoaderDimens.STROKE_WIDTH
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GenericLoaderPreview() {
    RealeTheme {
        GenericLoader()
    }
}
