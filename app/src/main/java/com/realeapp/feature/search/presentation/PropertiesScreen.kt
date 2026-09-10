package com.realeapp.feature.search.presentation

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.presentation.components.PillTabsRow
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import com.realeapp.ui.preview.PreviewData
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.MediaScrim
import com.realeapp.ui.theme.OnMediaContent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.SurfaceLight
import com.realeapp.ui.theme.White
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

private enum class SortBy(val label: String) {
    RELEVANCE(PropertiesStrings.SORT_RELEVANCE),
    PRICE_LOW_HIGH(PropertiesStrings.SORT_PRICE_LOW_HIGH),
    PRICE_HIGH_LOW(PropertiesStrings.SORT_PRICE_HIGH_LOW),
    NEWEST(PropertiesStrings.SORT_NEWEST)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    onPropertyClick: (Property) -> Unit = {},
    onChangeCity: () -> Unit = {},
    onOpenFilter: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedHomeCategory.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var sortBy by remember { mutableStateOf(SortBy.RELEVANCE) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val visibleProperties = remember(uiState.properties, sortBy, selectedCategory) {
        val filtered = uiState.properties.filter(selectedCategory::matches)
        when (sortBy) {
            SortBy.RELEVANCE -> filtered
            SortBy.PRICE_LOW_HIGH -> filtered.sortedBy { it.price }
            SortBy.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.price }
            SortBy.NEWEST -> filtered
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = White,
                    contentColor = Black
                )
            }
        }
    ) { innerPadding ->
        PropertiesScreenContent(
            properties = visibleProperties,
            isLoading = uiState.isLoading,
            isLoadingMore = uiState.isLoadingMore,
            hasReachedEnd = uiState.hasReachedEnd,
            currentFilter = uiState.currentFilter,
            sortBy = sortBy,
            onSortChange = { sortBy = it },
            selectedCategory = selectedCategory,
            onCategoryChange = viewModel::onCategorySelected,
            onRefresh = viewModel::refresh,
            onLoadMore = viewModel::onLoadMore,
            onOpenFilter = onOpenFilter,
            onLike = { propertyId -> viewModel.onLikeClicked(propertyId) },
            onPropertyClick = onPropertyClick,
            onChangeCity = onChangeCity,
            modifier = Modifier.padding(innerPadding)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertiesScreenContent(
    properties: List<Property>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasReachedEnd: Boolean,
    currentFilter: PropertyFilter?,
    sortBy: SortBy,
    onSortChange: (SortBy) -> Unit,
    selectedCategory: HomeCategory,
    onCategoryChange: (HomeCategory) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onOpenFilter: () -> Unit,
    onLike: (String) -> Unit,
    onPropertyClick: (Property) -> Unit,
    onChangeCity: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= totalItems - 2 && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = PropertiesDims.SCREEN_PADDING)
    ) {
        Spacer(modifier = Modifier.height(PropertiesDims.SCREEN_PADDING))

        PropertiesHeaderTitle(
            currentFilter = currentFilter,
            onOpenFilter = onOpenFilter
        )


        PullToRefreshBox(
            isRefreshing = isLoading && properties.isNotEmpty(),
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = PropertiesDims.RESULTS_LIST_VERTICAL_PADDING),
                verticalArrangement = Arrangement.spacedBy(PropertiesDims.RESULTS_LIST_SPACING)
            ) {
                item {
                    ResultsHeader(
                        count = properties.size,
                        sortBy = sortBy,
                        onSortChange = onSortChange
                    )
                }

                if (isLoading && properties.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Accent)
                        }
                    }
                } else if (properties.isEmpty()) {
                    item {
                        EmptyResults(
                            modifier = Modifier.fillParentMaxSize(),
                            city = currentFilter?.city,
                            onChangeCity = onChangeCity
                        )
                    }
                } else {
                    itemsIndexed(
                        items = properties,
                        key = { _, property -> property.id }
                    ) { _, property ->
                        PropertyResultCard(
                            property = property,
                            onLike = { onLike(property.documentId ?: property.id) },
                            onClick = { onPropertyClick(property) }
                        )
                    }

                    if (isLoadingMore || hasReachedEnd) {
                        item {
                            PropertiesListFooter(
                                showLoader = isLoadingMore && !hasReachedEnd,
                                showEndOfResults = hasReachedEnd && properties.isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertiesHeaderTitle(
    currentFilter: PropertyFilter?,
    onOpenFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = PropertiesDims.RESULTS_HEADER_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildHeaderTitle(currentFilter),
            color = Black,
            fontSize = PropertiesDims.HEADER_TITLE_FONT_SIZE,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onOpenFilter) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = PropertiesStrings.CD_FILTER,
                tint = Black,
                modifier = Modifier.size(PropertiesDims.SEARCH_BAR_ICON_SIZE)
            )
        }
    }
}

private fun buildHeaderTitle(filter: PropertyFilter?): String {
    val city = filter?.city
    val locality = filter?.localities?.firstOrNull()
    return when {
        !locality.isNullOrBlank() && !city.isNullOrBlank() -> {
            String.format(Locale.getDefault(), PropertiesStrings.HEADER_TITLE_FORMAT, locality, city)
        }
        !city.isNullOrBlank() -> {
            String.format(Locale.getDefault(), PropertiesStrings.HEADER_CITY_FORMAT, city)
        }
        !locality.isNullOrBlank() -> {
            String.format(Locale.getDefault(), PropertiesStrings.HEADER_CITY_FORMAT, locality)
        }
        else -> PropertiesStrings.SCREEN_TITLE
    }
}



@Composable
private fun FilterChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(PropertiesDims.FILTER_CHIP_CORNER_RADIUS),
        color = White,
        border = BorderStroke(PropertiesDims.BORDER_WIDTH, HomeSearchBarBorder)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PropertiesDims.FILTER_CHIP_ICON_TEXT_SPACING),
            modifier = Modifier.padding(
                horizontal = PropertiesDims.FILTER_CHIP_HORIZONTAL_PADDING,
                vertical = PropertiesDims.FILTER_CHIP_VERTICAL_PADDING
            )
        ) {
            Text(
                text = label,
                color = Black,
                fontSize = PropertiesDims.CHIP_FONT_SIZE,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(PropertiesDims.FILTER_CHIP_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun ResultsHeader(
    count: Int,
    sortBy: SortBy,
    onSortChange: (SortBy) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = PropertiesDims.RESULTS_HEADER_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SwapVert,
                    contentDescription = PropertiesStrings.CD_SORT,
                    tint = BrandBlue,
                    modifier = Modifier.size(PropertiesDims.SEARCH_BAR_ICON_SIZE)
                )
                Text(
                    text = "${PropertiesStrings.SORT_BY} ${sortBy.label}",
                    color = BrandBlue,
                    fontSize = PropertiesDims.SORT_FONT_SIZE,
                    fontWeight = FontWeight.Medium
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = White
            ) {
                SortBy.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.label,
                                color = if (option == sortBy) BrandBlue else Black
                            )
                        },
                        onClick = {
                            onSortChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyResultCard(
    property: Property,
    onLike: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(PropertiesDims.CARD_CORNER_RADIUS))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(PropertiesDims.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = PropertiesDims.CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(PropertiesDims.CARD_PADDING)
        ) {
            PropertyImage(
                property = property,
                modifier = Modifier.fillMaxHeight()
            )

            Spacer(modifier = Modifier.width(PropertiesDims.CARD_CONTENT_SPACING))

            PropertyInfo(
                property = property,
                onLike = onLike,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PropertyImage(
    property: Property,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(PropertiesDims.CARD_IMAGE_WIDTH)
            .clip(RoundedCornerShape(PropertiesDims.CARD_IMAGE_CORNER_RADIUS))
    ) {
        AsyncImage(
            model = property.images.firstOrNull()
                ?: "https://picsum.photos/seed/${property.id}/300/200",
            contentDescription = PropertiesStrings.CD_PROPERTY_IMAGE,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (property.images.size > 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PropertiesDims.FILTER_CHIP_ICON_TEXT_SPACING),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(PropertiesDims.FILTER_CHIP_ICON_TEXT_SPACING)
                    .clip(RoundedCornerShape(PropertiesDims.PHOTO_COUNT_CORNER_RADIUS))
                    .background(MediaScrim.copy(alpha = 0.6f))
                    .padding(
                        horizontal = PropertiesDims.PHOTO_COUNT_HORIZONTAL_PADDING,
                        vertical = PropertiesDims.PHOTO_COUNT_VERTICAL_PADDING
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoLibrary,
                    contentDescription = PropertiesStrings.CD_PHOTOS,
                    tint = OnMediaContent,
                    modifier = Modifier.size(PropertiesDims.PHOTO_COUNT_ICON_SIZE)
                )
                Text(
                    text = "${property.images.size} ${PropertiesStrings.PHOTOS_LABEL}",
                    color = OnMediaContent,
                    fontSize = PropertiesDims.PHOTO_COUNT_FONT_SIZE
                )
            }
        }
    }
}

@Composable
private fun PropertyInfo(
    property: Property,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ListingBadge(rentBuy = property.rentBuy)

            Icon(
                imageVector = if (property.isLiked == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = PropertiesStrings.CD_LIKE,
                tint = if (property.isLiked == true) BrandCoral else Gray,
                modifier = Modifier
                    .size(PropertiesDims.LIKE_ICON_SIZE)
                    .clickable(onClick = onLike)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = property.title,
            color = Black,
            fontSize = PropertiesDims.TITLE_FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PropertiesDims.SPEC_ICON_TEXT_SPACING)
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(PropertiesDims.SPEC_ICON_SIZE)
            )
            Text(
                text = "${property.locality}, ${property.city}",
                color = Gray,
                fontSize = PropertiesDims.LOCATION_FONT_SIZE,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = formatIndianPrice(property.price, property.isRentProperty()),
            color = BrandBlue,
            fontSize = PropertiesDims.PRICE_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        PropertySpecs(property = property)
    }
}

@Composable
private fun ListingBadge(rentBuy: RentBuy?) {
    val text = when (rentBuy) {
        RentBuy.RENT -> PropertiesStrings.BADGE_FOR_RENT
        else -> PropertiesStrings.BADGE_FOR_SALE
    }

    Text(
        text = text,
        color = BrandBlue,
        fontSize = PropertiesDims.BADGE_FONT_SIZE,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(PropertiesDims.BADGE_CORNER_RADIUS))
            .background(SurfaceLight)
            .padding(
                horizontal = PropertiesDims.BADGE_HORIZONTAL_PADDING,
                vertical = PropertiesDims.BADGE_VERTICAL_PADDING
            )
    )
}

@Composable
private fun PropertySpecs(
    property: Property,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PropertiesDims.SPEC_ITEM_SPACING)
    ) {
        property.bedroomType?.let {
            val bedCount = it.label.takeWhile { char -> char.isDigit() }
            val label = if (bedCount.isNotEmpty()) "$bedCount ${PropertiesStrings.BEDS_LABEL}" else it.label
            SpecItem(
                icon = Icons.Outlined.KingBed,
                text = label
            )
        }

        property.bathrooms?.takeIf { it > 0 }?.let { count ->
            SpecItem(
                icon = Icons.Filled.Bathtub,
                text = "$count ${PropertiesStrings.BATHS_LABEL}"
            )
        }

        property.carpetArea?.toInt()?.let { area ->
            SpecItem(
                icon = Icons.Outlined.SquareFoot,
                text = "${NumberFormat.getNumberInstance(Locale.getDefault()).format(area)} ${PropertiesStrings.SQFT_LABEL}"
            )
        }
    }
}

@Composable
private fun SpecItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PropertiesDims.SPEC_ICON_TEXT_SPACING)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gray,
            modifier = Modifier.size(PropertiesDims.SPEC_ICON_SIZE)
        )
        Text(
            text = text,
            color = Gray,
            fontSize = PropertiesDims.SPEC_FONT_SIZE,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun PropertiesListFooter(
    showLoader: Boolean,
    showEndOfResults: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PropertiesDims.FOOTER_PADDING),
        contentAlignment = Alignment.Center
    ) {
        when {
            showLoader -> CircularProgressIndicator(color = Accent)
            showEndOfResults -> Text(
                text = PropertiesStrings.END_OF_RESULTS,
                color = Black,
                fontSize = PropertiesDims.FOOTER_FONT_SIZE
            )
        }
    }
}

@Preview(showBackground = true, name = "Properties Light")
@Composable
private fun PropertiesScreenPreview() {
    RealeTheme(darkTheme = false) {
        PropertiesScreenContent(
            properties = PreviewData.sampleProperties,
            isLoading = false,
            isLoadingMore = false,
            hasReachedEnd = true,
            currentFilter = PropertyFilter(city = "Goa", localities = listOf("Porvorim")),
            sortBy = SortBy.RELEVANCE,
            onSortChange = {},
            selectedCategory = HomeCategory.BUY,
            onCategoryChange = {},
            onRefresh = {},
            onLoadMore = {},
            onOpenFilter = {},
            onLike = {},
            onPropertyClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Properties Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PropertiesScreenDarkPreview() {
    RealeTheme(darkTheme = true) {
        PropertiesScreenContent(
            properties = PreviewData.sampleProperties,
            isLoading = false,
            isLoadingMore = false,
            hasReachedEnd = true,
            currentFilter = PropertyFilter(city = "Goa", localities = listOf("Porvorim")),
            sortBy = SortBy.RELEVANCE,
            onSortChange = {},
            selectedCategory = HomeCategory.BUY,
            onCategoryChange = {},
            onRefresh = {},
            onLoadMore = {},
            onOpenFilter = {},
            onLike = {},
            onPropertyClick = {}
        )
    }
}
