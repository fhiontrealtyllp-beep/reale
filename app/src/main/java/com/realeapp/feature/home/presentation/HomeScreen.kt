package com.realeapp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.presentation.SearchViewModel
import com.realeapp.ui.theme.AppBackground
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSavedClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel()
) {
    val featuredProperties by viewModel.featuredProperties.collectAsStateWithLifecycle()
    val featuredList = remember(featuredProperties) {
        featuredProperties.map(Property::toFeaturedProperty)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { },
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = HomeDims.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(HomeDims.SECTION_SPACING)
        ) {
            item { HomeHeader(modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)) }
            item { HomeTitle(modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)) }
            item { HomeSearchBar(onSearchClick = onSearchClick, modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)) }
            item { CategoryChips(modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)) }
            item {
                FeaturedSection(
                    properties = featuredList,
                    onSeeAllClick = onSearchClick
                )
            }
            item { PromotionBanner(modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)) }
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
