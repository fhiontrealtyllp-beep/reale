package com.fhiont.feature.saved.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fhiont.ui.components.BOTTOM_NAV_CLEARANCE
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.model.RentBuy
import com.fhiont.feature.search.presentation.PropertyDetailScreen
import com.fhiont.feature.search.presentation.PropertyResultCard
import com.fhiont.ui.components.LoginPrompt
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.theme.Black
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.BrandCoral
import com.fhiont.ui.theme.HomeSearchBarBorder
import com.fhiont.ui.theme.HomeTextSecondary
import com.fhiont.ui.theme.OnControlAccent
import com.fhiont.ui.theme.White
import com.fhiont.ui.preview.PreviewData
import com.fhiont.ui.theme.FhiontTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview

private enum class SavedFilter(val label: String) {
    ALL(SavedStrings.FILTER_ALL),
    BUY(SavedStrings.FILTER_BUY),
    RENT(SavedStrings.FILTER_RENT);

    fun count(properties: List<Property>): Int = properties.count { matches(it) }

    fun matches(property: Property): Boolean = when (this) {
        ALL -> true
        BUY -> property.rentBuy == RentBuy.BUY
        RENT -> property.rentBuy == RentBuy.RENT
    }
}

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
                        color = ControlAccent
                    )
                }

                // Logged-out UI prompting the user to open the login flow.
                !uiState.isLoggedIn -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground)
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
        }
        if (property != null) {
            Dialog(
                onDismissRequest = { selectedProperty = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppBackground
                ) {
                    PropertyDetailScreen(
                        property = property,
                        onClose = { selectedProperty = null },
                        onLike = { viewModel.onLikeClicked(property.documentId ?: property.id) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            LaunchedEffect(Unit) { selectedProperty = null }
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
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            // Extra bottom space so the last item clears the floating glass nav capsule.
            contentPadding = PaddingValues(
                start = SavedDims.SCREEN_PADDING,
                top = SavedDims.SCREEN_PADDING,
                end = SavedDims.SCREEN_PADDING,
                bottom = SavedDims.SCREEN_PADDING + BOTTOM_NAV_CLEARANCE
            ),
            verticalArrangement = Arrangement.spacedBy(SavedDims.ITEM_SPACING)
        ) {
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
                    key = { it.documentId ?: it.id }
                ) { property ->
                    PropertyResultCard(
                        property = property,
                        onLike = { onLike(property.documentId ?: property.id) },
                        onClick = { onPropertyClick(property) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Saved Properties List")
@Composable
private fun SavedPropertyListPreview() {
    FhiontTheme {
        SavedPropertyList(
            properties = PreviewData.sampleProperties,
            isLoading = false,
            searchQuery = "",
            onSearchChange = {},
            selectedFilter = SavedFilter.ALL,
            onFilterSelected = {},
            onRefresh = {},
            onPropertyClick = {},
            onLike = {}
        )
    }
}

@Preview(showBackground = true, name = "Saved Properties Empty")
@Composable
private fun SavedPropertyListEmptyPreview() {
    FhiontTheme {
        SavedPropertyList(
            properties = emptyList(),
            isLoading = false,
            searchQuery = "",
            onSearchChange = {},
            selectedFilter = SavedFilter.ALL,
            onFilterSelected = {},
            onRefresh = {},
            onPropertyClick = {},
            onLike = {}
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
            val background = if (isSelected) ControlAccent else White
            val contentColor = if (isSelected) OnControlAccent else ControlAccent
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
            focusedTrailingIconColor = ControlAccent,
            unfocusedTrailingIconColor = ControlAccent
        )
    )
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
            colors = ButtonDefaults.textButtonColors(contentColor = ControlAccent)
        ) {
            Text(text = SavedStrings.RETRY)
        }
    }
}
