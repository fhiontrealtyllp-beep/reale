package com.realeapp.feature.home.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import com.realeapp.R
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.presentation.EmptyResults
import com.realeapp.feature.search.presentation.HomeCategory
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import com.realeapp.ui.components.BOTTOM_NAV_CLEARANCE
import com.realeapp.ui.preview.PreviewData
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.OnMediaContent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White
import java.util.Locale
import kotlin.math.abs

/**
 * UI model for a featured property displayed in the home screen carousel.
 */
internal data class FeaturedProperty(
    val id: String,
    val imageUrl: String,
    val price: Double,
    val title: String,
    val location: String,
    val beds: Int,
    val baths: Int,
    val sqft: Int,
    val isRent: Boolean = false,
    val isLiked: Boolean = false
)

/**
 * Home search bar with a search icon and placeholder text.
 *
 * @param onSearchClick Callback invoked when the search bar is tapped.
 * @param modifier Modifier to be applied to the search bar.
 */
@Composable
internal fun HomeSearchBar(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Wraps its content: the pill's size comes from the icon/text plus padding.
    Surface(
        modifier = modifier.fillMaxWidth()
            .shadow(HomeDims.SEARCH_ELEVATION, CircleShape)
            .clickable(onClick = onSearchClick),
        shape = CircleShape,
        color = White
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = HomeDims.SEARCH_HORIZONTAL_PADDING,
                vertical = HomeDims.SEARCH_VERTICAL_PADDING
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = HomeStrings.CD_SEARCH_ICON,
                tint = Black,
                modifier = Modifier.size(HomeDims.SEARCH_ICON_SIZE)
            )
            //search icon <--------------------------> start your search
            Spacer(modifier = Modifier.width(HomeDims.SEARCH_CONTENT_SPACING))
            Text(
                text = HomeStrings.SEARCH_START,
                color = Black,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Header section grouping the search bar and the buy/rent toggle with their
 * own tighter internal spacing ([HomeDims.SEARCH_TO_TOGGLE_SPACING]).
 *
 * @param onSearchClick Callback invoked when the search bar is tapped.
 * @param selectedCategory Currently selected [HomeCategory].
 * @param onCategorySelected Callback invoked when the user selects a category.
 * @param modifier Modifier to be applied to the section.
 */
@Composable
internal fun HomeHeaderSection(
    onSearchClick: () -> Unit,
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(HomeDims.SEARCH_TO_TOGGLE_SPACING)
    ) {
        HomeSearchBar(
            onSearchClick = onSearchClick,
            modifier = Modifier.padding(
                start = HomeDims.SCREEN_PADDING,
                top = HomeDims.SCREEN_TOP_PADDING,
                end = HomeDims.SCREEN_PADDING
            )
        )
        BuyRentToggle(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)
        )
    }
}

/**
 * Section that displays the list of featured properties with a circular
 * "see all" arrow action, styled like Airbnb's popular homes row.
 *
 * @param properties List of featured properties to show.
 * @param city City name used in the section title, or null for the fallback title.
 * @param onSeeAllClick Callback invoked when the arrow button is tapped.
 * @param onPropertyClick Callback invoked with the selected property ID.
 * @param onLike Callback invoked when a property's like button is tapped.
 * @param modifier Modifier to be applied to the section.
 */
@Composable
internal fun FeaturedSection(
    properties: List<FeaturedProperty>,
    onSeeAllClick: () -> Unit,
    onPropertyClick: (String) -> Unit = {},
    onLike: (FeaturedProperty) -> Unit = {},
    city: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(HomeDims.FEATURED_CARD_CONTENT_PADDING)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HomeDims.SCREEN_PADDING),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (city.isNullOrBlank()) {
                    HomeStrings.SECTION_FEATURED
                } else {
                    String.format(Locale.getDefault(), HomeStrings.SECTION_POPULAR_IN_CITY_FORMAT, city)
                },
                color = Black,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .size(HomeDims.SEE_ALL_CIRCLE_SIZE)
                    .clip(CircleShape)
                    .background(HomeCategoryUnselected)
                    .clickable(onClick = onSeeAllClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = HomeStrings.ACTION_SEE_ALL,
                    tint = Black,
                    modifier = Modifier.size(HomeDims.SEE_ALL_ICON_SIZE)
                )
            }
        }

        LazyRow(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = HomeDims.SCREEN_PADDING),
            horizontalArrangement = Arrangement.spacedBy(HomeDims.FEATURED_CARD_SPACING)
        ) {
            items(properties, key = { it.id }) { property ->
                FeaturedPropertyCard(
                    property = property,
                    isLiked = property.isLiked,
                    onLikeToggle = { onLike(property) },
                    onClick = { onPropertyClick(property.id) }
                )
            }
        }
    }
}

/**
 * Card that renders a single featured property with image, price, location and specs.
 *
 * @param property Featured property data to display.
 * @param isLiked Whether the property is currently liked.
 * @param onLikeToggle Callback invoked when the like button is tapped.
 * @param onClick Callback invoked when the card is tapped.
 */
@Composable
private fun FeaturedPropertyCard(
    property: FeaturedProperty,
    isLiked: Boolean,
    onLikeToggle: () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(HomeDims.FEATURED_CARD_SIZE)
            // Only the top corners are rounded; a bottom rounding would clip
            // the descenders of the title/location text below the image.
            .clip(
                RoundedCornerShape(
                    topStart = HomeDims.FEATURED_CARD_CORNER_RADIUS,
                    topEnd = HomeDims.FEATURED_CARD_CORNER_RADIUS
                )
            )
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HomeDims.FEATURED_CARD_SIZE)
                .clip(RoundedCornerShape(HomeDims.FEATURED_CARD_CORNER_RADIUS))
        ) {
            if (LocalInspectionMode.current) {
                Image(
                    painter = painterResource(R.drawable.ic_welcome_home),
                    contentDescription = HomeStrings.CD_PROPERTY_IMAGE,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = property.imageUrl,
                    contentDescription = HomeStrings.CD_PROPERTY_IMAGE,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            IconButton(
                onClick = onLikeToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(HomeDims.FEATURED_CARD_CONTENT_PADDING)
                    .size(HomeDims.HEART_BUTTON_SIZE)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = HomeStrings.CD_FAVORITE,
                    tint = if (isLiked) BrandRed else OnMediaContent,
                    modifier = Modifier.size(HomeDims.HEART_ICON_SIZE)
                )
            }
        }

        Column(
            modifier = Modifier.padding(top = HomeDims.FEATURED_TEXT_TOP_SPACING),
            verticalArrangement = Arrangement.spacedBy(HomeDims.FEATURED_TEXT_LINE_SPACING)
        ) {
            Text(
                text = property.title,
                color = Black,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            //price
            Text(
                text = formatIndianPrice(property.price, property.isRent),
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            //location
            if (property.location.isNotBlank()) {
                Text(
                    text = property.location,
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Page indicator dots for the promotional banner carousel.
 *
 * @param pageCount Total number of pages.
 * @param currentPage Index of the currently selected page.
 * @param modifier Modifier to be applied to the indicator row.
 */
@Composable
private fun PromotionPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    if (pageCount <= 1) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HomeDims.PROMO_INDICATOR_SPACING)
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .width(if (isSelected) HomeDims.PROMO_INDICATOR_ACTIVE_WIDTH else HomeDims.PROMO_INDICATOR_INACTIVE_WIDTH)
                    .height(HomeDims.PROMO_INDICATOR_HEIGHT)
                    .clip(CircleShape)
                    .background(if (isSelected) BrandCoral else HomeTextSecondary.copy(alpha = 0.5f))
            )
        }
    }
}

/**
 * Promotional banner that auto-slides through the given [Property] ads in a
 * peek carousel, or a fallback static banner when none are available.
 *
 * @param promotionalProperties Promotional properties to display.
 * @param onClick Callback invoked with the property whose arrow is tapped.
 * @param modifier Modifier to be applied to the banner.
 */
@Composable
internal fun PromotionBanner(
    promotionalProperties: List<Property>,
    onClick: (Property) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (promotionalProperties.isEmpty()) {
        PromotionBannerCard(
            title = HomeStrings.BANNER_TITLE,
            subtitle = HomeStrings.BANNER_SUBTITLE,
            imageUrl = HomeStrings.BANNER_FALLBACK_IMAGE,
            onClick = {},
            modifier = modifier.padding(horizontal = HomeDims.SCREEN_PADDING)
        )
        return
    }

    val pagerState = rememberPagerState(pageCount = { promotionalProperties.size })

    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(HomeDims.PROMO_AUTO_SCROLL_MS)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(
                nextPage,
                animationSpec = tween(
                    durationMillis = HomeDims.PROMO_SCROLL_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HomeDims.PROMO_INDICATOR_TOP_SPACING)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = HomeDims.PROMO_PEEK_PADDING),
            pageSpacing = HomeDims.PROMO_PAGE_SPACING,
            beyondViewportPageCount = 1
        ) { page ->
            val pageOffset = page - pagerState.currentPage - pagerState.currentPageOffsetFraction
            val distance = abs(pageOffset).coerceIn(0f, 1f)
            val scale = lerp(HomeDims.PROMO_INACTIVE_SCALE, 1f, 1f - distance)
            val alpha = lerp(HomeDims.PROMO_INACTIVE_ALPHA, 1f, 1f - distance)
            val property = promotionalProperties[page]

            PromotionBannerCard(
                title = formatIndianPrice(property.price, property.rentBuy == RentBuy.RENT),
                subtitle = listOf(property.locality, property.city)
                    .filter(String::isNotBlank)
                    .joinToString(HomeStrings.LOCATION_SEPARATOR),
                imageUrl = property.images.firstOrNull().orEmpty(),
                onClick = { onClick(property) },
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            )
        }

        if (promotionalProperties.size > 1) {
            PromotionPageIndicator(
                pageCount = promotionalProperties.size,
                currentPage = pagerState.currentPage
            )
        }
    }
}

@Composable
private fun PromotionBannerCard(
    title: String,
    subtitle: String,
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(HomeDims.BANNER_HEIGHT)
            .clip(RoundedCornerShape(HomeDims.BANNER_CORNER_RADIUS)),
        shape = RoundedCornerShape(HomeDims.BANNER_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = HomeDims.CARD_ELEVATION)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageUrl.takeIf { it.isNotBlank() } ?: HomeStrings.BANNER_FALLBACK_IMAGE,
                contentDescription = HomeStrings.CD_PROMOTIONAL_PROPERTY_IMAGE,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(HomeDims.BANNER_OVERLAY_FRACTION)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                HomeDims.BANNER_OVERLAY_TOP_STOP to Black.copy(alpha = 0f),
                                HomeDims.BANNER_OVERLAY_BOTTOM_STOP to Black.copy(alpha = HomeDims.BANNER_OVERLAY_BOTTOM_ALPHA)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(HomeDims.BANNER_PADDING),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(HomeDims.BANNER_TEXT_LINE_SPACING)
                    ) {
                        Text(
                            text = title,
                            color = White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = subtitle,
                            color = White.copy(alpha = HomeDims.BANNER_SUBTITLE_ALPHA),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = HomeDims.BANNER_CTA_START_PADDING)
                            .size(HomeDims.BANNER_ARROW_BUTTON_SIZE)
                            .clip(CircleShape)
                            .background(BrandCoral)
                            .clickable(onClick = onClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = HomeStrings.CD_BANNER_ARROW,
                            tint = OnMediaContent,
                            modifier = Modifier.size(HomeDims.BANNER_ARROW_ICON_SIZE)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Two-segment Buy / Rent toggle used on the home screen.
 *
 * @param selectedCategory Currently selected [HomeCategory].
 * @param onCategorySelected Callback invoked when the user selects a category.
 * @param modifier Modifier to be applied to the toggle.
 */
@Composable
internal fun BuyRentToggle(
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRent = selectedCategory == HomeCategory.RENT

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HomeDims.TOGGLE_ROW_HEIGHT)
            .clip(CircleShape)
            .background(HomeCategoryUnselected)
            .padding(HomeDims.TOGGLE_INNER_PADDING),
        horizontalArrangement = Arrangement.spacedBy(HomeDims.TOGGLE_INNER_PADDING)
    ) {
        BuyRentToggleSegment(
            label = HomeStrings.CATEGORY_RENT,
            isSelected = isRent,
            onClick = { onCategorySelected(HomeCategory.RENT) },
            modifier = Modifier.weight(1f)
        )
        BuyRentToggleSegment(
            label = HomeStrings.CATEGORY_BUY,
            isSelected = !isRent,
            onClick = { onCategorySelected(HomeCategory.BUY) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BuyRentToggleSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(if (isSelected) BrandBlue else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) White else HomeTextSecondary,
            fontSize = HomeDims.TOGGLE_FONT_SIZE,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

/**
 * Scrollable feed below the header: the featured-properties section followed by
 * the promotional banner, or an empty-results placeholder when both are absent.
 *
 * @param featuredProperties Featured properties to show in the carousel section.
 * @param promotionalProperties Promotional properties for the auto-sliding banner.
 * @param city City name used in the section title and empty state.
 * @param onSeeAllClick Callback invoked when the section's arrow is tapped.
 * @param onPropertyClick Callback invoked with the selected property ID.
 * @param onPromotionClick Callback invoked with the tapped promotional property.
 * @param onChangeCity Callback invoked from the empty state to change city.
 * @param onLike Callback invoked when a featured property's like button is tapped.
 * @param modifier Modifier to be applied to the feed container.
 */
@Composable
internal fun HomePropertyFeed(
    featuredProperties: List<FeaturedProperty>,
    promotionalProperties: List<Property>,
    city: String?,
    onSeeAllClick: () -> Unit,
    onPropertyClick: (String) -> Unit,
    onPromotionClick: (Property) -> Unit,
    onChangeCity: () -> Unit,
    onLike: (FeaturedProperty) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (featuredProperties.isEmpty() && promotionalProperties.isEmpty()) {
            EmptyResults(
                modifier = Modifier.fillMaxSize(),
                title = HomeStrings.NO_PROPERTIES_TITLE,
                subtitle = HomeStrings.NO_PROPERTIES_SUBTITLE,
                city = city,
                onChangeCity = onChangeCity
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                // Extra bottom space so the last item clears the floating glass nav capsule.
                // No top padding: the gap after the header comes from the
                // parent Column's SECTION_SPACING, same as every other component.
                contentPadding = PaddingValues(
                    bottom = HomeDims.SCREEN_PADDING + BOTTOM_NAV_CLEARANCE
                ),
                verticalArrangement = Arrangement.spacedBy(HomeDims.SECTION_SPACING)
            ) {
                if (featuredProperties.isNotEmpty()) {
                    item {
                        FeaturedSection(
                            properties = featuredProperties,
                            city = city,
                            onSeeAllClick = onSeeAllClick,
                            onPropertyClick = onPropertyClick,
                            onLike = onLike
                        )
                    }
                }

                if (promotionalProperties.isNotEmpty()) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(HomeDims.SECTION_SPACING)
                        ) {
                            Text(
                                text = HomeStrings.PROMOTIONAL_TITLE,
                                color = Black,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)
                            )
                            PromotionBanner(
                                promotionalProperties = promotionalProperties,
                                onClick = onPromotionClick
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full-screen dialog that shows [PropertyDetailScreen] for the selected property.
 *
 * @param property Property to display.
 * @param onClose Callback invoked when the dialog is dismissed.
 * @param onLike Callback invoked when the property's like button is tapped.
 */
@Composable
internal fun PropertyDetailDialog(
    property: Property,
    onClose: () -> Unit,
    onLike: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppBackground
        ) {
            PropertyDetailScreen(
                property = property,
                onClose = onClose,
                onLike = onLike,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Preview for [HomeSearchBar].
 */
@Preview(showBackground = true)
@Composable
private fun HomeSearchBarPreview() {
    RealeTheme {
        HomeSearchBar(
            onSearchClick = {},
            modifier = Modifier.padding(HomeDims.SCREEN_PADDING)
        )
    }
}

/**
 * Preview for [HomeHeaderSection].
 */
@Preview(showBackground = true)
@Composable
private fun HomeHeaderSectionPreview() {
    RealeTheme {
        HomeHeaderSection(
            onSearchClick = {},
            selectedCategory = HomeCategory.RENT,
            onCategorySelected = {}
        )
    }
}

/**
 * Preview for [FeaturedSection].
 */
@Preview(showBackground = true)
@Composable
private fun FeaturedSectionPreview() {
    RealeTheme {
        FeaturedSection(
            properties = listOf(
                FeaturedProperty(
                    id = "1",
                    imageUrl = "https://picsum.photos/seed/home1/800/600",
                    price = 14_265.0,
                    title = "Flat in Candolim",
                    location = "Candolim, Goa",
                    beds = 2,
                    baths = 2,
                    sqft = 1100,
                    isRent = true
                ),
                FeaturedProperty(
                    id = "2",
                    imageUrl = "https://picsum.photos/seed/home2/800/600",
                    price = 7_300.0,
                    title = "Flat in Candolim",
                    location = "Candolim, Goa",
                    beds = 1,
                    baths = 1,
                    sqft = 750,
                    isRent = true
                ),
                FeaturedProperty(
                    id = "3",
                    imageUrl = "https://picsum.photos/seed/home3/800/600",
                    price = 4_500_000.0,
                    title = "Villa in Assagao",
                    location = "Assagao, Goa",
                    beds = 3,
                    baths = 3,
                    sqft = 2100
                )
            ),
            city = "North Goa",
            onSeeAllClick = {}
        )
    }
}

/**
 * Preview for [FeaturedPropertyCard].
 */
@Preview(showBackground = true)
@Composable
private fun FeaturedPropertyCardPreview() {
    RealeTheme {
        FeaturedPropertyCard(
            property = FeaturedProperty(
                id = "1",
                imageUrl = "https://picsum.photos/seed/home1/800/600",
                price = 14_265.0,
                title = "Flat in Candolim",
                location = "Candolim, Goa",
                beds = 2,
                baths = 2,
                sqft = 1100,
                isRent = true
            ),
            isLiked = false,
            onLikeToggle = {},
            onClick = {}
        )
    }
}

/**
 * Preview for [PromotionBanner].
 */
@Preview(showBackground = true)
@Composable
private fun PromotionBannerPreview() {
    RealeTheme {
        PromotionBanner(
            promotionalProperties = PreviewData.sampleProperties.take(2),
            onClick = {}
        )
    }
}

/**
 * Preview for [BuyRentToggle].
 */
@Preview(showBackground = true)
@Composable
private fun BuyRentTogglePreview() {
    RealeTheme {
        BuyRentToggle(
            selectedCategory = HomeCategory.RENT,
            onCategorySelected = {},
            modifier = Modifier.padding(HomeDims.SCREEN_PADDING)
        )
    }
}

/**
 * Preview for [HomePropertyFeed].
 */
@Preview(showBackground = true)
@Composable
private fun HomePropertyFeedPreview() {
    RealeTheme {
        HomePropertyFeed(
            featuredProperties = listOf(
                FeaturedProperty(
                    id = "1",
                    imageUrl = "https://picsum.photos/seed/home1/800/600",
                    price = 14_265.0,
                    title = "Flat in Candolim",
                    location = "Candolim, Goa",
                    beds = 2,
                    baths = 2,
                    sqft = 1100,
                    isRent = true
                ),
                FeaturedProperty(
                    id = "2",
                    imageUrl = "https://picsum.photos/seed/home2/800/600",
                    price = 4_500_000.0,
                    title = "Villa in Assagao",
                    location = "Assagao, Goa",
                    beds = 3,
                    baths = 3,
                    sqft = 2100
                )
            ),
            promotionalProperties = PreviewData.sampleProperties.take(2),
            city = "North Goa",
            onSeeAllClick = {},
            onPropertyClick = {},
            onPromotionClick = {},
            onChangeCity = {}
        )
    }
}

/**
 * Preview for [PropertyDetailDialog]'s content (dialogs don't render in previews).
 */
@Preview(showBackground = true)
@Composable
private fun PropertyDetailDialogPreview() {
    RealeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppBackground
        ) {
            PropertyDetailScreen(
                property = PreviewData.sampleProperties.first(),
                onClose = {},
                onLike = {}
            )
        }
    }
}
