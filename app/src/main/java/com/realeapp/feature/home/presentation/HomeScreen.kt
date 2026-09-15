package com.realeapp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.presentation.HomeCategory
import com.realeapp.feature.auth.presentation.LoginPromptDialog
import com.realeapp.feature.search.presentation.SearchViewModel
import com.realeapp.ui.preview.PreviewData
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.components.GenericLoader
import com.realeapp.ui.theme.RealeTheme
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.realeapp.feature.search.presentation.SearchStrings

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

    if (uiState.isLoading) {
        GenericLoader(modifier = modifier)
    } else {
        HomeContent(
            featuredProperties = featuredProperties,
            promotionalProperties = promotionalProperties,
            selectedCategory = selectedCategory,
            selectedCity = selectedCity,
            onSearchClick = onSearchClick,
            onSavedClick = onSavedClick,
            onAddClick = onAddClick,
            onProfileClick = onProfileClick,
            onCategorySelected = viewModel::onCategorySelected,
            onLike = { property ->
                viewModel.onLikeClicked(property.documentId ?: property.id)
                val message = if (property.isLiked == true) {
                    String.format(SearchStrings.REMOVED_FROM_SAVED_TOAST_FORMAT, property.title)
                } else {
                    String.format(SearchStrings.SAVED_TOAST_FORMAT, property.title)
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            onChangeCity = onChangeCity,
            modifier = modifier
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

@Composable
internal fun HomeContent(
    featuredProperties: List<Property>,
    promotionalProperties: List<Property>,
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
    val propertyById = remember(featuredProperties) {
        featuredProperties.associateBy { it.documentId ?: it.id }
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
            HomeHeaderSection(
                onSearchClick = onSearchClick,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )

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
                modifier = Modifier.weight(1f)
            )
        }
    }

    selectedProperty?.let { property ->
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
    RealeTheme {
        HomeContent(
            featuredProperties = PreviewData.sampleProperties,
            promotionalProperties = PreviewData.sampleProperties.take(2),
            selectedCategory = HomeCategory.RENT,
            onSearchClick = {}
        )
    }
}
