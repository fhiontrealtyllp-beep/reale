package com.realeapp.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.realeapp.ui.components.AppNameLogo
import com.realeapp.R
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.OnMediaContent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White

/**
 * Auth landing screen shown before the credential login form: Google, mobile
 * number, or guest entry points.
 *
 * @param onGoogleClick Called when the user picks Google sign-in.
 * @param onMobileClick Called when the user picks mobile-number sign-in.
 * @param onGuestClick Called when the user continues as a guest.
 * @param modifier Optional modifier for the root container.
 */
@Composable
fun WelcomeScreen(
    onGoogleClick: () -> Unit,
    onMobileClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_welcome_home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = AuthDims.WELCOME_SCRIM_START_ALPHA),
                            0.5f to Color.Black.copy(alpha = AuthDims.WELCOME_SCRIM_MID_ALPHA),
                            1.0f to Color.Black.copy(alpha = AuthDims.WELCOME_SCRIM_END_ALPHA)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        start = AuthDims.SCREEN_PADDING,
                        end = AuthDims.SCREEN_PADDING,
                        top = AuthDims.WELCOME_LOGO_TOP_PADDING
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                AppNameLogo(
                    fontSize = AuthDims.WELCOME_LOGO_FONT_SIZE,
                    firstColor = OnMediaContent,
                    accentColor = BrandCoral
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = AuthStrings.WELCOME_TITLE,
                color = OnMediaContent,
                fontSize = AuthDims.WELCOME_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                lineHeight = AuthDims.WELCOME_TITLE_LINE_HEIGHT,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AuthDims.SCREEN_PADDING)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = AuthDims.WELCOME_SHEET_CORNER_RADIUS,
                            topEnd = AuthDims.WELCOME_SHEET_CORNER_RADIUS
                        )
                    )
                    .background(White)
                    .navigationBarsPadding()
                    .padding(AuthDims.WELCOME_SHEET_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WelcomeOption(
                    label = AuthStrings.OPTION_GOOGLE,
                    onClick = onGoogleClick,
                    leading = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = AuthStrings.CD_GOOGLE_LOGO,
                            modifier = Modifier.size(AuthDims.OPTION_ICON_SIZE)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

                WelcomeOption(
                    label = AuthStrings.OPTION_MOBILE,
                    onClick = onMobileClick,
                    leading = {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = null,
                            tint = Black,
                            modifier = Modifier.size(AuthDims.OPTION_ICON_SIZE)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(AuthDims.SPACE_16))

                WelcomeOption(
                    label = AuthStrings.OPTION_GUEST,
                    onClick = onGuestClick,
                    leading = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Black,
                            modifier = Modifier.size(AuthDims.OPTION_ICON_SIZE)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

                HorizontalDivider(color = HomeSearchBarBorder)

                Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

                TermsText()
            }
        }
    }
}

@Composable
private fun WelcomeOption(
    label: String,
    onClick: () -> Unit,
    leading: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(AuthDims.OPTION_CORNER_RADIUS)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AuthDims.WELCOME_OPTION_HEIGHT)
            .clip(shape)
            .background(White)
            .border(AuthDims.OPTION_BORDER_WIDTH, HomeSearchBarBorder, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = AuthDims.OPTION_HORIZONTAL_PADDING)
        ) {
            leading()
        }

        Text(
            text = label,
            color = Black,
            fontSize = AuthDims.OPTION_FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
internal fun TermsText() {
    val termsText = buildAnnotatedString {
        append(AuthStrings.TERMS_PREFIX)
        withStyle(SpanStyle(color = BrandBlue)) {
            append(AuthStrings.TERMS_OF_SERVICE)
        }
        append(AuthStrings.TERMS_AND)
        withStyle(SpanStyle(color = BrandBlue)) {
            append(AuthStrings.PRIVACY_POLICY)
        }
        append(AuthStrings.TERMS_SUFFIX)
    }

    Text(
        text = termsText,
        color = Gray,
        fontSize = AuthDims.TERMS_FONT_SIZE,
        textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true, name = "Welcome Light")
@Composable
private fun WelcomeScreenLightPreview() {
    RealeTheme {
        WelcomeScreen(
            onGoogleClick = {},
            onMobileClick = {},
            onGuestClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Welcome Dark")
@Composable
private fun WelcomeScreenDarkPreview() {
    RealeTheme(darkTheme = true) {
        WelcomeScreen(
            onGoogleClick = {},
            onMobileClick = {},
            onGuestClick = {}
        )
    }
}
