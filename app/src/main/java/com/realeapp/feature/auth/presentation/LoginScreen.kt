package com.realeapp.feature.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary
import com.realeapp.ui.components.VerticalSpacer8
import com.realeapp.ui.components.VerticalSpacer16
import com.realeapp.ui.components.VerticalSpacer24
import com.realeapp.ui.components.VerticalSpacer32
import com.realeapp.util.Logger

private const val TAG = "LoginScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit,
    onRegisterClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect {
            onLoginSuccess()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MainBackground, CardBackground)
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Welcome Back!",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                VerticalSpacer8()

                Text(
                    text = "Login to access your saved properties",
                    color = TextPrimary.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                VerticalSpacer32()

                // Email input UI.
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = TextSecondary,
                        focusedLabelColor = Accent,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                VerticalSpacer16()

                // Password input UI with a visibility toggle.
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (uiState.isPasswordVisible) "Hide password" else "Show password",
                                tint = TextSecondary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = TextSecondary,
                        focusedLabelColor = Accent,
                        unfocusedLabelColor = TextSecondary
                    )
                )

                // Validation or authentication error UI.
                if (uiState.errorMessage != null) {
                    VerticalSpacer8()
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = Error,
                        fontSize = 14.sp
                    )
                }

                VerticalSpacer24()

                // Primary login action; progress replaces its label during authentication.
                TextButton(
                    onClick = {
                        Logger.d(TAG, "Login button clicked: email=${uiState.email}")
                        viewModel.login()
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Accent,
                        contentColor = OnAccent,
                        disabledContainerColor = Accent.copy(alpha = 0.5f)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = OnAccent,
                            modifier = Modifier.height(24.dp)
                        )
                    } else {
                        Text(
                            text = "Login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                VerticalSpacer24()

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = TextPrimary,
                                fontSize = 16.sp
                            )
                        ) {
                            append("New user? ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        ) {
                            append("Register")
                        }
                    },
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }
        }
    }
}
