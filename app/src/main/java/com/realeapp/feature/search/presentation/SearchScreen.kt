package com.realeapp.feature.search.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realeapp.AppStrings
import com.realeapp.feature.search.domain.model.LocationSuggestion
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyFilter
import com.realeapp.feature.search.presentation.components.LocationSearchBar
import com.realeapp.feature.search.presentation.components.PropertyFilters
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.TextPrimary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onChangeCity: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showResults by rememberSaveable { mutableStateOf(false) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var draftFilter by remember(uiState.currentFilter) {
        mutableStateOf(uiState.currentFilter ?: PropertyFilter())
    }

    LaunchedEffect(showResults) {
        if (showResults) return@LaunchedEffect
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
        containerColor = AppBackground,
        bottomBar = {
            if (!showResults) {
                Surface(color = AppBackground) {
                    Button(
                        onClick = {
                            viewModel.onFilterChanged(draftFilter)
                            showResults = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = SearchDims.STICKY_ACTION_HORIZONTAL_PADDING,
                                vertical = SearchDims.STICKY_ACTION_VERTICAL_PADDING
                            )
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
        },
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
        if (showResults) {
            PropertiesScreen(
                viewModel = viewModel,
                onPropertyClick = { selectedProperty = it },
                onChangeCity = onChangeCity,
                onOpenFilter = { showResults = false },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            SearchLandingContent(
                query = query,
                suggestions = suggestions,
                filter = draftFilter,
                onFilterChange = { draftFilter = it },
                onQueryChange = viewModel::onSearchQueryChanged,
                onSuggestionSelected = viewModel::onSuggestionSelected,
                locationSuggestionsProvider = viewModel::getSuggestions,
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
                text = AppStrings.APP_NAME_FIRST,
                color = BrandBlue,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = AppStrings.APP_NAME_ACCENT,
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
private fun SearchLandingContent(
    query: String,
    suggestions: List<LocationSuggestion>,
    filter: PropertyFilter,
    onFilterChange: (PropertyFilter) -> Unit,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (LocationSuggestion) -> Unit,
    locationSuggestionsProvider: suspend (String) -> List<LocationSuggestion> = { _ -> emptyList() },
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = SearchDims.CONTENT_VERTICAL_PADDING,
            bottom = SearchDims.FILTER_LIST_BOTTOM_PADDING
        ),
        verticalArrangement = Arrangement.spacedBy(SearchDims.SECTION_SPACING)
    ) {
        item { SearchTopBar(modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)) }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SearchDims.SCREEN_PADDING),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SearchStrings.TITLE,
                    color = HomeTextSecondary,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(
                    onClick = { onFilterChange(PropertyFilter(city = filter.city)) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = SearchStrings.FILTER_RESET,
                        tint = Black
                    )
                }
            }
        }

        //searchbar
       /* item {
            LocationSearchBar(
                query = query,
                suggestions = suggestions,
                onQueryChange = { value ->
                    onQueryChange(value)
                    onFilterChange(filter.copy(city = value.takeIf(String::isNotBlank)))
                },
                onSuggestionSelected = { suggestion ->
                    onSuggestionSelected(suggestion)
                    onFilterChange(
                        filter.copy(
                            city = suggestion.primaryText,
                            localities = suggestion.secondaryText
                                .takeIf(String::isNotBlank)
                                ?.let(::listOf)
                                ?: emptyList()
                        )
                    )
                },
                onClearQuery = {
                    onQueryChange("")
                    onFilterChange(filter.copy(city = null, localities = emptyList()))
                },
                autoFocus = true,
                modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
            )
        }*/

        item {
            PropertyFilters(
                filter = filter,
                onFilterChange = onFilterChange,
                locationSuggestionsProvider = locationSuggestionsProvider,
                modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
            )
        }
    }
}

@Preview(showBackground = true, name = "Search Filters")
@Composable
private fun SearchLandingContentPreview() {
    RealeTheme {
        SearchLandingContent(
            query = "",
            suggestions = emptyList(),
            filter = PropertyFilter(),
            onFilterChange = {},
            onQueryChange = {},
            onSuggestionSelected = {},
            locationSuggestionsProvider = { _ -> emptyList() }
        )
    }
}
