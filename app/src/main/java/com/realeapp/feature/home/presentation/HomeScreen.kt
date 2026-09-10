package com.realeapp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.presentation.EmptyResults
import com.realeapp.feature.search.presentation.HomeCategory
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.feature.search.presentation.SearchViewModel
import com.realeapp.ui.preview.PreviewData
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.RealeTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSavedClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onChangeCity: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val featuredProperties by viewModel.featuredProperties.collectAsStateWithLifecycle()
    val promotionalProperty by viewModel.promotionalProperty.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedHomeCategory.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()

    HomeContent(
        featuredProperties = featuredProperties,
        promotionalProperty = promotionalProperty,
        selectedCategory = selectedCategory,
        selectedCity = selectedCity,
        onSearchClick = onSearchClick,
        onSavedClick = onSavedClick,
        onAddClick = onAddClick,
        onProfileClick = onProfileClick,
        onCategorySelected = viewModel::onCategorySelected,
        onLike = { property -> viewModel.onLikeClicked(property.documentId ?: property.id) },
        onChangeCity = onChangeCity,
        modifier = modifier
    )
}

@Composable
internal fun HomeContent(
    featuredProperties: List<Property>,
    promotionalProperty: Property?,
    selectedCategory: HomeCategory,
    selectedCity: String? = null,
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
        featuredProperties.map(Property::toFeaturedProperty)
    }

    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { },
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(HomeDims.SECTION_SPACING)
        ) {
            HomeHeader(modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING))
            HomeTitle(modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING))
            HomeSearchBar(onSearchClick = onSearchClick, modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING))
            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (featuredList.isEmpty() && promotionalProperty == null) {
                    EmptyResults(
                        modifier = Modifier.fillMaxSize(),
                        title = HomeStrings.NO_PROPERTIES_TITLE,
                        subtitle = HomeStrings.NO_PROPERTIES_SUBTITLE,
                        city = selectedCity,
                        onChangeCity = onChangeCity
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = HomeDims.SCREEN_PADDING),
                        verticalArrangement = Arrangement.spacedBy(HomeDims.SECTION_SPACING)
                    ) {
                        if (featuredList.isNotEmpty()) {
                            item {
                                FeaturedSection(
                                    properties = featuredList,
                                    onSeeAllClick = onSearchClick,
                                    onPropertyClick = { id ->
                                        selectedProperty = featuredProperties.find {
                                            (it.documentId ?: it.id) == id
                                        }
                                    }
                                )
                            }
                        }

                        if (promotionalProperty != null) {
                            item {
                                PromotionBanner(
                                    promotionalProperty = promotionalProperty,
                                    onClick = { selectedProperty = promotionalProperty },
                                    modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedProperty?.let { property ->
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
                    onLike = { onLike(property) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
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
    RealeTheme {
        HomeContent(
            featuredProperties = PreviewData.sampleProperties,
            promotionalProperty = PreviewData.sampleProperties.firstOrNull(),
            selectedCategory = HomeCategory.BUY,
            onSearchClick = {}
        )
    }
}
