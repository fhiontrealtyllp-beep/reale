package com.fhiont.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fhiont.feature.search.domain.model.BedroomType
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.model.RentBuy
import com.fhiont.feature.search.presentation.HomeCategory
import com.fhiont.feature.auth.presentation.LoginPromptDialog
import com.fhiont.feature.search.presentation.SearchViewModel
import com.fhiont.feature.search.presentation.components.MapViewContent
import com.fhiont.ui.components.BOTTOM_NAV_CLEARANCE
import com.fhiont.ui.preview.PreviewData
import com.fhiont.ui.theme.Accent
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.components.GenericLoader
import com.fhiont.ui.theme.FhiontTheme
import com.fhiont.ui.theme.White
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.fhiont.feature.search.presentation.SearchStrings

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSavedClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onChangeCity: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val featuredProperties by viewModel.featuredProperties.collectAsStateWithLifecycle()
    val promotionalProperties by viewModel.promotionalProperties.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedHomeCategory.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val context = LocalContext.current

    HomeContent(
        featuredProperties = featuredProperties,
        promotionalProperties = promotionalProperties,
        mapProperties = uiState.properties,
        selectedCategory = selectedCategory,
        selectedCity = selectedCity,
        isLoading = uiState.isLoading,
        onSearchClick = onSearchClick,
        onSavedClick = onSavedClick,
        onAddClick = onAddClick,
        onProfileClick = onProfileClick,
        onCategorySelected = viewModel::onCategorySelected,
        onLike = { property ->
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
        onChangeCity = onChangeCity,
        modifier = modifier
    )

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

@Composable
internal fun HomeContent(
    featuredProperties: List<Property>,
    promotionalProperties: List<Property>,
    mapProperties: List<Property> = emptyList(),
    selectedCategory: HomeCategory,
    selectedCity: String? = null,
    isLoading: Boolean = false,
    onSearchClick: () -> Unit,
    onSavedClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCategorySelected: (HomeCategory) -> Unit = {},
    onLike: (Property) -> Unit = {},
    onChangeCity: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val featuredList = remember(featuredProperties) {
        featuredProperties
            .distinctBy { it.documentId ?: it.id }
            .map(Property::toFeaturedProperty)
    }
    val propertyById = remember(featuredProperties) {
        featuredProperties.associateBy { it.documentId ?: it.id }
    }

    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var showMap by remember { mutableStateOf(false) }

    // Union of every property Home knows about, deduped for stable marker state.
    val mapProperties = remember(featuredProperties, promotionalProperties, mapProperties) {
        (featuredProperties + promotionalProperties + mapProperties)
            .distinctBy { it.documentId ?: it.id }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { },
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMap = !showMap },
                containerColor = Accent,
                contentColor = White,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = BOTTOM_NAV_CLEARANCE)
            ) {
                Icon(
                    imageVector = if (showMap) {
                        Icons.AutoMirrored.Filled.List
                    } else {
                        Icons.Filled.Map
                    },
                    contentDescription = if (showMap) {
                        HomeStrings.CD_LIST_VIEW
                    } else {
                        HomeStrings.CD_MAP_VIEW
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(HomeDims.SECTION_SPACING)
        ) {
            HomeHeaderSection(
                onSearchClick = onSearchClick,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )

            if (isLoading) {
                GenericLoader(modifier = Modifier.weight(1f))
            } else if (showMap) {
                MapViewContent(
                    properties = mapProperties,
                    onPropertyTap = { selectedProperty = it },
                    modifier = Modifier.weight(1f)
                )
            } else {
                HomePropertyFeed(
                    featuredProperties = featuredList,
                    promotionalProperties = promotionalProperties,
                    city = selectedCity,
                    onSeeAllClick = onSearchClick,
                    onPropertyClick = { id ->
                        selectedProperty = featuredProperties.find {
                            (it.documentId ?: it.id) == id
                        }
                    },
                    onPromotionClick = { selectedProperty = it },
                    onChangeCity = onChangeCity,
                    onLike = { featured ->
                        propertyById[featured.id]?.let { onLike(it) }
                    },
                    onPromotionalLike = onLike,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    selectedProperty?.let { selected ->
        // Resolve against the live union (featured + promotional + results) so
        // isLiked stays in sync; unified key avoids cross-id false matches.
        val selectedKey = selected.documentId ?: selected.id
        val property = mapProperties.find {
            (it.documentId ?: it.id) == selectedKey
        } ?: selected
        PropertyDetailDialog(
            property = property,
            onClose = { selectedProperty = null },
            onLike = { onLike(property) }
        )
    }
}

private fun Property.toFeaturedProperty(): FeaturedProperty = FeaturedProperty(
    id = documentId ?: id,
    imageUrl = images.firstOrNull().orEmpty(),
    price = price,
    title = title,
    location = listOf(locality, city).filter(String::isNotBlank).joinToString(HomeStrings.LOCATION_SEPARATOR),
    beds = bedroomType.toBedroomCount(),
    baths = bathrooms ?: 0,
    sqft = (superBuiltUpArea ?: builtUpArea ?: carpetArea ?: 0.0).toInt(),
    isRent = rentBuy == RentBuy.RENT,
    isLiked = isLiked ?: false
)

private fun BedroomType?.toBedroomCount(): Int = when (this) {
    BedroomType.ONE_RK,
    BedroomType.ONE_BHK -> 1
    BedroomType.TWO_BHK -> 2
    BedroomType.THREE_BHK -> 3
    BedroomType.FOUR_BHK -> 4
    BedroomType.FIVE_BHK -> 5
    BedroomType.SIX_BHK,
    BedroomType.SIX_PLUS_BHK -> 6
    BedroomType.STUDIO_APARTMENT,
    null -> 0
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    FhiontTheme {
        HomeContent(
            featuredProperties = PreviewData.sampleProperties,
            promotionalProperties = PreviewData.sampleProperties.take(2),
            selectedCategory = HomeCategory.RENT,
            onSearchClick = {}
        )
    }
}
