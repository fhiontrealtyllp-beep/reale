package com.fhiont.feature.search.presentation

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.model.PropertyFilter
import com.fhiont.ui.components.BOTTOM_NAV_CLEARANCE
import com.fhiont.ui.preview.PreviewData
import com.fhiont.ui.theme.Accent
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.theme.Black
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.FhiontTheme
import com.fhiont.ui.theme.White
import com.fhiont.feature.auth.presentation.LoginPromptDialog
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    onPropertyClick: (Property) -> Unit = {},
    onChangeCity: () -> Unit = {},
    onOpenFilter: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedHomeCategory.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val sortBy = uiState.sortBy

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

    val headerTitle = remember(uiState.currentFilter, visibleProperties) {
        viewModel.buildHeaderTitle(uiState.currentFilter, visibleProperties.size)
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
            headerTitle = headerTitle,
            isLoading = uiState.isLoading,
            isLoadingMore = uiState.isLoadingMore,
            hasReachedEnd = uiState.hasReachedEnd,
            currentFilter = uiState.currentFilter,
            sortBy = sortBy,
            onSortChange = viewModel::onSortChanged,
            onRefresh = viewModel::refresh,
            onLoadMore = viewModel::onLoadMore,
            onOpenFilter = onOpenFilter,
            onLike = { propertyId ->
                viewModel.onLikeClicked(propertyId)
                val property = visibleProperties.find { it.documentId == propertyId || it.id == propertyId }
                val propertyTitle = property?.title.orEmpty()
                val message = if (property?.isLiked == true) {
                    String.format(SearchStrings.REMOVED_FROM_SAVED_TOAST_FORMAT, propertyTitle)
                } else {
                    String.format(SearchStrings.SAVED_TOAST_FORMAT, propertyTitle)
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onPropertyClick = onPropertyClick,
            onChangeCity = onChangeCity,
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (uiState.showLoginPrompt) {
        LoginPromptDialog(
            onDismiss = viewModel::onLoginPromptDismissed,
            onLoginClick = {
                viewModel.onLoginPromptDismissed()
                onLoginClick()
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertiesScreenContent(
    properties: List<Property>,
    headerTitle: String,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasReachedEnd: Boolean,
    currentFilter: PropertyFilter?,
    sortBy: SortBy,
    onSortChange: (SortBy) -> Unit,
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
            headerTitle = headerTitle,
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
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                // Extra bottom space so the last card clears the floating glass nav capsule.
                contentPadding = PaddingValues(
                    bottom = PropertiesDims.RESULTS_LIST_VERTICAL_PADDING + BOTTOM_NAV_CLEARANCE
                ),
                verticalArrangement = Arrangement.spacedBy(PropertiesDims.RESULTS_LIST_SPACING)
            ) {
                item {
                    ResultsHeader(
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
    headerTitle: String,
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
            text = headerTitle,
            color = Black,
            fontSize = PropertiesDims.HEADER_TITLE_FONT_SIZE,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Filled.Tune,
            contentDescription = PropertiesStrings.CD_FILTER,
            tint = Black,
            modifier = Modifier
                .size(PropertiesDims.SEARCH_BAR_ICON_SIZE)
                .clickable(onClick = onOpenFilter)
        )
    }
}


@Composable
private fun ResultsHeader(
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
                    .padding(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.SwapVert,
                    contentDescription = PropertiesStrings.CD_SORT,
                    tint = ControlAccent,
                    modifier = Modifier.size(PropertiesDims.SEARCH_BAR_ICON_SIZE)
                )
                Text(
                    text = "${PropertiesStrings.SORT_BY} ${sortBy.label}",
                    color = ControlAccent,
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
                                color = if (option == sortBy) ControlAccent else Black
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
    FhiontTheme(darkTheme = false) {
        PropertiesScreenContent(
            properties = PreviewData.sampleProperties,
            isLoading = false,
            isLoadingMore = false,
            hasReachedEnd = true,
            currentFilter = PropertyFilter(city = "Goa", localities = listOf("Porvorim")),
            headerTitle = String.format(Locale.getDefault(), PropertiesStrings.PROPERTIES_FOUND_IN_LOCATION_FORMAT, 5, "Porvorim", "Goa"),
            sortBy = SortBy.RELEVANCE,
            onSortChange = {},
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
    FhiontTheme(darkTheme = true) {
        PropertiesScreenContent(
            properties = PreviewData.sampleProperties,
            isLoading = false,
            isLoadingMore = false,
            hasReachedEnd = true,
            currentFilter = PropertyFilter(city = "Goa", localities = listOf("Porvorim")),
            headerTitle = String.format(Locale.getDefault(), PropertiesStrings.PROPERTIES_FOUND_IN_LOCATION_FORMAT, 5, "Porvorim", "Goa"),
            sortBy = SortBy.RELEVANCE,
            onSortChange = {},
            onRefresh = {},
            onLoadMore = {},
            onOpenFilter = {},
            onLike = {},
            onPropertyClick = {}
        )
    }
}
