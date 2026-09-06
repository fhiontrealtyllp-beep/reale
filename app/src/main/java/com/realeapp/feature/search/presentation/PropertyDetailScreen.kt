package com.realeapp.feature.search.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled._360
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.NearbyPlace
import com.realeapp.feature.search.domain.model.NearbyPlaceType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.MediaScrim
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.OnMediaContent
import com.realeapp.ui.theme.White
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale


private const val MIN_ZOOM = 1f
private const val MAX_ZOOM = 5f
private const val DOUBLE_TAP_ZOOM = 2.5f
private const val MAP_ZOOM_LEVEL = 15f
private const val MAX_VISIBLE_THUMBS = 5
private const val MAX_STATS = 5
private const val DESCRIPTION_COLLAPSED_LINES = 3
private const val ITEMS_BEFORE_SECTIONS = 4
private const val HIGHLIGHTS_PER_ROW = 2


@Composable
fun PropertyDetailScreen(
    property: Property,
    onClose: () -> Unit,
    onLike: (() -> Unit)? = null,
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

    val hasOverview = property.description.isNotBlank()
    val hasHighlights = property.amenities.isNotEmpty()
    val hasLocation = (property.latitude != null && property.longitude != null) ||
        !property.address.isNullOrBlank() || property.city.isNotBlank()
    val locationItemIndex = ITEMS_BEFORE_SECTIONS +
        (if (hasOverview) 1 else 0) +
        (if (hasHighlights) 1 else 0)

    Scaffold(
        modifier = modifier,
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            DetailBottomBar(
                phone = property.agentPhone,
                onCall = { dialAgent(context, property.agentPhone) },
                onEnquire = { messageAgent(context, property.agentPhone) }
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
                InfoSection(property = property)
            }

            item {
                // Quick stats UI: beds, area, type, facing, furnishing.
                StatsCard(property = property)
            }

            if (hasOverview) {
                item {
                    OverviewSection(description = property.description)
                }
            }

            if (hasHighlights) {
                item {
                    HighlightsSection(amenities = property.amenities)
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
            AsyncImage(
                model = images[selectedImage.coerceIn(0, images.lastIndex)],
                contentDescription = property.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onImageClick)
            )

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
                        HeroCircleButton(
                            icon = if (property.isLiked == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = DetailStrings.CD_LIKE,
                            tint = if (property.isLiked == true) BrandRed else Black,
                            onClick = onLike
                        )
                    }
                    HeroCircleButton(
                        icon = Icons.Filled.Share,
                        contentDescription = DetailStrings.CD_SHARE,
                        onClick = onShare
                    )
                }
            }

            // Photo-count badge UI opening the full-screen gallery.
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = DetailDims.HERO_OVERLAY_PADDING,
                        bottom = DetailDims.HERO_BOTTOM_CONTENT_PADDING
                    )
                    .clip(RoundedCornerShape(DetailDims.BADGE_CORNER_RADIUS))
                    .background(MediaScrim.copy(alpha = DetailDims.OVERLAY_SCRIM_ALPHA))
                    .clickable(onClick = onShowPhotos)
                    .padding(
                        horizontal = DetailDims.BADGE_HORIZONTAL_PADDING,
                        vertical = DetailDims.BADGE_VERTICAL_PADDING
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DetailDims.BADGE_CONTENT_SPACING)
            ) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = null,
                    tint = OnMediaContent,
                    modifier = Modifier.size(DetailDims.BADGE_ICON_SIZE)
                )
                Text(
                    text = "${images.size}${DetailStrings.PHOTOS_COUNT_SUFFIX}",
                    color = OnMediaContent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            // Media switcher UI: photos, map scroll, and a 360 placeholder.
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = DetailDims.HERO_OVERLAY_PADDING,
                        bottom = DetailDims.HERO_BOTTOM_CONTENT_PADDING
                    ),
                horizontalArrangement = Arrangement.spacedBy(DetailDims.MEDIA_PILL_SPACING)
            ) {
                MediaPill(
                    icon = Icons.Filled.Image,
                    label = DetailStrings.MEDIA_PHOTOS,
                    contentDescription = DetailStrings.CD_PHOTOS,
                    selected = true,
                    onClick = onShowPhotos
                )
                MediaPill(
                    icon = Icons.Filled.Map,
                    label = DetailStrings.MEDIA_MAP,
                    contentDescription = DetailStrings.CD_VIEW_MAP,
                    selected = false,
                    onClick = onShowMap
                )
                MediaPill(
                    icon = Icons.Filled._360,
                    label = DetailStrings.MEDIA_360,
                    contentDescription = DetailStrings.CD_VIRTUAL_TOUR,
                    selected = false,
                    onClick = null
                )
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
private fun HeroCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = Black
) {
    Box(
        modifier = Modifier
            .size(DetailDims.HERO_BUTTON_SIZE)
            .clip(CircleShape)
            .background(White)
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
    val backgroundColor = if (selected) BrandBlue else White
    val contentColor = if (selected) OnBrandContent else Black
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
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = DetailDims.THUMB_CARD_ELEVATION)
    ) {
        LazyRow(
            modifier = Modifier.padding(DetailDims.THUMB_CARD_PADDING),
            horizontalArrangement = Arrangement.spacedBy(DetailDims.THUMB_SPACING)
        ) {
            itemsIndexed(images.take(MAX_VISIBLE_THUMBS)) { index, url ->
                val isOverflowTile = index == MAX_VISIBLE_THUMBS - 1 && images.size > MAX_VISIBLE_THUMBS
                Box(
                    modifier = Modifier
                        .size(DetailDims.THUMB_SIZE)
                        .clip(RoundedCornerShape(DetailDims.THUMB_CORNER_RADIUS))
                        .background(HomeCategoryUnselected)
                        .then(
                            if (index == selectedImage) {
                                Modifier.border(
                                    width = DetailDims.THUMB_SELECTED_BORDER,
                                    color = BrandBlue,
                                    shape = RoundedCornerShape(DetailDims.THUMB_CORNER_RADIUS)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onSelect(index) }
                ) {
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
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
private fun InfoSection(property: Property) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING)
            .padding(top = DetailDims.SCREEN_PADDING),
        verticalArrangement = Arrangement.spacedBy(DetailDims.CONTENT_SPACING)
    ) {
        property.rentBuy?.let { rentBuy ->
            Text(
                text = if (rentBuy == RentBuy.RENT) DetailStrings.BADGE_FOR_RENT else DetailStrings.BADGE_FOR_SALE,
                color = BrandBlue,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(DetailDims.SALE_BADGE_CORNER_RADIUS))
                    .background(BrandBlue.copy(alpha = DetailDims.ACCENT_BACKGROUND_ALPHA))
                    .padding(
                        horizontal = DetailDims.SALE_BADGE_HORIZONTAL_PADDING,
                        vertical = DetailDims.SALE_BADGE_VERTICAL_PADDING
                    )
            )
        }

        Text(
            text = property.title,
            color = Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

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
                fontSize = 14.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatIndianPrice(property.price, property.isRentProperty()),
                    color = BrandBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                formatPricePerSqFt(property)?.let { perSqFt ->
                    Text(
                        text = perSqFt,
                        color = HomeTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            if (property.rentBuy == RentBuy.BUY) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(DetailDims.LOAN_PILL_CORNER_RADIUS))
                        .background(BrandBlue.copy(alpha = DetailDims.ACCENT_BACKGROUND_ALPHA))
                        .padding(
                            horizontal = DetailDims.LOAN_PILL_HORIZONTAL_PADDING,
                            vertical = DetailDims.LOAN_PILL_VERTICAL_PADDING
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DetailDims.LOAN_PILL_CONTENT_SPACING)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalance,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(DetailDims.LOAN_ICON_SIZE)
                    )
                    Text(
                        text = DetailStrings.ACTION_GET_HOME_LOAN,
                        color = BrandBlue,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(DetailDims.LOAN_CHEVRON_SIZE)
                    )
                }
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING)
            .padding(top = DetailDims.SECTION_SPACING),
        shape = RoundedCornerShape(DetailDims.STATS_CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(DetailDims.BORDER_WIDTH, HomeSearchBarBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = DetailDims.STATS_CARD_VERTICAL_PADDING),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            stats.forEach { stat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(DetailDims.STAT_ITEM_SPACING)
                ) {
                    Icon(
                        imageVector = stat.icon,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(DetailDims.STAT_ICON_SIZE)
                    )
                    Text(
                        text = stat.value,
                        color = Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stat.label,
                        color = HomeTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Black,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun OverviewSection(description: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING)
            .padding(top = DetailDims.SECTION_SPACING),
        verticalArrangement = Arrangement.spacedBy(DetailDims.SECTION_TITLE_SPACING)
    ) {
        SectionTitle(text = DetailStrings.SECTION_OVERVIEW)
        Text(
            text = description,
            color = HomeTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            maxLines = if (expanded) Int.MAX_VALUE else DESCRIPTION_COLLAPSED_LINES,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { expanded = !expanded }
        ) {
            Text(
                text = if (expanded) DetailStrings.ACTION_READ_LESS else DetailStrings.ACTION_READ_MORE,
                color = BrandBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(DetailDims.READ_MORE_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun HighlightsSection(amenities: List<Amenity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING)
            .padding(top = DetailDims.SECTION_SPACING),
        verticalArrangement = Arrangement.spacedBy(DetailDims.SECTION_TITLE_SPACING)
    ) {
        SectionTitle(text = DetailStrings.SECTION_HIGHLIGHTS)
        Column(verticalArrangement = Arrangement.spacedBy(DetailDims.HIGHLIGHT_ROW_SPACING)) {
            amenities.chunked(HIGHLIGHTS_PER_ROW).forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { amenity ->
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(DetailDims.HIGHLIGHT_ITEM_SPACING)
                        ) {
                            Icon(
                                imageVector = amenityDisplayIcon(amenity),
                                contentDescription = null,
                                tint = BrandBlue,
                                modifier = Modifier.size(DetailDims.HIGHLIGHT_ICON_SIZE)
                            )
                            Text(
                                text = amenityDisplayLabel(amenity),
                                color = Black,
                                fontSize = DetailDims.HIGHLIGHT_LABEL_FONT_SIZE
                            )
                        }
                    }
                    repeat(HIGHLIGHTS_PER_ROW - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
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

@Composable
private fun LocationSection(property: Property) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DetailDims.SCREEN_PADDING)
            .padding(top = DetailDims.SECTION_SPACING),
        verticalArrangement = Arrangement.spacedBy(DetailDims.SECTION_TITLE_SPACING)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(text = DetailStrings.SECTION_LOCATION)
            Text(
                text = DetailStrings.ACTION_VIEW_ON_MAP,
                color = BrandBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { openInMaps(context, property) }
            )
        }
        LocationContent(property = property)
    }
}

@Composable
private fun LocationContent(property: Property) {
    val context = LocalContext.current
    val apiKey = remember { readMapApiKey(context) }
    val lat = property.latitude
    val lng = property.longitude
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DetailDims.LOCATION_CONTENT_SPACING)
    ) {
        if (lat != null && lng != null && !apiKey.isNullOrBlank() && apiKey != DetailStrings.MAPS_KEY_PLACEHOLDER) {
            val propertyLatLng = LatLng(lat, lng)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(propertyLatLng, MAP_ZOOM_LEVEL)
            }
            var isMapLoaded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .weight(1f)
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
                        color = BrandBlue
                    )
                }
            }
        } else {
            // Address-only location UI used when an interactive map cannot be displayed.
            Row(
                modifier = Modifier
                    .weight(1f)
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
                    tint = BrandBlue,
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

        NearbyPlacesCard(
            nearbyPlaces = property.nearbyPlaces,
            modifier = Modifier.width(DetailDims.NEARBY_CARD_WIDTH)
        )
    }
}

@Composable
private fun NearbyPlacesCard(
    nearbyPlaces: List<NearbyPlace>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(DetailDims.MAP_HEIGHT),
        shape = RoundedCornerShape(DetailDims.LOCATION_CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(DetailDims.BORDER_WIDTH, HomeSearchBarBorder)
    ) {
        Column(
            modifier = Modifier.padding(DetailDims.NEARBY_CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(DetailDims.NEARBY_ITEM_SPACING)
        ) {
            Text(
                text = DetailStrings.SECTION_NEARBY_PLACES,
                color = Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            if (nearbyPlaces.isEmpty()) {
                Text(
                    text = DetailStrings.NO_NEARBY_PLACES,
                    color = HomeTextSecondary,
                    fontSize = 13.sp
                )
            } else {
                nearbyPlaces.forEach { place ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DetailDims.NEARBY_ITEM_SPACING)
                    ) {
                        Icon(
                            imageVector = nearbyPlaceIcon(place.type),
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(DetailDims.NEARBY_ITEM_ICON_SIZE)
                        )
                        Text(
                            text = place.name,
                            color = Black,
                            fontSize = DetailDims.NEARBY_ITEM_NAME_FONT_SIZE,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = String.format(
                                Locale.ROOT,
                                DetailStrings.NEARBY_DISTANCE_FORMAT,
                                place.distanceKm
                            ),
                            color = HomeTextSecondary,
                            fontSize = DetailDims.NEARBY_ITEM_DISTANCE_FONT_SIZE
                        )
                    }
                }
            }
        }
    }
}

private fun nearbyPlaceIcon(type: NearbyPlaceType): ImageVector = when (type) {
    NearbyPlaceType.SCHOOL -> Icons.Filled.School
    NearbyPlaceType.SHOPPING -> Icons.Filled.ShoppingCart
    NearbyPlaceType.HOSPITAL -> Icons.Filled.LocalHospital
    NearbyPlaceType.OTHER -> Icons.Filled.LocationOn
}

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
            OutlinedButton(
                onClick = onCall,
                enabled = phone.isNotBlank(),
                modifier = Modifier
                    .weight(1f)
                    .height(DetailDims.BOTTOM_BUTTON_HEIGHT),
                shape = RoundedCornerShape(DetailDims.BOTTOM_BUTTON_CORNER_RADIUS),
                border = BorderStroke(DetailDims.BORDER_WIDTH, BrandBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue)
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
            Button(
                onClick = onEnquire,
                enabled = phone.isNotBlank(),
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
    val perSqFt = NumberFormat.getNumberInstance(Locale.forLanguageTag(DetailStrings.INDIA_LOCALE_TAG))
        .format((property.price / area).toLong())
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
