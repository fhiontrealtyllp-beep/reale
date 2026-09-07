package com.realeapp.feature.auth.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.realeapp.AppStrings
import com.realeapp.R
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.IsDarkAppTheme
import com.realeapp.ui.theme.NavyText
import com.realeapp.ui.theme.OnLightArtwork
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White

private const val SWOOSH_CONTROL_X_RATIO = 0.5f
private const val SWOOSH_START_Y_RATIO = 0.7f
private const val SWOOSH_END_Y_RATIO = 0.4f

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
                .padding(horizontal = AuthDims.SCREEN_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeIllustration()

            AuthBrandLogo(fontSize = AuthDims.LOGO_FONT_SIZE)

            Text(
                text = AuthStrings.WELCOME_TITLE_PREFIX + AppStrings.APP_NAME,
                color = NavyText,
                fontSize = AuthDims.TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_8))

            Text(
                text = AuthStrings.WELCOME_SUBTITLE,
                color = Gray,
                fontSize = AuthDims.SUBTITLE_FONT_SIZE,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_32))

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
                        tint = NavyText,
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
                        tint = NavyText,
                        modifier = Modifier.size(AuthDims.OPTION_ICON_SIZE)
                    )
                }
            )

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))

            TermsText()

            Spacer(modifier = Modifier.height(AuthDims.SPACE_24))
        }
    }
}

@Composable
private fun WelcomeIllustration() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.ic_welcome_illustration),
            contentDescription = AuthStrings.CD_WELCOME_ILLUSTRATION,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = AuthDims.SCRIPT_TOP_PADDING,
                    end = AuthDims.SCRIPT_END_PADDING
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = AuthStrings.WELCOME_ILLUSTRATION_TEXT,
                color = OnLightArtwork,
                fontSize = AuthDims.SCRIPT_FONT_SIZE,
                lineHeight = AuthDims.SCRIPT_LINE_HEIGHT,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Canvas(
                modifier = Modifier
                    .width(AuthDims.SWOOSH_WIDTH)
                    .height(AuthDims.SWOOSH_HEIGHT)
            ) {
                val path = Path().apply {
                    moveTo(0f, size.height * SWOOSH_START_Y_RATIO)
                    quadraticBezierTo(
                        size.width * SWOOSH_CONTROL_X_RATIO,
                        0f,
                        size.width,
                        size.height * SWOOSH_END_Y_RATIO
                    )
                }
                drawPath(
                    path = path,
                    color = BrandCoral,
                    style = Stroke(
                        width = AuthDims.SWOOSH_STROKE_WIDTH.toPx(),
                        cap = StrokeCap.Round
                    )
                )
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(White)
            .border(AuthDims.OPTION_BORDER_WIDTH, HomeSearchBarBorder, shape)
            .clickable(onClick = onClick)
            .padding(
                horizontal = AuthDims.OPTION_HORIZONTAL_PADDING,
                vertical = AuthDims.OPTION_VERTICAL_PADDING
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading()

        Text(
            text = label,
            color = NavyText,
            fontSize = AuthDims.OPTION_FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = AuthStrings.CD_OPTION_ARROW,
            tint = NavyText,
            modifier = Modifier.size(AuthDims.OPTION_ICON_SIZE)
        )
    }
}

@Composable
internal fun AuthBrandLogo(fontSize: TextUnit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = AppStrings.APP_NAME_FIRST,
            color = BrandBlue,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = AppStrings.APP_NAME_ACCENT,
            color = BrandCoral,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold
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

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    RealeTheme {
        WelcomeScreen(
            onGoogleClick = {},
            onMobileClick = {},
            onGuestClick = {}
        )
    }
}
