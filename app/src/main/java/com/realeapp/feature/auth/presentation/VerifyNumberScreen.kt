package com.realeapp.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.realeapp.R
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.components.AppNameLogo
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.IsDarkAppTheme
import com.realeapp.ui.theme.NavyText
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.OnLightArtwork
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.TextHint
import com.realeapp.ui.theme.White
import com.realeapp.util.findActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

private const val OTP_LENGTH = 6
private const val RESEND_SECONDS = 30
private const val DOB_PATTERN = "dd MMM yyyy"
private const val PHONE_GROUP_SIZE = 5
private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60

/**
 * OTP verification + profile-capture screen reached after Send OTP.
 *
 * @param viewModel ViewModel that verifies the OTP and owns the request state.
 * @param onBack Called when the user taps the back arrow.
 * @param onEditNumber Called when the user taps Edit next to the phone number.
 * @param onContinueSuccess Called after the profile is saved and the app should
 *        navigate to the main screen.
 * @param modifier Optional modifier for the root container.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyNumberScreen(
    viewModel: PhoneAuthViewModel,
    onBack: () -> Unit,
    onEditNumber: () -> Unit,
    onContinueSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect == PhoneAuthEffect.NavigateToMain) {
                onContinueSuccess()
            }
        }
    }

    VerifyNumberContent(
        phoneNumber = uiState.pendingPhone,
        uiState = uiState,
        onBack = onBack,
        onEditNumber = onEditNumber,
        onResendOtp = { viewModel.resendOtp(activity) },
        onContinue = { otp, fullName, dob ->
            viewModel.verifyOtpAndSaveProfile(otp, fullName, dob)
        },
        modifier = modifier
    )
}

/**
 * Stateless OTP verification + profile-capture content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VerifyNumberContent(
    phoneNumber: String,
    uiState: PhoneAuthUiState,
    onBack: () -> Unit,
    onEditNumber: () -> Unit,
    onResendOtp: () -> Unit,
    onContinue: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var otp by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var secondsLeft by rememberSaveable { mutableIntStateOf(RESEND_SECONDS) }
    var resendTick by rememberSaveable { mutableIntStateOf(0) }

    // Countdown for the resend timer; restarts whenever resendTick changes.
    LaunchedEffect(resendTick) {
        secondsLeft = RESEND_SECONDS
        while (secondsLeft > 0) {
            delay(MILLIS_PER_SECOND)
            secondsLeft--
        }
    }

    val displayPhone = if (phoneNumber.isBlank()) {
        AuthStrings.COUNTRY_CODE_IN
    } else {
        "${AuthStrings.COUNTRY_CODE_IN} ${phoneNumber.chunked(PHONE_GROUP_SIZE).joinToString(" ")}"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_city_skyline),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            // The skyline artwork is light; tint it to the surface color in dark
            // mode so it reads as a subtle silhouette instead of white blocks.
            colorFilter = if (IsDarkAppTheme) ColorFilter.tint(White) else null,
            alignment = Alignment.BottomCenter,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AuthDims.SCREEN_PADDING)
        ) {
            Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = AuthStrings.CD_BACK,
                tint = NavyText,
                modifier = Modifier
                    .size(AuthDims.BACK_ICON_SIZE)
                    .clickable(onClick = onBack)
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = AuthDims.VERIFY_ILLUSTRATION_WIDTH)
                ) {
                    AppNameLogo(fontSize = AuthDims.SMALL_LOGO_FONT_SIZE)

                    Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

                    Text(
                        text = AuthStrings.VERIFY_TITLE,
                        color = NavyText,
                        fontSize = AuthDims.ENTER_TITLE_FONT_SIZE,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(AuthDims.SPACE_8))

                    Text(
                        text = AuthStrings.VERIFY_SUBTITLE,
                        color = Gray,
                        fontSize = AuthDims.ENTER_SUBTITLE_FONT_SIZE
                    )

                    Spacer(modifier = Modifier.height(AuthDims.SPACE_4))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayPhone,
                            color = NavyText,
                            fontSize = AuthDims.VERIFY_PHONE_FONT_SIZE,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(AuthDims.SPACE_8))
                        Text(
                            text = AuthStrings.EDIT,
                            color = BrandBlue,
                            fontSize = AuthDims.VERIFY_PHONE_FONT_SIZE,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onEditNumber)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(AuthDims.VERIFY_ILLUSTRATION_WIDTH)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_welcome_illustration),
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = AuthStrings.VERIFY_ILLUSTRATION_TEXT,
                        color = OnLightArtwork,
                        fontSize = AuthDims.VERIFY_SCRIPT_FONT_SIZE,
                        lineHeight = AuthDims.VERIFY_SCRIPT_LINE_HEIGHT,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = AuthDims.SPACE_8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            OtpInput(otp = otp, onOtpChange = { otp = it })

            Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

            ResendTimer(
                secondsLeft = secondsLeft,
                onResend = {
                    onResendOtp()
                    resendTick++
                }
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            HorizontalDivider(color = HomeSearchBarBorder)

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            Text(
                text = AuthStrings.PROFILE_TITLE,
                color = NavyText,
                fontSize = AuthDims.PROFILE_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_4))

            Text(
                text = AuthStrings.PROFILE_SUBTITLE,
                color = Gray,
                fontSize = AuthDims.ENTER_SUBTITLE_FONT_SIZE
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            Text(
                text = AuthStrings.LABEL_FULL_NAME,
                color = NavyText,
                fontSize = AuthDims.FIELD_LABEL_FONT_SIZE,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_8))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = AuthStrings.NAME_HINT, color = TextHint)
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                singleLine = true,
                shape = RoundedCornerShape(AuthDims.FIELD_CORNER_RADIUS),
                colors = authFieldColors()
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_4))

            Text(
                text = AuthStrings.NAME_HELPER,
                color = Gray,
                fontSize = AuthDims.FIELD_HELPER_FONT_SIZE
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

            Text(
                text = AuthStrings.LABEL_DOB,
                color = NavyText,
                fontSize = AuthDims.FIELD_LABEL_FONT_SIZE,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_8))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dob,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = AuthStrings.DOB_HINT, color = TextHint)
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = AuthStrings.CD_CALENDAR,
                            tint = Gray
                        )
                    },
                    readOnly = true,
                    enabled = false,
                    singleLine = true,
                    shape = RoundedCornerShape(AuthDims.FIELD_CORNER_RADIUS),
                    colors = authFieldColors()
                )
                // Overlay captures taps so the read-only field opens the picker.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_4))

            Text(
                text = AuthStrings.DOB_HELPER,
                color = Gray,
                fontSize = AuthDims.FIELD_HELPER_FONT_SIZE
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            Button(
                onClick = { onContinue(otp, fullName, dob) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AuthDims.SEND_OTP_HEIGHT),
                shape = RoundedCornerShape(AuthDims.SEND_OTP_CORNER_RADIUS),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandCoral,
                    contentColor = OnBrandContent,
                    disabledContainerColor = BrandCoral.copy(alpha = AuthDims.BUTTON_DISABLED_ALPHA)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = OnBrandContent,
                        modifier = Modifier.height(AuthDims.SEND_OTP_PROGRESS_SIZE)
                    )
                } else {
                    Text(
                        text = AuthStrings.BUTTON_CONTINUE,
                        fontSize = AuthDims.SEND_OTP_FONT_SIZE,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(AuthDims.SPACE_12))

                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = Error,
                    fontSize = AuthDims.ENTER_SUBTITLE_FONT_SIZE,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AuthDims.SPACE_4))
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = AuthStrings.CD_SHIELD,
                    tint = BrandBlue,
                    modifier = Modifier.size(AuthDims.INFO_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(AuthDims.SPACE_8))
                Text(
                    text = AuthStrings.INFO_SECURE,
                    color = Gray,
                    fontSize = AuthDims.INFO_FONT_SIZE
                )
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            dob = formatDob(millis)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(AuthStrings.BUTTON_OK)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(AuthStrings.BUTTON_CANCEL)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = BrandBlue,
    unfocusedBorderColor = HomeSearchBarBorder,
    focusedContainerColor = White,
    unfocusedContainerColor = White,
    focusedTextColor = Black,
    unfocusedTextColor = Black,
    cursorColor = BrandBlue,
    disabledTextColor = Black,
    disabledBorderColor = HomeSearchBarBorder,
    disabledContainerColor = White,
    disabledPlaceholderColor = TextHint,
    disabledTrailingIconColor = Gray
)

@Composable
private fun OtpInput(
    otp: String,
    onOtpChange: (String) -> Unit
) {
    BasicTextField(
        value = otp,
        onValueChange = { input ->
            onOtpChange(input.filter(Char::isDigit).take(OTP_LENGTH))
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        textStyle = TextStyle(
            color = NavyText,
            fontSize = AuthDims.OTP_FONT_SIZE,
            fontWeight = FontWeight.SemiBold
        ),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(OTP_LENGTH) { index ->
                    OtpCell(
                        digit = otp.getOrNull(index)?.toString().orEmpty(),
                        isActive = index == otp.length
                    )
                }
            }
        }
    )
}

@Composable
private fun OtpCell(
    digit: String,
    isActive: Boolean
) {
    Box(
        modifier = Modifier
            .size(AuthDims.OTP_BOX_SIZE)
            .clip(RoundedCornerShape(AuthDims.OTP_BOX_CORNER_RADIUS))
            .background(White)
            .border(
                width = if (isActive) {
                    AuthDims.OTP_BOX_FOCUSED_BORDER_WIDTH
                } else {
                    AuthDims.OTP_BOX_BORDER_WIDTH
                },
                color = if (isActive) BrandBlue else HomeSearchBarBorder,
                shape = RoundedCornerShape(AuthDims.OTP_BOX_CORNER_RADIUS)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            color = NavyText,
            fontSize = AuthDims.OTP_FONT_SIZE,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ResendTimer(
    secondsLeft: Int,
    onResend: () -> Unit
) {
    val resendText = buildAnnotatedString {
        append(AuthStrings.RESEND_PREFIX)
        withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.SemiBold)) {
            append(AuthStrings.RESEND_OTP)
        }
        if (secondsLeft > 0) {
            append(AuthStrings.RESEND_IN)
            withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.SemiBold)) {
                append(formatResendTime(secondsLeft))
            }
        }
    }
    Text(
        text = resendText,
        color = Gray,
        fontSize = AuthDims.ENTER_SUBTITLE_FONT_SIZE,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = secondsLeft == 0, onClick = onResend)
    )
}

private fun formatResendTime(seconds: Int): String =
    String.format(
        Locale.getDefault(),
        "%02d:%02d",
        seconds / SECONDS_PER_MINUTE,
        seconds % SECONDS_PER_MINUTE
    )

private fun formatDob(millis: Long): String =
    SimpleDateFormat(DOB_PATTERN, Locale.getDefault()).format(Date(millis))

@Preview(showBackground = true)
@Composable
private fun VerifyNumberContentPreview() {
    RealeTheme {
        VerifyNumberContent(
            phoneNumber = "9876543210",
            uiState = PhoneAuthUiState(),
            onBack = {},
            onEditNumber = {},
            onResendOtp = {},
            onContinue = { _, _, _ -> }
        )
    }
}
