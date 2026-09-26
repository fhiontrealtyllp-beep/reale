package com.fhiont.feature.search.presentation

import android.widget.Toast
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fhiont.feature.search.domain.model.Enquiry
import com.fhiont.ui.components.BOTTOM_NAV_CLEARANCE
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.model.PropertyFilter
import com.fhiont.feature.search.domain.model.RentBuy
import com.fhiont.feature.search.domain.model.ResidentialCommercial
import com.fhiont.feature.search.presentation.components.PropertyFilters
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.theme.Black
import com.fhiont.ui.theme.BrandCoral
import com.fhiont.ui.theme.Error
import com.fhiont.ui.theme.OnBrandContent
import com.fhiont.ui.theme.FhiontTheme
import com.fhiont.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onChangeCity: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onViewChats: (Property) -> Unit = {},
    onOpenChat: (Enquiry) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val showResults = uiState.showResults
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var draftFilter by remember(uiState.currentFilter) {
        mutableStateOf(
            uiState.currentFilter ?: PropertyFilter(
                rentBuy = RentBuy.RENT,
                residentialCommercial = ResidentialCommercial.RESIDENTIAL
            )
        )
    }

    LaunchedEffect(showResults) {
        if (showResults) return@LaunchedEffect
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Back press returns from the results view to the search landing view.
    BackHandler(enabled = showResults) {
        viewModel.onShowResultsChanged(false)
    }

    Scaffold(
        modifier = modifier.imePadding(),
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = AppBackground,
        bottomBar = {
            if (!showResults) {
                // Lifted above the floating glass nav capsule.
                Surface(
                    color = AppBackground,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Button(
                        onClick = {
                            viewModel.onFilterChanged(draftFilter)
                            viewModel.onShowResultsChanged(true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = SearchDims.STICKY_ACTION_HORIZONTAL_PADDING,
                                top = SearchDims.STICKY_ACTION_VERTICAL_PADDING,
                                end = SearchDims.STICKY_ACTION_HORIZONTAL_PADDING,
                                bottom = SearchDims.STICKY_ACTION_VERTICAL_PADDING + BOTTOM_NAV_CLEARANCE
                            )
                            .height(SearchDims.BUTTON_HEIGHT),
                        shape = RoundedCornerShape(SearchDims.BUTTON_CORNER_RADIUS),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandCoral.copy(alpha = 0.85f),
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
                    containerColor = White,
                    contentColor = Black
                )
            }
        }
    ) { innerPadding ->
        if (showResults) {
            PropertiesScreen(
                viewModel = viewModel,
                onPropertyClick = { selectedProperty = it },
                onChangeCity = onChangeCity,
                onOpenFilter = { viewModel.onShowResultsChanged(false) },
                onLoginClick = onLoginClick,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            SearchLandingContent(
                filter = draftFilter,
                onFilterChange = { draftFilter = it },
                citySuggestionsProvider = viewModel::getCitySuggestions,
                localitySuggestionsProvider = viewModel::getLocalitySuggestions,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    // Full-screen property details UI shown after selecting a result.
    selectedProperty?.let { selected ->
        val property = uiState.properties.find {
            (it.documentId ?: it.id) == (selected.documentId ?: selected.id)
        } ?: selected
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
                    onLike = {
                        val didLike = viewModel.onLikeClicked(property.documentId ?: property.id)
                        if (didLike) {
                            val message = if (property.isLiked == true) {
                                String.format(SearchStrings.REMOVED_FROM_SAVED_TOAST_FORMAT, property.title)
                            } else {
                                String.format(SearchStrings.SAVED_TOAST_FORMAT, property.title)
                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onViewChats = {
                        selectedProperty = null
                        onViewChats(property)
                    },
                    onOpenChat = { enquiry ->
                        selectedProperty = null
                        onOpenChat(enquiry)
                    },
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
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                    .background(Error)
            )
        }
    }
}

@Composable
private fun SearchLandingContent(
    filter: PropertyFilter,
    onFilterChange: (PropertyFilter) -> Unit,
    citySuggestionsProvider: suspend (String) -> List<String> = { _ -> emptyList() },
    localitySuggestionsProvider: suspend (String, String) -> List<String> = { _, _ -> emptyList() },
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

        item {
            PropertyFilters(
                filter = filter,
                onFilterChange = onFilterChange,
                citySuggestionsProvider = citySuggestionsProvider,
                localitySuggestionsProvider = localitySuggestionsProvider,
                modifier = Modifier.padding(horizontal = SearchDims.SCREEN_PADDING)
            )
        }
    }
}

@Preview(showBackground = true, name = "Search Filters")
@Composable
private fun SearchLandingContentPreview() {
    FhiontTheme {
        SearchLandingContent(
            filter = PropertyFilter(),
            onFilterChange = {},
            citySuggestionsProvider = { _ -> emptyList() },
            localitySuggestionsProvider = { _, _ -> emptyList() }
        )
    }
}
