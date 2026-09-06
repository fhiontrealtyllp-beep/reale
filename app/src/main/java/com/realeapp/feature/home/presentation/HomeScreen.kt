package com.realeapp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realeapp.ui.theme.White

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSavedClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val featuredProperties = remember { sampleFeaturedProperties() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { },
        containerColor = White,
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
                    properties = featuredProperties,
                    onSeeAllClick = onSearchClick
                )
            }
            item { PromotionBanner(modifier = Modifier.padding(horizontal = HomeDims.SCREEN_PADDING)) }
        }
    }
}

private fun sampleFeaturedProperties(): List<FeaturedProperty> = listOf(
    FeaturedProperty(
        id = "featured-1",
        imageUrl = "https://images.unsplash.com/photo-1600596542815-86d7f88998bb?w=800&q=80",
        price = 1_85_00_000.0,
        title = "4 BHK Villa",
        location = "Porvorim, Goa",
        beds = 4,
        baths = 4,
        sqft = 2800
    ),
    FeaturedProperty(
        id = "featured-2",
        imageUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800&q=80",
        price = 95_00_000.0,
        title = "2 BHK Apartment",
        location = "Panaji, Goa",
        beds = 2,
        baths = 2,
        sqft = 1200
    ),
    FeaturedProperty(
        id = "featured-3",
        imageUrl = "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=800&q=80",
        price = 2_10_00_000.0,
        title = "3 BHK Villa",
        location = "Anjuna, Goa",
        beds = 3,
        baths = 3,
        sqft = 2200
    )
)
