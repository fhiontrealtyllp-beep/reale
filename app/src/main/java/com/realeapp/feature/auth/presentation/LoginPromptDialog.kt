package com.realeapp.feature.auth.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.SurfaceLight
import com.realeapp.ui.theme.White

@Composable
fun LoginPromptDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = AuthDims.LOGIN_PROMPT_HORIZONTAL_MARGIN),
            shape = RoundedCornerShape(AuthDims.LOGIN_PROMPT_CORNER_RADIUS),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = AuthDims.LOGIN_PROMPT_ELEVATION)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AuthDims.LOGIN_PROMPT_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(AuthDims.LOGIN_PROMPT_ICON_CONTAINER_SIZE)
                        .background(color = SurfaceLight, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = BrandCoral,
                        modifier = Modifier.size(AuthDims.LOGIN_PROMPT_ICON_SIZE)
                    )
                }

                Spacer(modifier = Modifier.height(AuthDims.LOGIN_PROMPT_TITLE_SPACING))

                Text(
                    text = AuthStrings.LOGIN_PROMPT_TITLE,
                    color = Black,
                    fontSize = AuthDims.LOGIN_PROMPT_TITLE_FONT_SIZE,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AuthDims.LOGIN_PROMPT_MESSAGE_SPACING))

                Text(
                    text = AuthStrings.LOGIN_PROMPT_MESSAGE,
                    color = Gray,
                    fontSize = AuthDims.LOGIN_PROMPT_MESSAGE_FONT_SIZE,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AuthDims.LOGIN_PROMPT_BUTTONS_SPACING))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AuthDims.LOGIN_PROMPT_BUTTON_HEIGHT),
                    shape = RoundedCornerShape(AuthDims.LOGIN_PROMPT_BUTTON_CORNER_RADIUS),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandCoral,
                        contentColor = OnBrandContent
                    )
                ) {
                    Text(
                        text = AuthStrings.LOGIN_PROMPT_LOGIN,
                        fontSize = AuthDims.LOGIN_PROMPT_BUTTON_FONT_SIZE,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(AuthDims.LOGIN_PROMPT_BUTTON_SPACING))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = Gray)
                ) {
                    Text(
                        text = AuthStrings.LOGIN_PROMPT_CANCEL,
                        fontSize = AuthDims.LOGIN_PROMPT_BUTTON_FONT_SIZE
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Prompt Light")
@Preview(showBackground = true, name = "Login Prompt Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginPromptDialogPreview() {
    RealeTheme {
        LoginPromptDialog(
            onDismiss = {},
            onLoginClick = {}
        )
    }
}
