package com.realeapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.realeapp.util.Logger
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.OnBrandContent

private const val TAG = "LoginPrompt"

private object LoginPromptStrings {
    const val DEFAULT_BUTTON_TEXT = "Login"
}

private object LoginPromptDims {
    val CONTENT_PADDING = 24.dp
    val ICON_SIZE = 80.dp
    val BUTTON_HEIGHT = 48.dp
    val BUTTON_CORNER_RADIUS = 12.dp
    val BUTTON_FONT_SIZE = 18.sp
}

@Composable
fun LoginPrompt(
    title: String,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = LoginPromptStrings.DEFAULT_BUTTON_TEXT
) {
    Column(
        modifier = modifier.padding(LoginPromptDims.CONTENT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = BrandBlue,
            modifier = Modifier.size(LoginPromptDims.ICON_SIZE)
        )
        VerticalSpacer16()
        Text(
            text = title,
            color = Black,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        VerticalSpacer24()
        TextButton(
            onClick = {
                Logger.d(TAG, "Login button clicked: \"$title\"")
                onLoginClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(LoginPromptDims.BUTTON_HEIGHT),
            shape = RoundedCornerShape(LoginPromptDims.BUTTON_CORNER_RADIUS),
            colors = ButtonDefaults.textButtonColors(
                containerColor = BrandBlue,
                contentColor = OnBrandContent
            )
        ) {
            Text(
                text = buttonText,
                fontSize = LoginPromptDims.BUTTON_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
