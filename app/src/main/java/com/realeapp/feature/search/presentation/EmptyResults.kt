package com.realeapp.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White

@Composable
fun EmptyResults(
    modifier: Modifier = Modifier,
    title: String = PropertiesStrings.EMPTY_TITLE,
    subtitle: String = PropertiesStrings.EMPTY_SUBTITLE,
    city: String? = null,
    onChangeCity: (() -> Unit)? = null
) {
    val displayTitle = if (city.isNullOrBlank()) {
        title
    } else {
        String.format(PropertiesStrings.EMPTY_CITY_TITLE_FORMAT, city)
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = null,
                tint = Gray.copy(alpha = 0.38f),
                modifier = Modifier.height(PropertiesDims.EMPTY_ICON_HEIGHT)
            )
            Spacer(modifier = Modifier.height(PropertiesDims.EMPTY_TITLE_SPACING))
            Text(
                text = displayTitle,
                color = Black,
                fontSize = PropertiesDims.EMPTY_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(PropertiesDims.EMPTY_SUBTITLE_SPACING))
            Text(
                text = subtitle,
                color = Gray.copy(alpha = 0.7f),
                fontSize = PropertiesDims.EMPTY_SUBTITLE_FONT_SIZE
            )
            if (onChangeCity != null) {
                Spacer(modifier = Modifier.height(PropertiesDims.EMPTY_BUTTON_SPACING))
                Button(
                    onClick = onChangeCity,
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text(text = PropertiesStrings.EMPTY_CHANGE_CITY, color = White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyResultsPreview() {
    RealeTheme {
        EmptyResults(modifier = Modifier.fillMaxSize())
    }
}
