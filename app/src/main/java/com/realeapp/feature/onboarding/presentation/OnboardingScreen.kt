package com.realeapp.feature.onboarding.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.ContextCompat
import com.realeapp.AppStrings
import com.realeapp.R
import com.realeapp.feature.onboarding.data.resolveCurrentCityAndLocation
import com.realeapp.ui.theme.AppBackground
import com.realeapp.util.Logger
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.NavyText
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.RealeTheme
import kotlinx.coroutines.launch

private const val ONBOARDING_PAGE_COUNT = 4
private const val CURVE_EDGE_RATIO = 0.55f
private const val CURVE_LIFT_RATIO = 0.25f
private const val PROGRESS_TRACK_ALPHA = 0.2f
private const val PROGRESS_TRACK_ALPHA_ON_SPLASH = 0.3f

/**
 * Full-screen onboarding flow: three intro pages followed by a location permission request.
 *
 * @param onComplete Called once the user finishes the flow.
 * @param onSkipToCity Called when the user skips location access to pick a city manually.
 * @param onLocationResolved Called with the resolved (city, location) after the user grants
 * location access and the device position is reverse-geocoded.
 * @param modifier Optional modifier for the root container.
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onSkipToCity: () -> Unit,
    onLocationResolved: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_PAGE_COUNT })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isResolvingLocation by remember { mutableStateOf(false) }

    val resolveAndFinish: () -> Unit = {
        scope.launch {
            isResolvingLocation = true
            val resolved = resolveCurrentCityAndLocation(context)
            if (resolved != null) {
                onLocationResolved(resolved.first, resolved.second)
            }
            isResolvingLocation = false
            onComplete()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.any { it.value }) {
            resolveAndFinish()
        } else {
            onComplete()
        }
    }

    val onSkipToLocation: () -> Unit = {
        scope.launch { pagerState.animateScrollToPage(ONBOARDING_PAGE_COUNT - 1) }
    }

    val onNext: () -> Unit = {
        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
    }

    val onAllowLocation: () -> Unit = {
        Logger.d("OnboardingScreen", "onAllowLocation clicked")
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            resolveAndFinish()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    BackHandler(enabled = pagerState.currentPage > 0) {
        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            beyondViewportPageCount = 1
        ) { page ->
            OnboardingPageContent(
                page = page,
                onSkipToLocation = onSkipToLocation,
                modifier = Modifier.fillMaxSize()
            )
        }

        OnboardingFooter(
            currentPage = pagerState.currentPage,
            onNext = onNext,
            onAllowLocation = onAllowLocation,
            onSkip = {
                Logger.d("OnboardingScreen", "onSkip clicked -> navigating to city picker")
                onSkipToCity()
            },
            isResolvingLocation = isResolvingLocation,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun OnboardingPageContent(
    page: Int,
    onSkipToLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (page) {
        0 -> SplashPage(modifier = modifier)
        1 -> FindPlacePage(modifier = modifier)
        2 -> NextChapterPage(
            onSkip = onSkipToLocation,
            modifier = modifier
        )
        3 -> LocationPage(modifier = modifier)
        else -> Box(modifier = modifier)
    }
}

@Composable
private fun BrandLogo(
    firstColor: Color,
    accentColor: Color,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = AppStrings.APP_NAME_FIRST,
            color = firstColor,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = AppStrings.APP_NAME_ACCENT,
            color = accentColor,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun SplashPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBlue)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        BrandLogo(
            firstColor = OnBrandContent,
            accentColor = BrandCoral,
            fontSize = OnboardingDims.SPLASH_LOGO_FONT_SIZE,
            modifier = Modifier.padding(horizontal = OnboardingDims.SCREEN_PADDING)
        )

        Spacer(modifier = Modifier.height(OnboardingDims.SPACE_16))

        Text(
            text = OnboardingStrings.ONBOARDING_TAGLINE,
            color = OnBrandContent,
            fontSize = OnboardingDims.TAGLINE_FONT_SIZE,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = OnboardingDims.SCREEN_PADDING)
        )

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.ic_splash_illustration),
            contentDescription = OnboardingStrings.CD_SPLASH_ILLUSTRATION,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(OnBrandContent.copy(alpha = 0.5f))
        )
    }
}

@Composable
private fun FindPlacePage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_villa),
            contentDescription = OnboardingStrings.CD_VILLA_ILLUSTRATION,
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.BottomCenter,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        val curveColor = AppBackground

        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(OnboardingDims.IMAGE_CURVE_HEIGHT)
        ) {
            val curvePath = Path().apply {
                moveTo(0f, size.height)
                lineTo(0f, size.height * CURVE_EDGE_RATIO)
                quadraticBezierTo(
                    size.width / 2f,
                    -size.height * CURVE_LIFT_RATIO,
                    size.width,
                    size.height * CURVE_EDGE_RATIO
                )
                lineTo(size.width, size.height)
                close()
            }
            drawPath(path = curvePath, color = curveColor)
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = OnboardingDims.FIND_PLACE_HEADER_TOP)
                .padding(horizontal = OnboardingDims.SCREEN_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandLogo(
                firstColor = BrandBlue,
                accentColor = BrandCoral,
                fontSize = OnboardingDims.FIND_PLACE_LOGO_FONT_SIZE
            )

            Spacer(modifier = Modifier.height(OnboardingDims.SPACE_8))

            Text(
                text = OnboardingStrings.FIND_PLACE_TITLE,
                color = Black,
                fontSize = OnboardingDims.FIND_PLACE_SUBTITLE_FONT_SIZE,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NextChapterPage(
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding_chapter),
            contentDescription = OnboardingStrings.CD_CHAPTER_ILLUSTRATION,
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.BottomCenter,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OnboardingDims.TOP_BAR_HEIGHT)
                    .padding(horizontal = OnboardingDims.SCREEN_PADDING),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = OnboardingStrings.BUTTON_SKIP,
                        color = Gray,
                        fontSize = OnboardingDims.SKIP_TEXT_FONT_SIZE
                    )
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = OnboardingDims.SCREEN_PADDING)
            ) {
                Text(
                    text = OnboardingStrings.NEXT_CHAPTER_TITLE,
                    color = NavyText,
                    fontSize = OnboardingDims.NEXT_CHAPTER_TITLE_FONT_SIZE,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = OnboardingDims.NEXT_CHAPTER_TITLE_LINE_HEIGHT
                )

                Spacer(modifier = Modifier.height(OnboardingDims.SPACE_16))

                Text(
                    text = OnboardingStrings.ONBOARDING_TAGLINE,
                    color = Gray,
                    fontSize = OnboardingDims.SUBTITLE_FONT_SIZE
                )
            }
        }
    }
}

@Composable
private fun LocationPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .padding(OnboardingDims.SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.weight(0.4f))

        Box(
            modifier = Modifier
                .size(OnboardingDims.LOCATION_CIRCLE_SIZE)
                .clip(CircleShape)
                .background(BrandBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = OnboardingStrings.CD_LOCATION_ICON,
                modifier = Modifier.size(OnboardingDims.LOCATION_ICON_SIZE),
                tint = BrandBlue
            )
        }

        Spacer(modifier = Modifier.height(OnboardingDims.SPACE_24))

        Text(
            text = OnboardingStrings.LOCATION_TITLE,
            color = Black,
            fontSize = OnboardingDims.TITLE_FONT_SIZE,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(OnboardingDims.SPACE_8))

        Text(
            text = OnboardingStrings.LOCATION_SUBTITLE,
            color = Gray,
            fontSize = OnboardingDims.SUBTITLE_FONT_SIZE,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(0.6f))
    }
}

@Composable
private fun OnboardingFooter(
    currentPage: Int,
    onNext: () -> Unit,
    onAllowLocation: () -> Unit,
    onSkip: () -> Unit,
    isResolvingLocation: Boolean = false,
    modifier: Modifier = Modifier
) {
    val background = if (currentPage == 0) BrandBlue else AppBackground
    val progressColor = if (currentPage == 0) OnBrandContent else BrandBlue
    val trackColor = if (currentPage == 0) {
        OnBrandContent.copy(alpha = PROGRESS_TRACK_ALPHA_ON_SPLASH)
    } else {
        Gray.copy(alpha = PROGRESS_TRACK_ALPHA)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .navigationBarsPadding()
            .padding(OnboardingDims.SCREEN_PADDING)
    ) {
        if (currentPage < ONBOARDING_PAGE_COUNT - 1) {
            PageProgressIndicator(
                progress = currentPage / (ONBOARDING_PAGE_COUNT - 1).toFloat(),
                progressColor = progressColor,
                trackColor = trackColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(OnboardingDims.SPACE_24))

            val buttonText = when (currentPage) {
                0, 2 -> OnboardingStrings.BUTTON_GET_STARTED
                else -> OnboardingStrings.BUTTON_NEXT
            }

            val buttonContainerColor = when (currentPage) {
                0 -> Color.White
                2 -> BrandCoral
                else -> BrandBlue
            }
            val buttonContentColor = if (currentPage == 0) BrandBlue else Color.White

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OnboardingDims.BUTTON_HEIGHT),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonContainerColor,
                    contentColor = buttonContentColor
                ),
                shape = RoundedCornerShape(OnboardingDims.BUTTON_CORNER_RADIUS)
            ) {
                Text(
                    text = buttonText,
                    fontSize = OnboardingDims.BUTTON_TEXT_FONT_SIZE,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(OnboardingDims.SPACE_8))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = OnboardingStrings.CD_NEXT,
                    modifier = Modifier.size(OnboardingDims.BUTTON_ICON_SIZE)
                )
            }
        } else {
            Button(
                onClick = onAllowLocation,
                enabled = !isResolvingLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OnboardingDims.BUTTON_HEIGHT),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(OnboardingDims.BUTTON_CORNER_RADIUS)
            ) {
                if (isResolvingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(OnboardingDims.BUTTON_ICON_SIZE),
                        color = Color.White,
                        strokeWidth = OnboardingDims.BUTTON_PROGRESS_STROKE
                    )
                } else {
                    Text(
                        text = OnboardingStrings.BUTTON_ALLOW_LOCATION,
                        fontSize = OnboardingDims.BUTTON_TEXT_FONT_SIZE,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(OnboardingDims.SPACE_12))

            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(OnboardingDims.BUTTON_HEIGHT),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue),
                shape = RoundedCornerShape(OnboardingDims.BUTTON_CORNER_RADIUS)
            ) {
                Text(
                    text = OnboardingStrings.BUTTON_SKIP_FOR_NOW,
                    fontSize = OnboardingDims.BUTTON_TEXT_FONT_SIZE,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PageProgressIndicator(
    progress: Float,
    progressColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(OnboardingDims.PROGRESS_BAR_WIDTH_FRACTION)
            .height(OnboardingDims.PROGRESS_BAR_HEIGHT)
            .clip(CircleShape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(CircleShape)
                .background(progressColor)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashPagePreview() {
    RealeTheme {
        SplashPage()
    }
}

@Preview(showBackground = true)
@Composable
private fun FindPlacePagePreview() {
    RealeTheme {
        FindPlacePage()
    }
}

@Preview(showBackground = true)
@Composable
private fun NextChapterPagePreview() {
    RealeTheme {
        NextChapterPage(onSkip = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationPagePreview() {
    RealeTheme {
        LocationPage()
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingFooterPreview() {
    RealeTheme {
        OnboardingFooter(
            currentPage = 1,
            onNext = {},
            onAllowLocation = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingLocationFooterPreview() {
    RealeTheme {
        OnboardingFooter(
            currentPage = ONBOARDING_PAGE_COUNT - 1,
            onNext = {},
            onAllowLocation = {},
            onSkip = {}
        )
    }
}
