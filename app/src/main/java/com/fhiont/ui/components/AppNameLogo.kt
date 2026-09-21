package com.fhiont.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.fhiont.AppStrings
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.BrandCoral
import com.fhiont.ui.theme.FhiontTheme

/**
 * Reusable two-tone app logo. It is the single UI component that should be used
 * to display the app name anywhere in the app.
 *
 * The first [APP_NAME_SPLIT_INDEX] characters use [firstColor] (defaults to [ControlAccent])
 * and the remaining characters use [accentColor] (defaults to [BrandCoral]).
 */
@Composable
fun AppNameLogo(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    firstColor: Color = ControlAccent,
    accentColor: Color = BrandCoral
) {
    Row(modifier = modifier) {
        Text(
            text = AppStrings.APP_NAME_FIRST,
            color = firstColor,
            style = style,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
        Text(
            text = AppStrings.APP_NAME_ACCENT,
            color = accentColor,
            style = style,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    }
}

@Preview(showBackground = true, name = "AppNameLogo Light")
@Composable
private fun AppNameLogoLightPreview() {
    FhiontTheme {
        Box(
            modifier = Modifier
                .background(AppBackground)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AppNameLogo()
        }
    }
}

@Preview(showBackground = true, name = "AppNameLogo Dark")
@Composable
private fun AppNameLogoDarkPreview() {
    FhiontTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(AppBackground)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AppNameLogo()
        }
    }
}
