package com.realeapp.feature.search.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.House
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Villa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.realeapp.feature.search.domain.model.PriceRange
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial
import com.realeapp.feature.search.presentation.components.FilterDialog
import com.realeapp.feature.search.presentation.components.MapViewContent
import com.realeapp.feature.search.presentation.components.PropertyList
import com.realeapp.feature.search.presentation.components.SearchHeader
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.White
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private const val BUDGET_MIN = 5_000_000f
private const val BUDGET_MAX = 50_000_000f
private const val LAKH = 100_000f
private const val CRORE = 10_000_000f

private data class SearchTab(
    val label: String,
    val rentBuy: RentBuy?,
    val residentialCommercial: ResidentialCommercial?
)

private val searchTabs = listOf(
    SearchTab(SearchStrings.TAB_BUY, RentBuy.BUY, null),
    SearchTab(SearchStrings.TAB_RENT, RentBuy.RENT, null),
    SearchTab(SearchStrings.TAB_PROJECTS, null, null),
    SearchTab(SearchStrings.TAB_COMMERCIAL, null, ResidentialCommercial.COMMERCIAL)
)

private data class SearchTypeOption(val type: PropertyType, val icon: ImageVector)

private val searchTypeOptions = listOf(
    SearchTypeOption(PropertyType.APARTMENT, Icons.Outlined.Apartment),
    SearchTypeOption(PropertyType.VILLA, Icons.Outlined.Villa),
    SearchTypeOption(PropertyType.PLOT, Icons.Outlined.Landscape),
    SearchTypeOption(PropertyType.INDEPENDENT_HOUSE, Icons.Outlined.House)
)

private data class PopularLocation(
    val name: String,
    val propertyCount: Int,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Back press returns from the results view to the search landing view.
    BackHandler(enabled = showResults) {
        showResults = false
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = if (showResults) MainBackground else AppBackground,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CardBackground,
                    contentColor = TextPrimary
                )
            }
        },
        floatingActionButton = {
            // View toggle action switches the results UI between map and list modes.
            if (showResults) {
                FloatingActionButton(
                    onClick = viewModel::onToggleView,
                    containerColor = Accent,
                    contentColor = OnAccent
                ) {
                    Icon(
                        imageVector = if (uiState.isMapView) Icons.AutoMirrored.Filled.List else Icons.Default.Map,
                        contentDescription = if (uiState.isMapView) SearchStrings.CD_LIST_VIEW else SearchStrings.CD_MAP_VIEW
                    )
                }
            }
        }
    ) { innerPadding ->
        if (showResults) {
            SearchResultsContent(
                uiState = uiState,
                onBack = { showResults = false },
                onOpenFilter = { showFilterDialog = true },
                onRefresh = viewModel::refresh,
                onLoadMore = viewModel::onLoadMore,
                onLike = viewModel::onLikeClicked,
                onPropertyClick = { selectedProperty = it },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            SearchLandingContent(
                onOpenFilter = { showFilterDialog = true },
                onSearch = { filter ->
                    viewModel.onFilterChanged(filter)
                    showResults = true
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    // Full-screen property details UI shown after selecting a result.
    selectedProperty?.let { selected ->
        val property = uiState.properties.find {
            it.documentId == selected.documentId || it.id == selected.id
        } ?: selected
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

    // Filter dialog overlay shown from the search header and landing search bar.
    if (showFilterDialog) {
        FilterDialog(
            filter = uiState.currentFilter,
            onDismiss = { showFilterDialog = false },
            onApply = { newFilter ->
                showFilterDialog = false
                viewModel.onFilterChanged(newFilter)
                showResults = true
            },
            onReset = {
                showFilterDialog = false
                viewModel.onResetFilter()
            }
        )
    }
}

@Composable
private fun SearchResultsContent(
    uiState: SearchUiState,
    onBack: () -> Unit,
    onOpenFilter: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onLike: (String) -> Unit,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = SearchStrings.CD_BACK,
                    tint = TextPrimary
                )
            }
            // Search and filter controls remain visible above every results state.
            SearchHeader(
                onOpenFilter = onOpenFilter,
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                // Full-screen loading UI while the initial search results are fetched.
                uiState.isLoading && uiState.properties.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Accent
                    )
                }

                // Error UI with an action to retry the search.
                uiState.errorMessage != null -> ErrorContent(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = onRefresh,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Map results UI with tappable property markers.
                uiState.isMapView -> MapViewContent(
                    properties = uiState.properties,
                    onPropertyTap = onPropertyClick,
                    modifier = Modifier.fillMaxSize()
                )

                // Paginated list results UI used as the default search view.
                else -> PropertyList(
                    properties = uiState.properties,
                    isLoading = uiState.isLoading,
                    isLoadingMore = uiState.isLoadingMore,
                    hasReachedEnd = uiState.hasReachedEnd,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                    onLike = onLike,
                    onPropertyClick = onPropertyClick,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = SearchDims.RESULTS_LIST_HORIZONTAL_PADDING,
                        top = SearchDims.RESULTS_LIST_TOP_PADDING,
                        end = SearchDims.RESULTS_LIST_HORIZONTAL_PADDING,
                        bottom = SearchDims.RESULTS_LIST_BOTTOM_PADDING
                    )
                )
            }
        }
    }
}

@Composable
private fun SearchLandingContent(
    onOpenFilter: () -> Unit,
    onSearch: (PropertyFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedType by remember { mutableStateOf<PropertyType?>(null) }
    var budget by remember { mutableStateOf(BUDGET_MIN..BUDGET_MAX) }
    val locations = remember { samplePopularLocations() }

    fun currentFilter() = PropertyFilter(
        rentBuy = searchTabs[selectedTabIndex].rentBuy,
        residentialCommercial = searchTabs[selectedTabIndex].residentialCommercial,
        propertyType = selectedType,
        priceRange = PriceRange(budget.start.toDouble(), budget.endInclusive.toDouble())
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = SearchDims.CONTENT_VERTICAL_PADDING),
        verticalArrangement = Arrangement.spacedBy(SearchDims.SECTION_SPACING)
    ) {
        item { SearchTopBar(modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)) }

        item {
            Text(
                text = SearchStrings.TITLE,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
            )
        }

        item {
            LandingSearchBar(
                onClick = { onSearch(currentFilter()) },
                onFilterClick = onOpenFilter,
                modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
            )
        }

        item {
            SearchTabsRow(
                selectedIndex = selectedTabIndex,
                onSelect = { selectedTabIndex = it },
                modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
            )
        }

        item {
            PopularLocationsSection(
                locations = locations,
                onLocationClick = { onSearch(currentFilter().copy(city = it.name)) },
                onSeeAllClick = { onSearch(currentFilter()) }
            )
        }

        item {
            PropertyTypeSection(
                selectedType = selectedType,
                onSelect = { selectedType = if (selectedType == it) null else it },
                modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
            )
        }

        item {
            BudgetSection(
                budget = budget,
                onBudgetChange = { budget = it },
                modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
            )
        }

        item {
            Button(
                onClick = { onSearch(currentFilter()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SearchDims.SCREEN_PADDING)
                    .height(SearchDims.BUTTON_HEIGHT),
                shape = RoundedCornerShape(SearchDims.BUTTON_CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandCoral,
                    contentColor = OnBrandContent
                )
            ) {
                Text(
                    text = SearchStrings.BTN_SEARCH_PROPERTIES,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(SearchDims.BUTTON_CONTENT_SPACING))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = SearchStrings.CD_SEARCH_ARROW,
                    modifier = Modifier.size(SearchDims.BUTTON_ICON_SIZE)
                )
            }
        }
    }
}

@Composable
private fun SearchTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Text(
                text = SearchStrings.APP_NAME_BLUE,
                color = BrandBlue,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = SearchStrings.APP_NAME_ACCENT,
                color = BrandCoral,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = SearchStrings.CD_NOTIFICATIONS,
                tint = Black,
                modifier = Modifier.size(SearchDims.HEADER_ICON_SIZE)
            )
            Box(
                modifier = Modifier
                    .size(SearchDims.NOTIFICATION_BADGE_SIZE)
                    .clip(CircleShape)
                    .background(BrandRed)
            )
        }
    }
}

@Composable
private fun LandingSearchBar(
    onClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(SearchDims.SEARCH_HEIGHT)
            .clip(RoundedCornerShape(SearchDims.SEARCH_CORNER_RADIUS))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SearchDims.SEARCH_CORNER_RADIUS),
        color = White,
        border = BorderStroke(SearchDims.BORDER_WIDTH, HomeSearchBarBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = SearchDims.SEARCH_HORIZONTAL_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = SearchStrings.CD_SEARCH_ICON,
                tint = HomeTextSecondary,
                modifier = Modifier.size(SearchDims.SEARCH_ICON_SIZE)
            )
            Spacer(modifier = Modifier.width(SearchDims.SEARCH_CONTENT_SPACING))
            Text(
                text = SearchStrings.SEARCH_HINT,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(SearchDims.SEARCH_DIVIDER_WIDTH)
                    .height(SearchDims.SEARCH_DIVIDER_HEIGHT)
                    .background(HomeSearchBarBorder)
            )
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = SearchStrings.CD_FILTER_ICON,
                    tint = BrandBlue,
                    modifier = Modifier.size(SearchDims.SEARCH_ICON_SIZE)
                )
            }
        }
    }
}

@Composable
private fun SearchTabsRow(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SearchDims.TAB_SPACING)
    ) {
        searchTabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(SearchDims.TAB_CORNER_RADIUS))
                    .clickable { onSelect(index) },
                shape = RoundedCornerShape(SearchDims.TAB_CORNER_RADIUS),
                color = if (selected) BrandBlue else White,
                border = if (selected) null else BorderStroke(SearchDims.BORDER_WIDTH, HomeSearchBarBorder)
            ) {
                Text(
                    text = tab.label,
                    color = if (selected) OnBrandContent else Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SearchDims.TAB_VERTICAL_PADDING)
                )
            }
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    action: String?,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (action != null) {
            Text(
                text = action,
                color = BrandBlue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}

@Composable
private fun PopularLocationsSection(
    locations: List<PopularLocation>,
    onLocationClick: (PopularLocation) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeaderRow(
            title = SearchStrings.SECTION_LOCATIONS,
            action = SearchStrings.ACTION_SEE_ALL,
            onActionClick = onSeeAllClick,
            modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
        )
        Spacer(modifier = Modifier.height(SearchDims.SECTION_HEADER_SPACING))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(SearchDims.LOCATION_CARD_SPACING),
            contentPadding = PaddingValues(horizontal = SearchDims.SCREEN_PADDING)
        ) {
            items(locations, key = { it.name }) { location ->
                LocationCard(
                    location = location,
                    onClick = { onLocationClick(location) }
                )
            }
        }
    }
}

@Composable
private fun LocationCard(
    location: PopularLocation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(SearchDims.LOCATION_CARD_WIDTH)
            .clip(RoundedCornerShape(SearchDims.LOCATION_CARD_CORNER_RADIUS))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SearchDims.LOCATION_CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = SearchDims.CARD_ELEVATION)
    ) {
        Column {
            AsyncImage(
                model = location.imageUrl,
                contentDescription = SearchStrings.CD_LOCATION_IMAGE,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SearchDims.LOCATION_IMAGE_HEIGHT)
            )
            Column(
                modifier = Modifier.padding(SearchDims.LOCATION_TEXT_PADDING),
                verticalArrangement = Arrangement.spacedBy(SearchDims.LOCATION_TEXT_SPACING)
            ) {
                Text(
                    text = location.name,
                    color = Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${NumberFormat.getNumberInstance(Locale.getDefault()).format(location.propertyCount)} ${SearchStrings.PROPERTIES_SUFFIX}",
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PropertyTypeSection(
    selectedType: PropertyType?,
    onSelect: (PropertyType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeaderRow(
            title = SearchStrings.SECTION_LOOKING_FOR,
            action = null,
            onActionClick = {}
        )
        Spacer(modifier = Modifier.height(SearchDims.SECTION_HEADER_SPACING))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(SearchDims.TYPE_CARD_SPACING)
        ) {
            searchTypeOptions.forEach { option ->
                val selected = option.type == selectedType
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(SearchDims.TYPE_CARD_CORNER_RADIUS))
                        .clickable { onSelect(option.type) },
                    shape = RoundedCornerShape(SearchDims.TYPE_CARD_CORNER_RADIUS),
                    color = if (selected) BrandBlue else White,
                    border = if (selected) null else BorderStroke(SearchDims.BORDER_WIDTH, HomeSearchBarBorder)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = SearchDims.TYPE_CARD_VERTICAL_PADDING)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(SearchDims.TYPE_ICON_CIRCLE_SIZE)
                                .clip(CircleShape)
                                .background(
                                    if (selected) OnBrandContent.copy(alpha = SearchDims.SELECTED_ICON_OVERLAY_ALPHA)
                                    else HomeCategoryUnselected
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = option.type.label,
                                tint = if (selected) OnBrandContent else BrandBlue,
                                modifier = Modifier.size(SearchDims.TYPE_ICON_SIZE)
                            )
                        }
                        Spacer(modifier = Modifier.height(SearchDims.TYPE_LABEL_SPACING))
                        Text(
                            text = option.type.label,
                            color = if (selected) OnBrandContent else Black,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetSection(
    budget: ClosedFloatingPointRange<Float>,
    onBudgetChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = SearchStrings.SECTION_BUDGET,
                color = Black,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = budgetRangeText(budget),
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        RangeSlider(
            value = budget,
            onValueChange = onBudgetChange,
            valueRange = BUDGET_MIN..BUDGET_MAX,
            colors = SliderDefaults.colors(
                thumbColor = BrandBlue,
                activeTrackColor = BrandBlue,
                inactiveTrackColor = HomeSearchBarBorder
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            budgetTicks().forEach { tick ->
                Text(
                    text = tick,
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun budgetTicks(): List<String> = listOf(
    budgetTick(BUDGET_MIN),
    budgetTick(CRORE),
    budgetTick(2 * CRORE),
    budgetTick(BUDGET_MAX)
)

private fun budgetRangeText(range: ClosedFloatingPointRange<Float>): String =
    "${SearchStrings.RUPEE} ${budgetTick(range.start)}" +
        "${SearchStrings.RANGE_SEPARATOR}${SearchStrings.RUPEE} ${budgetTick(range.endInclusive)}${SearchStrings.PLUS_SUFFIX}"

private fun budgetTick(value: Float): String = when {
    value >= CRORE -> compactNumber(value / CRORE) + SearchStrings.CRORE_SUFFIX
    else -> compactNumber(value / LAKH) + SearchStrings.LAKH_SUFFIX
}

private fun compactNumber(value: Float): String =
    if (value == value.roundToInt().toFloat()) {
        value.roundToInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }

private fun samplePopularLocations(): List<PopularLocation> = listOf(
    PopularLocation(
        name = SearchStrings.LOCATION_NORTH_GOA,
        propertyCount = 1250,
        imageUrl = "https://images.unsplash.com/photo-1600596542815-86d7f88998bb?w=600&q=80"
    ),
    PopularLocation(
        name = SearchStrings.LOCATION_SOUTH_GOA,
        propertyCount = 980,
        imageUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=600&q=80"
    ),
    PopularLocation(
        name = SearchStrings.LOCATION_PANAJI,
        propertyCount = 640,
        imageUrl = "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=600&q=80"
    )
)

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(SearchDims.ERROR_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = TextPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
        TextButton(
            onClick = onRetry,
            colors = ButtonDefaults.textButtonColors(contentColor = Accent)
        ) {
            Text(text = SearchStrings.RETRY)
        }
    }
}
