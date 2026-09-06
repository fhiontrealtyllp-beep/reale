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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.HomeCategoryIconUnselected
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.White
import java.text.NumberFormat
import java.util.Locale

internal data class FeaturedProperty(
    val id: String,
    val imageUrl: String,
    val price: Double,
    val title: String,
    val location: String,
    val beds: Int,
    val baths: Int,
    val sqft: Int,
    val isLiked: Boolean = false
)

@Composable
internal fun HomeHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HomeDims.LOGO_ICON_TEXT_SPACING)
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = HomeStrings.CD_LOGO,
                tint = BrandRed,
                modifier = Modifier.size(HomeDims.LOGO_ICON_SIZE)
            )
            Text(
                text = HomeStrings.APP_NAME,
                color = BrandBlue,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = HomeStrings.CD_NOTIFICATIONS,
                tint = Black,
                modifier = Modifier.size(HomeDims.LOGO_ICON_SIZE)
            )
            Box(
                modifier = Modifier
                    .size(HomeDims.NOTIFICATION_BADGE_SIZE)
                    .clip(CircleShape)
                    .background(BrandRed)
            )
        }
    }
}

@Composable
internal fun HomeTitle(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = HomeStrings.HOME_TITLE_LINE1,
            color = Black,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(HomeDims.TITLE_LINE_SPACING))
        Text(
            text = HomeStrings.HOME_TITLE_LINE2,
            color = Black,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun HomeSearchBar(onSearchClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(HomeDims.SEARCH_HEIGHT)
            .clip(RoundedCornerShape(HomeDims.SEARCH_CORNER_RADIUS))
            .clickable(onClick = onSearchClick),
        shape = RoundedCornerShape(HomeDims.SEARCH_CORNER_RADIUS),
        color = White,
        border = BorderStroke(HomeDims.CATEGORY_BORDER_WIDTH, HomeSearchBarBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = HomeDims.SEARCH_HORIZONTAL_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = HomeStrings.CD_SEARCH_ICON,
                tint = HomeTextSecondary,
                modifier = Modifier.size(HomeDims.SEARCH_ICON_SIZE)
            )
            Spacer(modifier = Modifier.width(HomeDims.SEARCH_CONTENT_SPACING))
            Text(
                text = HomeStrings.SEARCH_HINT,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = HomeStrings.CD_FILTER_ICON,
                tint = BrandBlue,
                modifier = Modifier.size(HomeDims.SEARCH_ICON_SIZE)
            )
        }
    }
}

private data class HomeCategory(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
internal fun CategoryChips(modifier: Modifier = Modifier) {
    val categories = remember {
        listOf(
            HomeCategory(HomeStrings.CATEGORY_BUY, Icons.Filled.Home, Icons.Outlined.Home),
            HomeCategory(HomeStrings.CATEGORY_RENT, Icons.Filled.Home, Icons.Outlined.Home),
            HomeCategory(HomeStrings.CATEGORY_NEW_PROJECTS, Icons.Filled.Apartment, Icons.Outlined.Apartment),
            HomeCategory(HomeStrings.CATEGORY_COMMERCIAL, Icons.Filled.Business, Icons.Outlined.Business)
        )
    }
    var selectedIndex by remember { mutableIntStateOf(0) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        categories.forEachIndexed { index, category ->
            CategoryItem(
                category = category,
                selected = index == selectedIndex,
                onClick = { selectedIndex = index }
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: HomeCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    val icon = if (selected) category.selectedIcon else category.unselectedIcon
    val background = if (selected) BrandBlue else HomeCategoryUnselected
    val iconTint = if (selected) White else HomeCategoryIconUnselected
    val textTint = if (selected) BrandBlue else Black
    val border = if (selected) null else BorderStroke(HomeDims.CATEGORY_BORDER_WIDTH, HomeSearchBarBorder)

    Column(
        modifier = Modifier.width(HomeDims.CATEGORY_ITEM_WIDTH),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(HomeDims.CATEGORY_CIRCLE_SIZE)
                .clip(CircleShape)
                .background(background)
                .then(if (border != null) Modifier.border(border, CircleShape) else Modifier)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.label,
                tint = iconTint,
                modifier = Modifier.size(HomeDims.CATEGORY_ICON_SIZE)
            )
        }
        Spacer(modifier = Modifier.height(HomeDims.CATEGORY_LABEL_SPACING))
        Text(
            text = category.label,
            color = textTint,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
internal fun FeaturedSection(
    properties: List<FeaturedProperty>,
    onSeeAllClick: () -> Unit,
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
                text = HomeStrings.SECTION_FEATURED,
                color = Black,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = HomeStrings.ACTION_SEE_ALL,
                color = BrandBlue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
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
                    onLikeToggle = { isLiked = !isLiked }
                )
            }
        }
    }
}

@Composable
private fun FeaturedPropertyCard(
    property: FeaturedProperty,
    isLiked: Boolean,
    onLikeToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(HomeDims.FEATURED_CARD_WIDTH)
            .clip(RoundedCornerShape(HomeDims.FEATURED_CARD_CORNER_RADIUS)),
        shape = RoundedCornerShape(HomeDims.FEATURED_CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = HomeDims.FEATURED_CARD_ELEVATION)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HomeDims.FEATURED_CARD_IMAGE_HEIGHT)
            ) {
                AsyncImage(
                    model = property.imageUrl,
                    contentDescription = HomeStrings.CD_PROPERTY_IMAGE,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = HomeStrings.BADGE_FEATURED,
                    color = White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(HomeDims.FEATURED_CARD_CONTENT_PADDING)
                        .clip(RoundedCornerShape(HomeDims.FEATURED_BADGE_CORNER_RADIUS))
                        .background(BrandBlue)
                        .padding(
                            horizontal = HomeDims.FEATURED_BADGE_HORIZONTAL_PADDING,
                            vertical = HomeDims.FEATURED_BADGE_VERTICAL_PADDING
                        )
                )

                IconButton(
                    onClick = onLikeToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(HomeDims.FEATURED_CARD_CONTENT_PADDING)
                        .size(HomeDims.HEART_BUTTON_SIZE)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.9f))
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
                modifier = Modifier.padding(HomeDims.FEATURED_CARD_CONTENT_PADDING),
                verticalArrangement = Arrangement.spacedBy(HomeDims.FEATURED_DETAILS_SPACING)
            ) {
                Text(
                    text = formatIndianPrice(property.price),
                    color = Black,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = property.title,
                    color = Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HomeDims.FEATURED_SPEC_LABEL_SPACING)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = HomeTextSecondary,
                        modifier = Modifier.size(HomeDims.SMALL_ICON_SIZE)
                    )
                    Text(
                        text = property.location,
                        color = HomeTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HomeDims.FEATURED_SPEC_SPACING)
                ) {
                    PropertySpecChip(
                        icon = Icons.Outlined.KingBed,
                        label = "${property.beds} ${HomeStrings.SPEC_BEDS_LABEL}"
                    )
                    PropertySpecChip(
                        icon = Icons.Filled.Bathtub,
                        label = "${property.baths} ${HomeStrings.SPEC_BATHS_LABEL}"
                    )
                    PropertySpecChip(
                        icon = Icons.Outlined.SquareFoot,
                        label = "${NumberFormat.getNumberInstance(Locale.getDefault()).format(property.sqft)} ${HomeStrings.SPEC_SQFT_LABEL}"
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertySpecChip(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HomeDims.FEATURED_SPEC_LABEL_SPACING)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HomeTextSecondary,
            modifier = Modifier.size(HomeDims.TINY_ICON_SIZE)
        )
        Text(
            text = label,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
internal fun PromotionBanner(modifier: Modifier = Modifier) {
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
                model = "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?w=800&q=80",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

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
                        text = HomeStrings.BANNER_TITLE,
                        color = Black,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = HomeStrings.BANNER_SUBTITLE,
                        color = HomeTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Box(
                    modifier = Modifier
                        .size(HomeDims.BANNER_ARROW_BUTTON_SIZE)
                        .clip(CircleShape)
                        .background(BrandCoral)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = HomeStrings.CD_BANNER_ARROW,
                        tint = White,
                        modifier = Modifier.size(HomeDims.BANNER_ARROW_ICON_SIZE)
                    )
                }
            }
        }
    }
}
