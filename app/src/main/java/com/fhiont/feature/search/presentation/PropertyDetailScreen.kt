package com.fhiont.feature.search.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fence
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.fhiont.R
import com.fhiont.feature.search.domain.model.Amenity
import com.fhiont.feature.search.domain.model.BedroomType
import com.fhiont.feature.search.domain.model.ListingCategory
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.presentation.components.formatIndianPrice
import com.fhiont.ui.theme.FilterChipSelectedContainer
import com.fhiont.ui.theme.FilterChipSelectedLabel
import com.fhiont.ui.preview.PreviewData
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.theme.Black
import com.fhiont.ui.theme.BrandCoral
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.Error
import com.fhiont.ui.theme.HomeCategoryUnselected
import com.fhiont.ui.theme.HomeSearchBarBorder
import com.fhiont.ui.theme.HomeTextSecondary
import com.fhiont.ui.theme.IsDarkAppTheme
import com.fhiont.ui.theme.MediaScrim
import com.fhiont.ui.theme.OnBrandContent
import com.fhiont.ui.theme.OnControlAccent
import com.fhiont.ui.theme.OnMediaContent
import com.fhiont.ui.theme.FhiontTheme
import com.fhiont.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToLong


private const val MIN_ZOOM = 1f
private const val MAX_ZOOM = 5f
private const val ISO_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val DISPLAY_TIMESTAMP_PATTERN = "dd MMM yyyy, hh:mm a"
private const val DOUBLE_TAP_ZOOM = 2.5f
private const val MAP_ZOOM_LEVEL = 15f
private const val MAX_VISIBLE_THUMBS = 5
private const val HERO_AUTO_ADVANCE_MS = 3000L
private const val MAX_STATS = 5
private const val DESCRIPTION_COLLAPSED_LINES = 3
private const val ITEMS_BEFORE_SECTIONS = 3


@Composable
fun PropertyDetailScreen(
    property: Property,
    onClose: () -> Unit,
    onLike: (() -> Unit)? = null,
    enquiryCount: Int? = null,
    onViewEnquiries: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val images = remember(property.id, property.images) {
        property.images.filter { it.isNotBlank() }
            .ifEmpty {
                listOf(DetailStrings.FALLBACK_IMAGE_PREFIX + property.id + DetailStrings.FALLBACK_IMAGE_SUFFIX)
            }
    }
    var selectedImage by remember(property.id) { mutableIntStateOf(0) }
    var fullScreenPage by remember { mutableStateOf<Int?>(null) }
    var showEnquire by remember { mutableStateOf(false) }

    // Auto-rotate the hero every few seconds, looping back to the first image.
    // Keyed on selectedImage so a manual thumbnail tap restarts the timer;
    // pauses while the fullscreen viewer is open or there's only one image.
    LaunchedEffect(images.size, selectedImage, fullScreenPage) {
        if (images.size > 1 && fullScreenPage == null) {
            delay(HERO_AUTO_ADVANCE_MS)
            selectedImage = (selectedImage + 1) % images.size
        }
    }

    val hasDetails = propertyHasDetails(property)
    val hasOverview = property.description.isNotBlank()
    val hasLocation = (property.latitude != null && property.longitude != null) ||
        !property.address.isNullOrBlank() || property.city.isNotBlank()
    val locationItemIndex = ITEMS_BEFORE_SECTIONS +
        (if (hasDetails) 1 else 0) +
        (if (hasOverview) 1 else 0)

    Scaffold(
        modifier = modifier,
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            DetailBottomBar(
                phone = property.agentPhone,
                onCall = { dialAgent(context, property.agentPhone) },
                onEnquire = { showEnquire = true }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = DetailDims.LIST_BOTTOM_PADDING)
        ) {
            item {
                // Hero UI: cover image with overlay actions and the thumbnail strip card.
                HeroSection(
                    property = property,
                    images = images,
                    selectedImage = selectedImage,
                    onClose = onClose,
                    onLike = onLike,
                    onShare = { shareProperty(context, property) },
                    onShowPhotos = { fullScreenPage = selectedImage },
                    onShowMap = {
                        if (hasLocation) {
                            coroutineScope.launch { listState.animateScrollToItem(locationItemIndex) }
                        }
                    },
                    onImageClick = { fullScreenPage = selectedImage },
                    onSelectImage = { selectedImage = it }
                )
            }

            item {
                // Primary info UI: sale badge, title, location, and price.
                InfoSection(
                    property = property,
                    enquiryCount = enquiryCount,
                    onViewEnquiries = onViewEnquiries
                )
            }

            item {
                // Quick stats UI: beds, area, type, facing, furnishing.
                StatsCard(property = property)
            }

            if (hasDetails) {
                item {
                    // Detailed property facts: category, configuration, age, areas, status, etc.
                    DetailsSection(property = property)
                }
            }

            if (hasOverview) {
                item {
                    OverviewSection(description = property.description)
                }
            }

            if (hasLocation) {
                item {
                    LocationSection(property = property)
                }
            }
        }
    }

    // Full-screen zoomable image UI opened by tapping the hero or a thumbnail.
    fullScreenPage?.let { startPage ->
        FullScreenImageViewer(
            images = images,
            initialPage = startPage,
            contentDescription = property.title,
            onDismiss = { fullScreenPage = null }
        )
    }

    if (showEnquire) {
        EnquireBottomSheet(
            property = property,
            onDismiss = { showEnquire = false }
        )
    }
}

@Composable
private fun HeroSection(
    property: Property,
    images: List<String>,
    selectedImage: Int,
    onClose: () -> Unit,
    onLike: (() -> Unit)?,
    onShare: () -> Unit,
    onShowPhotos: () -> Unit,
    onShowMap: () -> Unit,
    onImageClick: () -> Unit,
    onSelectImage: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DetailDims.HERO_HEIGHT + DetailDims.THUMB_OVERLAP)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailDims.HERO_HEIGHT)
        ) {
            if (LocalInspectionMode.current) {
                Image(
                    painter = painterResource(R.drawable.ic_welcome_home),
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onImageClick)
                )
            } else {
                AnimatedContent(
                    targetState = selectedImage.coerceIn(0, images.lastIndex),
                    transitionSpec = {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    },
                    label = "heroImage",
                    modifier = Modifier.fillMaxSize()
                ) { imageIndex ->
                    AsyncImage(
                        model = images[imageIndex],
                        contentDescription = property.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onImageClick)
                    )
                }
            }

            if (images.size > 1) {
                ImageDotsIndicator(
                    count = images.size,
                    activeIndex = selectedImage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = DetailDims.HERO_DOTS_BOTTOM_PADDING)
                        .clip(RoundedCornerShape(DetailDims.HERO_DOTS_CORNER_RADIUS))
                        .background(MediaScrim.copy(alpha = 0.6f))
                        .padding(
                            horizontal = DetailDims.HERO_DOTS_HORIZONTAL_PADDING,
                            vertical = DetailDims.HERO_DOTS_VERTICAL_PADDING
                        )
                )
            }

            // Top overlay UI: back, favorite, and share actions over the image.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(DetailDims.HERO_OVERLAY_PADDING),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroCircleButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = DetailStrings.CD_BACK,
                    onClick = onClose
                )
                Row(horizontalArrangement = Arrangement.spacedBy(DetailDims.HERO_BUTTON_SPACING)) {
                    if (onLike != null) {
                        if (property.isLiked == true) {
                            HeroCircleButton(
                                icon = Icons.Filled.Favorite,
                                contentDescription = DetailStrings.CD_LIKE,
                                tint = Error,
                                onClick = onLike
                            )
                        } else {
                            HeroCircleButton(
                                icon = Icons.Filled.FavoriteBorder,
                                contentDescription = DetailStrings.CD_LIKE,
                                onClick = onLike
                            )
                        }
                    }
                    HeroCircleButton(
                        icon = Icons.Filled.Share,
                        contentDescription = DetailStrings.CD_SHARE,
                        onClick = onShare
                    )
                }
            }

        }

        ThumbnailStrip(
            images = images,
            selectedImage = selectedImage,
            onSelect = onSelectImage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = DetailDims.SCREEN_PADDING)
        )
    }
}

@Composable
private fun ImageDotsIndicator(
    count: Int,
    activeIndex: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = OnMediaContent,
    inactiveColor: Color = OnMediaContent.copy(alpha = DetailDims.HERO_DOT_INACTIVE_ALPHA)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DetailDims.HERO_DOT_SPACING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { index ->
            val color = if (index == activeIndex) {
                activeColor.copy(alpha = DetailDims.HERO_DOT_ACTIVE_ALPHA)
            } else {
                inactiveColor
            }
            Box(
                modifier = Modifier
                    .size(DetailDims.HERO_DOT_SIZE)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun HeroCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = White,
    backgroundColor: Color = Color.Unspecified
) {
    val resolvedBackground = if (backgroundColor == Color.Unspecified) {
        Black.copy(alpha = 0.45f)
    } else {
        backgroundColor
    }
    Box(
        modifier = Modifier
            .size(DetailDims.HERO_BUTTON_SIZE)
            .clip(CircleShape)
            .background(resolvedBackground)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(DetailDims.HERO_BUTTON_ICON_SIZE)
        )
    }
}

@Composable
private fun MediaPill(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    selected: Boolean,
    onClick: (() -> Unit)?
) {
    val backgroundColor = if (selected) ControlAccent else White
    val contentColor = if (selected) OnControlAccent else Black
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(DetailDims.MEDIA_PILL_CORNER_RADIUS))
            .background(backgroundColor)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(
                horizontal = DetailDims.MEDIA_PILL_HORIZONTAL_PADDING,
                vertical = DetailDims.MEDIA_PILL_VERTICAL_PADDING
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DetailDims.MEDIA_PILL_CONTENT_SPACING)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(DetailDims.MEDIA_PILL_ICON_SIZE)
        )
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ThumbnailStrip(
    images: List<String>,
    selectedImage: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(DetailDims.THUMB_CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DetailDims.THUMB_CARD_PADDING),
            horizontalArrangement = Arrangement.spacedBy(DetailDims.THUMB_SPACING, Alignment.CenterHorizontally)
        ) {
            itemsIndexed(images.take(MAX_VISIBLE_THUMBS)) { index, url ->
                val isOverflowTile = index == MAX_VISIBLE_THUMBS - 1 && images.size > MAX_VISIBLE_THUMBS
                val isSelected = index == selectedImage
                val thumbSize = if (isSelected) DetailDims.THUMB_SIZE else DetailDims.THUMB_UNSELECTED_SIZE
                Box(
                    modifier = Modifier
                        .size(thumbSize)
                        .clip(RoundedCornerShape(DetailDims.THUMB_CORNER_RADIUS))
                        .background(HomeCategoryUnselected)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    width = DetailDims.THUMB_SELECTED_BORDER,
                                    color = ControlAccent,
                                    shape = RoundedCornerShape(DetailDims.THUMB_CORNER_RADIUS)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onSelect(index) }
                ) {
                    if (LocalInspectionMode.current) {
                        Image(
                            painter = painterResource(R.drawable.ic_welcome_home),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    if (isOverflowTile) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MediaScrim.copy(alpha = DetailDims.OVERLAY_SCRIM_ALPHA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = DetailStrings.MORE_PHOTOS_PREFIX + (images.size - MAX_VISIBLE_THUMBS + 1),
                                color = OnMediaContent,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullScreenImageViewer(
    images: List<String>,
    initialPage: Int,
    contentDescription: String?,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { images.size }
        )
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        // Zoom resets whenever a different image becomes visible.
        LaunchedEffect(pagerState.currentPage) {
            scale = 1f
            offset = Offset.Zero
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MediaScrim)
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = scale <= 1f,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val isCurrentPage = page == pagerState.currentPage
                AsyncImage(
                    model = images[page],
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(isCurrentPage) {
                            if (!isCurrentPage) return@pointerInput
                            detectTapGestures(
                                onDoubleTap = {
                                    if (scale > 1f) {
                                        scale = 1f
                                        offset = Offset.Zero
                                    } else {
                                        scale = DOUBLE_TAP_ZOOM
                                    }
                                }
                            )
                        }
                        .pointerInput(isCurrentPage) {
                            if (!isCurrentPage) return@pointerInput
                            detectTransformGestures { _, pan, zoomChange, _ ->
                                scale = (scale * zoomChange).coerceIn(MIN_ZOOM, MAX_ZOOM)
                                val maxX = (size.width * (scale - 1f)) / 2f
                                val maxY = (size.height * (scale - 1f)) / 2f
                                offset = Offset(
                                    x = (offset.x + pan.x * scale).coerceIn(-maxX, maxX),
                                    y = (offset.y + pan.y * scale).coerceIn(-maxY, maxY)
                                )
                            }
                        }
                        .graphicsLayer(
                            scaleX = if (isCurrentPage) scale else 1f,
                            scaleY = if (isCurrentPage) scale else 1f,
                            translationX = if (isCurrentPage) offset.x else 0f,
                            translationY = if (isCurrentPage) offset.y else 0f
                        )
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(DetailDims.VIEWER_CLOSE_PADDING)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = DetailStrings.CD_CLOSE,
                    tint = OnMediaContent
                )
            }

            if (images.size > 1) {
                Text(
                    text = "${pagerState.currentPage + 1}/${images.size}",
                    color = OnMediaContent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(DetailDims.VIEWER_COUNTER_PADDING)
                )
            }
        }
    }
}

@Composable
private fun InfoSection(
    property: Property,
    enquiryCount: Int? = null,
    onViewEnquiries: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING),
        verticalArrangement = Arrangement.spacedBy(DetailDims.CONTENT_SPACING)
    ) {
        // Property summary tag, e.g. "Residential Apartment for Buy"
        val propertySummary = buildPropertySummary(property)
        if (propertySummary.isNotBlank()) {
            Text(
                text = propertySummary,
                color = ControlAccent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        // Property title, e.g. "Luxury 3 BHK Apartment"
        Text(
            text = property.title,
            color = Black,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        // Location row with pin icon, e.g. "📍 Panjim, Goa"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DetailDims.CONTENT_SPACING_SMALL)
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = HomeTextSecondary,
                modifier = Modifier.size(DetailDims.LOCATION_ICON_SIZE)
            )
            Text(
                text = buildShortLocation(property),
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Price row, e.g. "₹1.25 Cr" with optional "₹6,944 per sq ft" below
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatIndianPrice(property.price, property.isRentProperty()),
                    color = ControlAccent,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                formatPricePerSqFt(property)?.let { perSqFt ->
                    Text(
                        text = perSqFt,
                        color = HomeTextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // Key highlights below price, e.g. "🏊 Private Pool", "🅿 Parking", "🏋 Gymnasium"
        if (property.amenities.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SearchDims.FILTER_ITEM_SPACING),
                verticalArrangement = Arrangement.spacedBy(SearchDims.FILTER_ITEM_SPACING)
            ) {
                property.amenities.forEach { amenity ->
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text(amenityDisplayLabel(amenity)) },
                        leadingIcon = {
                            Icon(
                                imageVector = amenityDisplayIcon(amenity),
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FilterChipSelectedContainer,
                            selectedLabelColor = FilterChipSelectedLabel,
                            selectedLeadingIconColor = FilterChipSelectedLabel
                        ),
                        elevation = FilterChipDefaults.filterChipElevation(
                            elevation = SearchDims.FILTER_CHIP_ELEVATION
                        ),
                        border = null,
                        shape = CircleShape
                    )
                }
            }
        }

        // Enquiry count pill, e.g. "💬 3 Enquiries" (owner-only)
        if (enquiryCount != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DetailDims.CONTENT_SPACING)
            ) {
                MediaPill(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = "$enquiryCount ${DetailStrings.LABEL_ENQUIRIES}",
                    contentDescription = DetailStrings.CD_VIEW_ENQUIRIES,
                    selected = false,
                    onClick = onViewEnquiries
                )
            }
        }
    }
}

private data class StatItem(
    val icon: ImageVector,
    val value: String,
    val label: String
)

@Composable
private fun StatsCard(property: Property) {
    val stats = listOfNotNull(
        bedroomCount(property.bedroomType).takeIf { it > 0 }?.let {
            StatItem(Icons.Filled.KingBed, it.toString(), DetailStrings.LABEL_BEDS)
        },
        property.bathrooms?.takeIf { it > 0 }?.let {
            StatItem(Icons.Filled.Bathtub, it.toString(), DetailStrings.LABEL_BATHS)
        },
        primaryArea(property)?.let {
            StatItem(Icons.Filled.SquareFoot, formatAreaValue(it), DetailStrings.LABEL_SQ_FT)
        },
        property.propertyType?.let {
            StatItem(Icons.Filled.Home, it.label, DetailStrings.LABEL_TYPE)
        },
        property.facing?.let {
            StatItem(Icons.Filled.Explore, it.label, DetailStrings.LABEL_FACING)
        },
        property.furnishing?.let {
            StatItem(Icons.Filled.Chair, it.label, DetailStrings.LABEL_FURNISHING)
        }
    ).take(MAX_STATS)

    if (stats.isEmpty()) return


}


// Overview section with expandable description text and "Read More" / "Read Less" toggle.
@Composable
private fun OverviewSection(description: String) {
    var expanded by remember { mutableStateOf(false) }
    var hasOverflow by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING)
            .padding(top = DetailDims.SECTION_SPACING),
        verticalArrangement = Arrangement.spacedBy(DetailDims.SECTION_TITLE_SPACING)
    ) {
        // Description text, e.g. "Spacious and well ventilated apartment with modern fittings..."
        Text(
            text = description,
            color = HomeTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = if (expanded) Int.MAX_VALUE else DESCRIPTION_COLLAPSED_LINES,
            overflow = TextOverflow.Ellipsis,
            // Detect if text exceeds maxLines (DESCRIPTION_COLLAPSED_LINES = 3); sets hasOverflow = true
            // so "Read More" appears only when the description is long enough to be truncated.
            onTextLayout = { result ->
                if (!expanded) hasOverflow = result.hasVisualOverflow
            }
        )
        // Toggle link shown only when text overflows collapsed lines, e.g. "Read More ▼" / "Read Less ▲"
        if (hasOverflow || expanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    text = if (expanded) DetailStrings.ACTION_READ_LESS else DetailStrings.ACTION_READ_MORE,
                    color = ControlAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = ControlAccent,
                    modifier = Modifier.size(DetailDims.READ_MORE_ICON_SIZE)
                )
            }
        }
    }
}

private fun amenityDisplayIcon(amenity: Amenity): ImageVector = when (amenity) {
    Amenity.SWIMMING_POOL -> Icons.Filled.Pool
    Amenity.MODULAR_KITCHEN -> Icons.Filled.Kitchen
    Amenity.PARKING, Amenity.AUTOMATED_PARKING_SYSTEMS -> Icons.Filled.LocalParking
    Amenity.GARDEN -> Icons.Filled.Grass
    Amenity.SECURITY -> Icons.Filled.Security
    Amenity.SCHOOLS_HOSPITALS -> Icons.Filled.LocationOn
    Amenity.LIFT -> Icons.Filled.Elevator
    Amenity.POWER_BACKUP -> Icons.Filled.Power
    Amenity.CLUB_HOUSE -> Icons.Filled.HolidayVillage
    Amenity.GAS_PIPELINE -> Icons.Filled.Whatshot
    Amenity.GYMNASIUM -> Icons.Filled.FitnessCenter
    Amenity.PLAYGROUND -> Icons.Filled.SportsSoccer
    Amenity.EV_CHARGING_POINTS -> Icons.Filled.ElectricCar
    Amenity.CCTV -> Icons.Filled.Videocam
    Amenity.GATED_COMMUNITY -> Icons.Filled.Fence
    Amenity.PET_FRIENDLY -> Icons.Filled.Pets
}

private fun amenityDisplayLabel(amenity: Amenity): String = when (amenity) {
    Amenity.SWIMMING_POOL -> DetailStrings.HL_PRIVATE_POOL
    Amenity.MODULAR_KITCHEN -> DetailStrings.HL_MODULAR_KITCHEN
    Amenity.PARKING, Amenity.AUTOMATED_PARKING_SYSTEMS -> DetailStrings.HL_COVERED_PARKING
    Amenity.GARDEN -> DetailStrings.HL_LANDSCAPED_GARDEN
    Amenity.SECURITY -> DetailStrings.HL_24X7_SECURITY
    Amenity.SCHOOLS_HOSPITALS -> DetailStrings.HL_CLOSE_TO_SCHOOLS_HOSPITALS
    else -> amenity.label
}

private fun buildLocationString(property: Property): String {
    return buildString {
        append(property.locality)
        if (property.city.isNotBlank()) {
            if (isNotBlank()) append(", ")
            append(property.city)
        }
        if (!property.pincode.isNullOrBlank()) {
            if (isNotBlank()) append(" - ")
            append(property.pincode)
        }
        if (!property.address.isNullOrBlank()) {
            if (isNotBlank()) append("\n")
            append(property.address)
        }
    }
}

// Location section with map/address content and a floating "View on Map" button.
@Composable
private fun LocationSection(property: Property) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING)
            .padding(top = DetailDims.SECTION_SPACING)
    ) {
        LocationContent(property = property)

        // Floating "View on Map" pill overlaid on bottom-end of the map
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(DetailDims.MAP_FAB_PADDING)
                .clickable { openInMaps(context, property) },
            shape = RoundedCornerShape(DetailDims.MAP_FAB_CORNER_RADIUS),
            color = White,
            shadowElevation = DetailDims.MAP_FAB_ELEVATION
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = DetailDims.MAP_FAB_HORIZONTAL_PADDING,
                    vertical = DetailDims.MAP_FAB_VERTICAL_PADDING
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DetailDims.CONTENT_SPACING_SMALL)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = ControlAccent,
                    modifier = Modifier.size(DetailDims.MAP_FAB_ICON_SIZE)
                )
                Text(
                    text = DetailStrings.ACTION_VIEW_ON_MAP,
                    color = ControlAccent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Interactive Google Map with marker, or address-only fallback when map is unavailable.
@Composable
private fun LocationContent(property: Property) {
    val context = LocalContext.current
    val apiKey = remember { readMapApiKey(context) }
    val lat = property.latitude
    val lng = property.longitude
    // Google Map with pin, e.g. marker at "Luxury 3 BHK Apartment – Panjim, Goa"
    if (lat != null && lng != null && !apiKey.isNullOrBlank() && apiKey != DetailStrings.MAPS_KEY_PLACEHOLDER) {
        val propertyLatLng = LatLng(lat, lng)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(propertyLatLng, MAP_ZOOM_LEVEL)
        }
        var isMapLoaded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailDims.MAP_HEIGHT)
                .clip(RoundedCornerShape(DetailDims.MAP_CORNER_RADIUS))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                ),
                mapColorScheme = if (IsDarkAppTheme) ComposeMapColorScheme.DARK else ComposeMapColorScheme.LIGHT,
                onMapLoaded = { isMapLoaded = true }
            ) {
                Marker(
                    state = MarkerState(position = propertyLatLng),
                    title = property.title,
                    snippet = buildShortLocation(property)
                )
            }

            // Map loading UI remains visible until Google Maps reports readiness.
            if (!isMapLoaded) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ControlAccent
                )
            }
        }
    } else {
        // Address-only location UI used when an interactive map cannot be displayed.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailDims.MAP_HEIGHT)
                .clip(RoundedCornerShape(DetailDims.MAP_CORNER_RADIUS))
                .border(
                    width = DetailDims.BORDER_WIDTH,
                    color = HomeSearchBarBorder,
                    shape = RoundedCornerShape(DetailDims.MAP_CORNER_RADIUS)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                DetailDims.MAP_PLACEHOLDER_SPACING,
                Alignment.CenterHorizontally
            )
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = ControlAccent,
                modifier = Modifier.size(DetailDims.MAP_PLACEHOLDER_ICON_SIZE)
            )
            Text(
                text = buildShortLocation(property),
                color = HomeTextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

// Sticky bottom bar with "Call" and "Enquire Now" buttons.
@Composable
private fun DetailBottomBar(
    phone: String,
    onCall: () -> Unit,
    onEnquire: () -> Unit
) {
    Surface(
        color = White,
        shadowElevation = DetailDims.BOTTOM_BAR_ELEVATION
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(DetailDims.BOTTOM_BAR_PADDING),
            horizontalArrangement = Arrangement.spacedBy(DetailDims.BOTTOM_BUTTON_SPACING)
        ) {
            // Outlined "Call" button, e.g. "📞 Call"
            OutlinedButton(
                onClick = onCall,
                enabled = phone.isNotBlank(),
                modifier = Modifier
                    .weight(1f)
                    .height(DetailDims.BOTTOM_BUTTON_HEIGHT),
                shape = RoundedCornerShape(DetailDims.BOTTOM_BUTTON_CORNER_RADIUS),
                border = BorderStroke(DetailDims.BORDER_WIDTH, ControlAccent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ControlAccent)
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    modifier = Modifier.size(DetailDims.BOTTOM_BUTTON_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(DetailDims.BOTTOM_BUTTON_ICON_SPACING))
                Text(
                    text = DetailStrings.ACTION_CALL,
                    fontWeight = FontWeight.Bold
                )
            }
            // Filled "Enquire Now" button, e.g. "💬 Enquire Now"
            Button(
                onClick = onEnquire,
                enabled = true,
                modifier = Modifier
                    .weight(1f)
                    .height(DetailDims.BOTTOM_BUTTON_HEIGHT),
                shape = RoundedCornerShape(DetailDims.BOTTOM_BUTTON_CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandCoral,
                    contentColor = OnBrandContent
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(DetailDims.BOTTOM_BUTTON_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(DetailDims.BOTTOM_BUTTON_ICON_SPACING))
                Text(
                    text = DetailStrings.ACTION_ENQUIRE,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun propertyHasDetails(property: Property): Boolean {
    return property.bedroomType != null ||
        property.bathrooms?.takeIf { it > 0 } != null ||
        property.furnishing != null ||
        property.facing != null ||
        property.age != null ||
        property.carpetArea?.takeIf { it > 0 } != null ||
        property.builtUpArea?.takeIf { it > 0 } != null ||
        property.superBuiltUpArea?.takeIf { it > 0 } != null ||
        !property.pincode.isNullOrBlank() ||
        !property.address.isNullOrBlank() ||
        property.listingCategory != ListingCategory.NORMAL ||
        !property.status.isNullOrBlank() ||
        property.rating != null ||
        !property.createdAt.isNullOrBlank()
}

private fun detailRows(property: Property): List<Pair<String, String>> = buildList {
    add(DetailStrings.LABEL_CONFIGURATION to (property.bedroomType?.label ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_BEDROOMS to (bedroomCount(property.bedroomType).takeIf { it > 0 }?.toString() ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_BATHROOMS to (property.bathrooms?.takeIf { it > 0 }?.toString() ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_FURNISHING to (property.furnishing?.label ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_FACING to (property.facing?.label ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_AGE to (property.age?.label ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_CARPET_AREA to (property.carpetArea?.takeIf { it > 0 }?.let { formatAreaValue(it) + DetailStrings.SQ_FT_SUFFIX } ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_BUILT_UP_AREA to (property.builtUpArea?.takeIf { it > 0 }?.let { formatAreaValue(it) + DetailStrings.SQ_FT_SUFFIX } ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_SUPER_BUILT_UP_AREA to (property.superBuiltUpArea?.takeIf { it > 0 }?.let { formatAreaValue(it) + DetailStrings.SQ_FT_SUFFIX } ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_PINCODE to (property.pincode?.takeIf { it.isNotBlank() } ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_ADDRESS to (property.address?.takeIf { it.isNotBlank() } ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_LISTING_CATEGORY to property.listingCategory.label)
    add(DetailStrings.LABEL_STATUS to (property.status?.takeIf { it.isNotBlank() }?.replaceFirstChar { c -> c.titlecase() } ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_RATING to (property.rating?.toString() ?: DetailStrings.VALUE_NOT_AVAILABLE))
    add(DetailStrings.LABEL_POSTED_ON to (property.createdAt?.takeIf { it.isNotBlank() }?.let { formatPostedOn(it) } ?: DetailStrings.VALUE_NOT_AVAILABLE))
}

private fun formatPostedOn(createdAt: String): String {
    return try {
        val parser = SimpleDateFormat(ISO_TIMESTAMP_PATTERN, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val formatter = SimpleDateFormat(DISPLAY_TIMESTAMP_PATTERN, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        val date = parser.parse(createdAt)
        date?.let { formatter.format(it) } ?: createdAt
    } catch (e: Exception) {
        createdAt
    }
}

// Property details table, e.g. "Configuration → 3 BHK", "Furnishing → Fully Furnished", etc.
@Composable
private fun DetailsSection(property: Property) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING)
            .padding(top = DetailDims.SECTION_SPACING),
        verticalArrangement = Arrangement.spacedBy(DetailDims.SECTION_TITLE_SPACING)
    ) {
        // Bordered card with label–value rows separated by dividers
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(DetailDims.STATS_CARD_CORNER_RADIUS),
            colors = CardDefaults.cardColors(containerColor = White),
            border = BorderStroke(DetailDims.BORDER_WIDTH, HomeSearchBarBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DetailDims.DETAIL_CARD_PADDING)
            ) {
                val rows = detailRows(property)
                rows.forEachIndexed { index, (label, value) ->
                    DetailRow(label = label, value = value)
                    if (index != rows.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(DetailDims.BORDER_WIDTH)
                                .background(HomeSearchBarBorder)
                        )
                    }
                }
            }
        }
    }
}

// Single label–value row inside the details card, e.g. "Carpet Area" → "1,200 sq ft"
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DetailDims.DETAIL_ROW_PADDING_VERTICAL),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = HomeTextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailsSectionPreview() {
    FhiontTheme {
        DetailsSection(property = PreviewData.sampleProperty)
    }
}

private fun buildPropertySummary(property: Property): String {
    val parts = listOfNotNull(
        property.residentialCommercial?.label,
        property.propertyType?.label,
        property.rentBuy?.let { DetailStrings.SUMMARY_FOR_PREFIX + it.label }
    )
    return parts.joinToString(DetailStrings.SUMMARY_SEPARATOR)
}

private fun buildShortLocation(property: Property): String {
    return listOf(property.locality, property.city)
        .filter { it.isNotBlank() }
        .joinToString(", ")
}

private fun bedroomCount(bedroomType: BedroomType?): Int = when (bedroomType) {
    BedroomType.ONE_RK, BedroomType.ONE_BHK, BedroomType.STUDIO_APARTMENT -> 1
    BedroomType.TWO_BHK -> 2
    BedroomType.THREE_BHK -> 3
    BedroomType.FOUR_BHK -> 4
    BedroomType.FIVE_BHK -> 5
    BedroomType.SIX_BHK -> 6
    BedroomType.SIX_PLUS_BHK -> 7
    null -> 0
}

private fun primaryArea(property: Property): Double? {
    return property.builtUpArea ?: property.carpetArea ?: property.superBuiltUpArea
}

private fun formatAreaValue(area: Double): String {
    return NumberFormat.getNumberInstance(Locale.forLanguageTag(DetailStrings.INDIA_LOCALE_TAG))
        .format(area.toLong())
}

private fun formatPricePerSqFt(property: Property): String? {
    val area = primaryArea(property) ?: return null
    if (area <= 0.0 || property.price <= 0.0) return null
    val raw = property.price / area
    if (raw < 1.0) return null
    val perSqFt = NumberFormat.getNumberInstance(Locale.forLanguageTag(DetailStrings.INDIA_LOCALE_TAG))
        .format(raw.roundToLong())
    return SearchStrings.RUPEE + " " + perSqFt + DetailStrings.PER_SQ_FT_SUFFIX
}

private fun readMapApiKey(context: Context): String? {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        appInfo.metaData?.getString(DetailStrings.MAPS_API_KEY_METADATA)
    } catch (e: Exception) {
        null
    }
}

private fun dialAgent(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse(DetailStrings.TEL_URI_PREFIX + phone.trim())
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

private fun messageAgent(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse(DetailStrings.SMS_URI_PREFIX + phone.trim())
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

private fun shareProperty(context: Context, property: Property) {
    val shareText = property.title + "\n" +
        formatIndianPrice(property.price, property.isRentProperty()) + "\n" +
        buildLocationString(property)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = DetailStrings.SHARE_MIME_TYPE
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, null))
}

private fun openInMaps(context: Context, property: Property) {
    val lat = property.latitude
    val lng = property.longitude
    val uri = if (lat != null && lng != null) {
        Uri.parse(
            DetailStrings.GEO_URI_PREFIX + lat + "," + lng +
                DetailStrings.GEO_QUERY_SEPARATOR + lat + "," + lng +
                "(" + Uri.encode(property.title) + ")"
        )
    } else {
        Uri.parse(
            DetailStrings.GEO_URI_PREFIX + DetailStrings.GEO_DEFAULT_COORDINATE +
                DetailStrings.GEO_QUERY_SEPARATOR + Uri.encode(buildLocationString(property))
        )
    }
    val intent = Intent(Intent.ACTION_VIEW, uri)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
private fun PropertyDetailScreenPreview() {
    FhiontTheme {
        PropertyDetailScreen(
            property = PreviewData.sampleProperty,
            onClose = {}
        )
    }
}
