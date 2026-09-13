package com.realeapp.feature.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.presentation.HomeCategory
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.OnMediaContent
import com.realeapp.ui.theme.White
import java.util.Locale

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
 * Capsule-style Buy/Rent toggle for the home screen.
 *
 * @param selectedCategory Currently selected home category.
 * @param onCategorySelected Callback invoked when a segment is tapped.
 * @param modifier Modifier to be applied to the toggle row.
 */
@Composable
internal fun BuyRentToggle(
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(HomeDims.TOGGLE_ROW_HEIGHT)
            .clip(CircleShape)
            .background(HomeCategoryUnselected)
            .padding(HomeDims.TOGGLE_INNER_PADDING)
    ) {
        BuyRentSegment(
            label = HomeStrings.CATEGORY_RENT,
            isSelected = selectedCategory == HomeCategory.RENT,
            onClick = { onCategorySelected(HomeCategory.RENT) },
            modifier = Modifier.weight(1f)
        )
        BuyRentSegment(
            label = HomeStrings.CATEGORY_BUY,
            isSelected = selectedCategory == HomeCategory.BUY ||
                selectedCategory == HomeCategory.NEW_PROJECTS,
            onClick = { onCategorySelected(HomeCategory.BUY) },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Individual capsule segment inside [BuyRentToggle].
 *
 * @param label Segment text.
 * @param isSelected Whether this segment is currently selected.
 * @param onClick Callback invoked when the segment is tapped.
 * @param modifier Modifier to be applied to the segment.
 */
@Composable
private fun BuyRentSegment(
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
            color = if (isSelected) OnBrandContent else HomeTextSecondary,
            fontSize = HomeDims.TOGGLE_FONT_SIZE,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
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
            selectedCategory = HomeCategory.BUY,
            onCategorySelected = {},
            modifier = Modifier.padding(HomeDims.SCREEN_PADDING)
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
 * @param modifier Modifier to be applied to the section.
 */
@Composable
internal fun FeaturedSection(
    properties: List<FeaturedProperty>,
    onSeeAllClick: () -> Unit,
    onPropertyClick: (String) -> Unit = {},
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
                    .border(BorderStroke(HomeDims.CATEGORY_BORDER_WIDTH, HomeSearchBarBorder), CircleShape)
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
            horizontalArrangement = Arrangement.spacedBy(HomeDims.FEATURED_CARD_SPACING),
            contentPadding = PaddingValues(horizontal = HomeDims.SCREEN_PADDING)
        ) {
            items(properties, key = { it.id }) { property ->
                var isLiked by remember(property.id) { mutableStateOf(property.isLiked) }
                FeaturedPropertyCard(
                    property = property,
                    isLiked = isLiked,
                    onLikeToggle = { isLiked = !isLiked },
                    onClick = { onPropertyClick(property.id) }
                )
            }
        }
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
            .width(HomeDims.FEATURED_CARD_WIDTH)
            .clip(RoundedCornerShape(HomeDims.FEATURED_CARD_CORNER_RADIUS))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HomeDims.FEATURED_CARD_IMAGE_HEIGHT)
                .clip(RoundedCornerShape(HomeDims.FEATURED_CARD_CORNER_RADIUS))
        ) {
            AsyncImage(
                model = property.imageUrl,
                contentDescription = HomeStrings.CD_PROPERTY_IMAGE,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

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
                    tint = if (isLiked) BrandRed else Black,
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
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatIndianPrice(property.price, property.isRent) +
                    HomeStrings.SEPARATOR_DOT + property.location,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Promotional banner card that shows a single [Property] ad, or a fallback
 * static banner when no promotional property is available.
 *
 * @param promotionalProperty Promotional property to display, or null for fallback.
 * @param onClick Callback invoked when the call-to-action arrow is tapped.
 * @param modifier Modifier to be applied to the banner.
 */
@Composable
internal fun PromotionBanner(
    promotionalProperty: Property?,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val title: String
    val subtitle: String
    val imageUrl: String

    if (promotionalProperty != null) {
        title = formatIndianPrice(
            promotionalProperty.price,
            promotionalProperty.rentBuy == RentBuy.RENT
        )
        subtitle = listOf(
            promotionalProperty.locality,
            promotionalProperty.city
        ).filter(String::isNotBlank).joinToString(HomeStrings.LOCATION_SEPARATOR)
        imageUrl = promotionalProperty.images.firstOrNull().orEmpty()
    } else {
        title = HomeStrings.BANNER_TITLE
        subtitle = HomeStrings.BANNER_SUBTITLE
        imageUrl = HomeStrings.BANNER_FALLBACK_IMAGE
    }

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

            if (promotionalProperty != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(HomeDims.BANNER_PADDING)
                        .clip(RoundedCornerShape(HomeDims.FEATURED_BADGE_CORNER_RADIUS))
                        .background(BrandBlue)
                        .padding(
                            horizontal = HomeDims.FEATURED_BADGE_HORIZONTAL_PADDING,
                            vertical = HomeDims.FEATURED_BADGE_VERTICAL_PADDING
                        )
                ) {
                    Text(
                        text = HomeStrings.BADGE_PROMOTIONAL,
                        color = OnMediaContent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0f to White.copy(alpha = 0.95f),
                                HomeDims.BANNER_TEXT_OVERLAY_START to White.copy(alpha = 0.75f),
                                1f to Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(HomeDims.BANNER_PADDING),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Black,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        color = HomeTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
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
