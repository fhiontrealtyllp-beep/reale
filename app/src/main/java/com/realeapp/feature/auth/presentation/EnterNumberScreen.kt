package com.realeapp.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.realeapp.R
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.components.AppNameLogo
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.IsDarkAppTheme
import com.realeapp.ui.theme.NavyText
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.TextHint
import com.realeapp.ui.theme.White
import com.realeapp.util.findActivity

private const val PHONE_MAX_LENGTH = 10

/**
 * Phone-number entry screen reached from the welcome screen's mobile option.
 *
 * @param viewModel ViewModel that sends the OTP and owns the request state.
 * @param onBack Called when the user taps the back arrow.
 * @param onSendOtpSuccess Called when the OTP request succeeds and the app should
 *        navigate to the verification screen.
 * @param onGoogleClick Called when the user picks the Google fallback.
 * @param modifier Optional modifier for the root container.
 */
@Composable
fun EnterNumberScreen(
    viewModel: PhoneAuthViewModel,
    onBack: () -> Unit,
    onSendOtpSuccess: () -> Unit,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect == PhoneAuthEffect.NavigateToVerify) {
                onSendOtpSuccess()
            }
        }
    }

    EnterNumberContent(
        uiState = uiState,
        onBack = onBack,
        onSendOtp = { phone -> viewModel.sendOtp(activity, phone) },
        onGoogleClick = onGoogleClick,
        modifier = modifier
    )
}

/**
 * Stateless phone-number entry content.
 */
@Composable
internal fun EnterNumberContent(
    uiState: PhoneAuthUiState,
    onBack: () -> Unit,
    onSendOtp: (String) -> Unit,
    onGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }

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
                        .padding(end = AuthDims.ENTER_ILLUSTRATION_WIDTH)
                ) {
                    AppNameLogo(fontSize = AuthDims.SMALL_LOGO_FONT_SIZE)

                    Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

                    Text(
                        text = AuthStrings.ENTER_NUMBER_TITLE,
                        color = NavyText,
                        fontSize = AuthDims.ENTER_TITLE_FONT_SIZE,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(AuthDims.SPACE_8))

                    Text(
                        text = AuthStrings.ENTER_NUMBER_SUBTITLE,
                        color = Gray,
                        fontSize = AuthDims.ENTER_SUBTITLE_FONT_SIZE
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_welcome_illustration),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(AuthDims.ENTER_ILLUSTRATION_WIDTH)
                )
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { input ->
                    phoneNumber = input.filter(Char::isDigit).take(PHONE_MAX_LENGTH)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = AuthStrings.PHONE_HINT, color = TextHint)
                },
                leadingIcon = { CountryCodePicker() },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSendOtp(phoneNumber) }
                ),
                singleLine = true,
                shape = RoundedCornerShape(AuthDims.FIELD_CORNER_RADIUS),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandBlue,
                    unfocusedBorderColor = HomeSearchBarBorder,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    cursorColor = BrandBlue
                )
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            Button(
                onClick = { onSendOtp(phoneNumber) },
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
                        text = AuthStrings.BUTTON_SEND_OTP,
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
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = HomeSearchBarBorder
                )
                Text(
                    text = AuthStrings.DIVIDER_OR,
                    color = Gray,
                    fontSize = AuthDims.ENTER_SUBTITLE_FONT_SIZE,
                    modifier = Modifier.padding(horizontal = AuthDims.SPACE_16)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = HomeSearchBarBorder
                )
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

            Text(
                text = AuthStrings.OPTION_GOOGLE,
                color = BrandBlue,
                fontSize = AuthDims.OPTION_FONT_SIZE,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoogleClick)
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_40))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TrustBadge(
                    icon = Icons.Outlined.Shield,
                    label = AuthStrings.BADGE_SECURE,
                    contentDescription = AuthStrings.CD_BADGE_SECURE
                )
                TrustBadge(
                    icon = Icons.Outlined.Lock,
                    label = AuthStrings.BADGE_PROTECTED,
                    contentDescription = AuthStrings.CD_BADGE_PROTECTED
                )
                TrustBadge(
                    icon = Icons.Outlined.Group,
                    label = AuthStrings.BADGE_QUICK,
                    contentDescription = AuthStrings.CD_BADGE_QUICK
                )
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TermsText()
            }

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))
        }
    }
}

@Composable
private fun CountryCodePicker() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            // TODO: open a country-code picker once multiple countries are supported.
        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_flag_in),
            contentDescription = null,
            modifier = Modifier
                .size(AuthDims.FLAG_WIDTH, AuthDims.FLAG_HEIGHT)
                .clip(RoundedCornerShape(AuthDims.FLAG_CORNER_RADIUS))
        )
        Spacer(modifier = Modifier.width(AuthDims.SPACE_8))
        Text(
            text = AuthStrings.COUNTRY_CODE_IN,
            color = Black,
            fontSize = AuthDims.COUNTRY_CODE_FONT_SIZE,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = AuthStrings.CD_COUNTRY_PICKER,
            tint = Gray
        )
    }
}

@Composable
private fun TrustBadge(
    icon: ImageVector,
    label: String,
    contentDescription: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(AuthDims.BADGE_CIRCLE_SIZE)
                .clip(CircleShape)
                .background(HomeCategoryUnselected),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = BrandBlue,
                modifier = Modifier.size(AuthDims.BADGE_ICON_SIZE)
            )
        }
        Spacer(modifier = Modifier.height(AuthDims.SPACE_8))
        Text(
            text = label,
            color = Gray,
            fontSize = AuthDims.BADGE_LABEL_FONT_SIZE,
            lineHeight = AuthDims.BADGE_LABEL_LINE_HEIGHT,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EnterNumberContentPreview() {
    RealeTheme {
        EnterNumberContent(
            uiState = PhoneAuthUiState(),
            onBack = {},
            onSendOtp = {},
            onGoogleClick = {}
        )
    }
}
