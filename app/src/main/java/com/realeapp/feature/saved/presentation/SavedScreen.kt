package com.realeapp.feature.saved.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import com.realeapp.ui.components.LoginPrompt
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.White
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

private val indianNumberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))

private enum class SavedFilter(val label: String) {
    ALL(SavedStrings.FILTER_ALL),
    HOMES(SavedStrings.FILTER_HOMES),
    PLOTS(SavedStrings.FILTER_PLOTS),
    PROJECTS(SavedStrings.FILTER_PROJECTS);

    fun count(properties: List<Property>): Int = properties.count { matches(it) }

    fun matches(property: Property): Boolean = when (this) {
        ALL -> true
        HOMES -> property.rentBuy != null && property.propertyType in setOf(
            PropertyType.APARTMENT,
            PropertyType.VILLA,
            PropertyType.INDEPENDENT_HOUSE
        )
        PLOTS -> property.rentBuy != null && property.propertyType in setOf(
            PropertyType.PLOT,
            PropertyType.LAND
        )
        PROJECTS -> property.rentBuy == null
    }
}

private fun Property.isProject(): Boolean = rentBuy == null

private fun Property.statusLabel(): String = when {
    isProject() -> SavedStrings.PROJECT_LABEL
    isRentProperty() -> SavedStrings.LISTING_FOR_RENT
    else -> SavedStrings.LISTING_FOR_SALE
}

private fun Property.statusColor(): Color = when {
    isProject() -> BrandBlue
    isRentProperty() -> BrandCoral
    else -> BrandBlue
}

private fun Property.statusBackground(): Color = statusColor().copy(alpha = 0.08f)

private fun BedroomType.bedCount(): Int = when (this) {
    BedroomType.ONE_RK -> 1
    BedroomType.ONE_BHK -> 1
    BedroomType.TWO_BHK -> 2
    BedroomType.THREE_BHK -> 3
    BedroomType.FOUR_BHK -> 4
    BedroomType.FIVE_BHK -> 5
    BedroomType.SIX_BHK -> 6
    BedroomType.SIX_PLUS_BHK -> 7
    BedroomType.STUDIO_APARTMENT -> 1
}

private fun Property.bedCount(): Int = bedroomType?.bedCount() ?: 0

private fun Property.specs(): List<Pair<ImageVector, String>> = buildList {
    val beds = bedCount()
    if (beds > 0) add(Icons.Outlined.KingBed to "$beds ${SavedStrings.SPEC_BEDS_LABEL}")
    carpetArea?.let {
        add(
            Icons.Outlined.SquareFoot to "${indianNumberFormat.format(it.toInt())} ${SavedStrings.SPEC_SQFT_LABEL}"
        )
    }
    furnishing?.let { add(Icons.Outlined.Chair to it.label) }
}

private fun Property.photoCountLabel(): String =
    "${images.size}${SavedStrings.PHOTOS_SUFFIX}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SavedFilter.ALL) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = White,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CardBackground,
                    contentColor = TextPrimary
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                // Full-screen loading UI while the initial saved properties are fetched.
                uiState.isLoading && uiState.properties.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BrandBlue
                    )
                }

                // Logged-out UI prompting the user to open the login flow.
                !uiState.isLoggedIn -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MainBackground)
                ) {
                    LoginPrompt(
                        title = SavedStrings.LOGIN_PROMPT_TITLE,
                        buttonText = SavedStrings.LOGIN_BUTTON,
                        onLoginClick = onLoginClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Error UI with an action to retry loading saved properties.
                uiState.errorMessage != null -> ErrorContent(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = viewModel::refresh,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Logged-in UI listing the user's saved properties.
                else -> SavedPropertyList(
                    properties = uiState.properties,
                    isLoading = uiState.isLoading,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    onRefresh = viewModel::refresh,
                    onPropertyClick = { selectedProperty = it },
                    onLike = viewModel::onLikeClicked,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Full-screen property details UI shown after selecting a saved property.
    selectedProperty?.let { selected ->
        val property = uiState.properties.find {
            it.documentId == selected.documentId || it.id == selected.id
        } ?: selected.copy(isLiked = true)
        Dialog(
            onDismissRequest = { selectedProperty = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MainBackground
            ) {
                PropertyDetailScreen(
                    property = property,
                    onClose = { selectedProperty = null },
                    onLike = { viewModel.onLikeClicked(property.documentId ?: property.id) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedPropertyList(
    properties: List<Property>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: SavedFilter,
    onFilterSelected: (SavedFilter) -> Unit,
    onRefresh: () -> Unit,
    onPropertyClick: (Property) -> Unit,
    onLike: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val counts = remember(properties) {
        SavedFilter.entries.associateWith { it.count(properties) }
    }

    val filteredProperties = remember(properties, searchQuery, selectedFilter) {
        val query = searchQuery.trim().lowercase()
        properties.filter { property ->
            selectedFilter.matches(property) &&
                (query.isEmpty() ||
                    property.title.lowercase().contains(query) ||
                    property.locality.lowercase().contains(query) ||
                    property.city.lowercase().contains(query))
        }
    }

    PullToRefreshBox(
        isRefreshing = isLoading && properties.isNotEmpty(),
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = SavedDims.SCREEN_PADDING,
                vertical = SavedDims.SCREEN_PADDING
            ),
            verticalArrangement = Arrangement.spacedBy(SavedDims.ITEM_SPACING)
        ) {
            item { SavedTopBar() }
            item { SavedTitle() }
            item {
                FilterChipRow(
                    counts = counts,
                    selected = selectedFilter,
                    onSelect = onFilterSelected
                )
            }
            item {
                SavedSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchChange
                )
            }
            item { CollectionCard() }
            if (filteredProperties.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptySavedResults()
                    }
                }
            } else {
                items(
                    items = filteredProperties,
                    key = { it.id }
                ) { property ->
                    SavedPropertyCard(
                        property = property,
                        onLike = { onLike(property.documentId ?: property.id) },
                        onClick = { onPropertyClick(property) },
                        onMore = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Text(
                text = SavedStrings.APP_NAME_BLUE,
                color = BrandBlue,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = SavedStrings.APP_NAME_ACCENT,
                color = BrandCoral,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = SavedStrings.CD_NOTIFICATIONS,
                tint = Black,
                modifier = Modifier.size(SavedDims.HEADER_ICON_SIZE)
            )
            Box(
                modifier = Modifier
                    .size(SavedDims.NOTIFICATION_BADGE_SIZE)
                    .clip(CircleShape)
                    .background(BrandRed)
            )
        }
    }
}

@Composable
private fun SavedTitle(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = SavedStrings.TITLE,
            color = Black,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(SavedDims.TITLE_LINE_SPACING))
        Text(
            text = SavedStrings.SUBTITLE,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun FilterChipRow(
    counts: Map<SavedFilter, Int>,
    selected: SavedFilter,
    onSelect: (SavedFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SavedDims.CHIP_SPACING)
    ) {
        items(SavedFilter.entries, key = { it.name }) { filter ->
            val isSelected = filter == selected
            val count = counts[filter] ?: 0
            val label = String.format(SavedStrings.FILTER_COUNT_FORMAT, filter.label, count)
            val background = if (isSelected) BrandBlue else White
            val contentColor = if (isSelected) White else BrandBlue
            val border = if (isSelected) null else BorderStroke(SavedDims.SEARCH_DIVIDER_WIDTH, HomeSearchBarBorder)

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(SavedDims.CHIP_CORNER_RADIUS))
                    .clickable { onSelect(filter) },
                shape = RoundedCornerShape(SavedDims.CHIP_CORNER_RADIUS),
                color = background,
                border = border
            ) {
                Text(
                    text = label,
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(
                        horizontal = SavedDims.CHIP_HORIZONTAL_PADDING,
                        vertical = SavedDims.CHIP_VERTICAL_PADDING
                    )
                )
            }
        }
    }
}

@Composable
private fun SavedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SavedDims.SEARCH_CORNER_RADIUS),
        placeholder = {
            Text(
                text = SavedStrings.SEARCH_HINT,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = SavedStrings.CD_SEARCH_ICON,
                tint = HomeTextSecondary,
                modifier = Modifier.size(SavedDims.SEARCH_ICON_SIZE)
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = SavedStrings.CD_FILTER_ICON,
                tint = BrandBlue,
                modifier = Modifier.size(SavedDims.SEARCH_ICON_SIZE)
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedBorderColor = HomeSearchBarBorder,
            unfocusedBorderColor = HomeSearchBarBorder,
            focusedTextColor = Black,
            unfocusedTextColor = Black,
            focusedLeadingIconColor = HomeTextSecondary,
            unfocusedLeadingIconColor = HomeTextSecondary,
            focusedTrailingIconColor = BrandBlue,
            unfocusedTrailingIconColor = BrandBlue
        )
    )
}

@Composable
private fun CollectionCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SavedDims.COLLECTION_CARD_CORNER_RADIUS)),
        shape = RoundedCornerShape(SavedDims.COLLECTION_CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = SavedDims.COLLECTION_CARD_ELEVATION),
        border = BorderStroke(SavedDims.SEARCH_DIVIDER_WIDTH, HomeSearchBarBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SavedDims.CARD_INNER_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SavedDims.CARD_INNER_PADDING)
        ) {
            Box(
                modifier = Modifier
                    .size(SavedDims.COLLECTION_ICON_CIRCLE_SIZE)
                    .clip(CircleShape)
                    .background(BrandBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = SavedStrings.CD_FAVORITE,
                    tint = BrandBlue,
                    modifier = Modifier.size(SavedDims.COLLECTION_ICON_SIZE)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = SavedStrings.COLLECTION_TITLE,
                    color = Black,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = SavedStrings.COLLECTION_SUBTITLE,
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = SavedStrings.CD_COLLECTION_ARROW,
                tint = HomeTextSecondary,
                modifier = Modifier.size(SavedDims.SEARCH_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun SavedPropertyCard(
    property: Property,
    onLike: () -> Unit,
    onClick: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SavedDims.CARD_CORNER_RADIUS))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SavedDims.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = SavedDims.CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier.padding(SavedDims.CARD_INNER_PADDING),
            horizontalArrangement = Arrangement.spacedBy(SavedDims.CARD_INNER_PADDING),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(SavedDims.CARD_IMAGE_WIDTH)
                    .height(SavedDims.CARD_IMAGE_HEIGHT)
                    .clip(RoundedCornerShape(SavedDims.CARD_IMAGE_CORNER_RADIUS))
            ) {
                AsyncImage(
                    model = property.images.firstOrNull()
                        ?: "https://picsum.photos/seed/${property.id}/300/200",
                    contentDescription = SavedStrings.CD_PROPERTY_IMAGE,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (property.images.size > 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SavedDims.CARD_IMAGE_PHOTO_ICON_SPACING),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(SavedDims.CARD_PHOTO_COUNT_PADDING)
                            .clip(RoundedCornerShape(SavedDims.CARD_IMAGE_CORNER_RADIUS))
                            .background(Black.copy(alpha = 0.55f))
                            .padding(SavedDims.CARD_PHOTO_COUNT_PADDING)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = SavedStrings.CD_PHOTO_LIBRARY,
                            tint = White,
                            modifier = Modifier.size(SavedDims.CARD_IMAGE_PHOTO_ICON_SIZE)
                        )
                        Text(
                            text = property.photoCountLabel(),
                            color = White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SavedDims.CARD_DETAILS_SPACING)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.statusLabel(),
                        color = property.statusColor(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(SavedDims.CARD_STATUS_CORNER_RADIUS))
                            .background(property.statusBackground())
                            .padding(
                                horizontal = SavedDims.CARD_STATUS_HORIZONTAL_PADDING,
                                vertical = SavedDims.CARD_STATUS_VERTICAL_PADDING
                            )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(SavedDims.CARD_ICON_SPACING)) {
                        IconButton(
                            onClick = onLike,
                            modifier = Modifier.size(SavedDims.CARD_ACTION_ICON_SIZE)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = SavedStrings.CD_FAVORITE,
                                tint = BrandRed,
                                modifier = Modifier.size(SavedDims.CARD_ACTION_ICON_SIZE)
                            )
                        }
                        IconButton(
                            onClick = onMore,
                            modifier = Modifier.size(SavedDims.CARD_ACTION_ICON_SIZE)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = SavedStrings.CD_MORE_OPTIONS,
                                tint = HomeTextSecondary,
                                modifier = Modifier.size(SavedDims.CARD_ACTION_ICON_SIZE)
                            )
                        }
                    }
                }

                Text(
                    text = property.title,
                    color = Black,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SavedDims.CARD_ICON_SPACING)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = HomeTextSecondary,
                        modifier = Modifier.size(SavedDims.CARD_SMALL_ICON_SIZE)
                    )
                    Text(
                        text = "${property.locality}, ${property.city}",
                        color = HomeTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val priceText = if (property.isProject()) {
                    "${formatIndianPrice(property.price)} ${SavedStrings.PRICE_PROJECT_SUFFIX}"
                } else {
                    formatIndianPrice(property.price, property.isRentProperty())
                }
                Text(
                    text = priceText,
                    color = BrandBlue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val specs = property.specs()
                if (specs.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(SavedDims.CARD_SPEC_SPACING),
                        verticalArrangement = Arrangement.spacedBy(SavedDims.CARD_SPEC_SPACING)
                    ) {
                        specs.forEach { (icon, label) ->
                            SavedSpecChip(icon = icon, label = label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedSpecChip(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SavedDims.CARD_ICON_SPACING)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HomeTextSecondary,
            modifier = Modifier.size(SavedDims.CARD_SMALL_ICON_SIZE)
        )
        Text(
            text = label,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptySavedResults(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(SavedDims.SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = BrandCoral,
            modifier = Modifier.size(SavedDims.EMPTY_ICON_SIZE)
        )
        Spacer(modifier = Modifier.height(SavedDims.SECTION_SPACING))
        Text(
            text = SavedStrings.EMPTY_TITLE,
            color = Black,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(SavedDims.TITLE_LINE_SPACING))
        Text(
            text = SavedStrings.EMPTY_SUBTITLE,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(SavedDims.SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = Black,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(SavedDims.SECTION_SPACING))
        TextButton(
            onClick = onRetry,
            colors = ButtonDefaults.textButtonColors(contentColor = BrandBlue)
        ) {
            Text(text = SavedStrings.RETRY)
        }
    }
}
