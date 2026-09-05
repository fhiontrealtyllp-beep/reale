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
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.TextPrimary

private const val TAG = "LoginPrompt"

@Composable
fun LoginPrompt(
    title: String,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "Login"
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(80.dp)
        )
        VerticalSpacer16()
        Text(
            text = title,
            color = TextPrimary,
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
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = Accent,
                contentColor = OnAccent
            )
        ) {
            Text(
                text = buttonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
