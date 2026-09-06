package com.realeapp.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.MediaScrim
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.OnMediaContent
import com.realeapp.ui.theme.VerifiedGreen
import com.realeapp.ui.theme.White
import java.text.NumberFormat
import java.util.Locale

internal enum class ListingStatus { ACTIVE, INACTIVE, DRAFT }

internal data class MyListing(
    val id: String,
    val imageUrl: String,
    val title: String,
    val location: String,
    val type: String,
    val price: Double,
    val beds: Int?,
    val baths: Int?,
    val sqft: Int,
    val views: Int,
    val enquiries: Int,
    val shortlisted: Int,
    val photoCount: Int,
    val status: ListingStatus
)

// Sample listings until a "my properties" backend query exists.
private val sampleListings = listOf(
    MyListing(
        id = "1",
        imageUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=400&q=80",
        title = "3 BHK Premium Villa",
        location = "Porvorim, Goa",
        type = "Villa",
        price = 18_500_000.0,
        beds = 3,
        baths = 3,
        sqft = 2400,
        views = 245,
        enquiries = 18,
        shortlisted = 3,
        photoCount = 12,
        status = ListingStatus.ACTIVE
    ),
    MyListing(
        id = "2",
        imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=400&q=80",
        title = "2 BHK Sea View Apartment",
        location = "Miramar, Goa",
        type = "Apartment",
        price = 12_500_000.0,
        beds = 2,
        baths = 2,
        sqft = 1200,
        views = 189,
        enquiries = 12,
        shortlisted = 5,
        photoCount = 10,
        status = ListingStatus.ACTIVE
    ),
    MyListing(
        id = "3",
        imageUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=400&q=80",
        title = "3 BHK Apartment",
        location = "Taleigao, Goa",
        type = "Apartment",
        price = 9_800_000.0,
        beds = 3,
        baths = 3,
        sqft = 1650,
        views = 320,
        enquiries = 25,
        shortlisted = 8,
        photoCount = 14,
        status = ListingStatus.ACTIVE
    ),
    MyListing(
        id = "4",
        imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=400&q=80",
        title = "Residential Plot",
        location = "Assagao, North Goa",
        type = "Plot",
        price = 7_500_000.0,
        beds = null,
        baths = null,
        sqft = 5000,
        views = 112,
        enquiries = 6,
        shortlisted = 2,
        photoCount = 8,
        status = ListingStatus.INACTIVE
    )
)

@Composable
internal fun MyListingsScreen(
    onBack: () -> Unit,
    onAddProperty: () -> Unit = {},
    onEditListing: (MyListing) -> Unit = {},
    onViewDetails: (MyListing) -> Unit = {},
    onMoreOptions: (MyListing) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by rememberSaveable { mutableStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedListing by remember { mutableStateOf<MyListing?>(null) }

    val filteredListings = remember(selectedFilter, searchQuery) {
        sampleListings.filter { listing ->
            val matchesFilter = when (selectedFilter) {
                1 -> listing.status == ListingStatus.ACTIVE
                2 -> listing.status == ListingStatus.INACTIVE
                3 -> listing.status == ListingStatus.DRAFT
                else -> true
            }
            val query = searchQuery.trim()
            val matchesQuery = query.isEmpty() ||
                listing.title.contains(query, ignoreCase = true) ||
                listing.location.contains(query, ignoreCase = true) ||
                listing.type.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = MyListingsDims.SCREEN_PADDING)
        ) {
            Spacer(modifier = Modifier.height(MyListingsDims.SECTION_SPACING))

            MyListingsTopBar(onBack = onBack, onAddProperty = onAddProperty)

            Spacer(modifier = Modifier.height(MyListingsDims.SECTION_SPACING))

            StatusFilterChips(
                listings = sampleListings,
                selectedIndex = selectedFilter,
                onSelect = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(MyListingsDims.SECTION_SPACING))

            ListingsSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(MyListingsDims.SECTION_SPACING))

            if (filteredListings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = MyListingsStrings.EMPTY_TITLE,
                        color = HomeTextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MyListingsDims.CARD_SPACING),
                    contentPadding = PaddingValues(bottom = MyListingsDims.SCREEN_PADDING)
                ) {
                    items(filteredListings, key = { it.id }) { listing ->
                        ListingCard(
                            listing = listing,
                            onEdit = { onEditListing(listing) },
                            onViewDetails = {
                                selectedListing = listing
                                onViewDetails(listing)
                            },
                            onMoreOptions = { onMoreOptions(listing) }
                        )
                    }
                }
            }
        }
    }

    // Full-screen property details UI shown after selecting a listing.
    selectedListing?.let { listing ->
        val property = remember(listing.id) { listing.toProperty() }
        Dialog(
            onDismissRequest = { selectedListing = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = AppBackground
            ) {
                PropertyDetailScreen(
                    property = property,
                    onClose = { selectedListing = null },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun MyListing.toProperty(): Property {
    val locationParts = location.split(",").map { it.trim() }
    val locality = locationParts.firstOrNull().orEmpty()
    val city = locationParts.drop(1).firstOrNull().orEmpty()
    return Property(
        id = id,
        userId = "",
        title = title,
        description = "",
        price = price,
        city = city,
        locality = locality,
        images = listOfNotNull(imageUrl.takeIf { it.isNotBlank() }),
        bathrooms = baths,
        bedroomType = beds?.toBedroomType(),
        propertyType = PropertyType.entries.find { it.label.equals(type, ignoreCase = true) },
        builtUpArea = sqft.toDouble(),
        status = status.name
    )
}

private fun Int.toBedroomType(): BedroomType = when (this) {
    1 -> BedroomType.ONE_BHK
    2 -> BedroomType.TWO_BHK
    3 -> BedroomType.THREE_BHK
    4 -> BedroomType.FOUR_BHK
    5 -> BedroomType.FIVE_BHK
    6 -> BedroomType.SIX_BHK
    else -> BedroomType.SIX_PLUS_BHK
}

@Composable
private fun MyListingsTopBar(
    onBack: () -> Unit,
    onAddProperty: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = MyListingsStrings.CD_BACK,
            tint = Black,
            modifier = Modifier
                .size(MyListingsDims.BACK_ICON_SIZE)
                .clickable(onClick = onBack)
        )

        Spacer(modifier = Modifier.width(MyListingsDims.CARD_CONTENT_SPACING))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = MyListingsStrings.SCREEN_TITLE,
                color = Black,
                fontSize = MyListingsDims.TOP_BAR_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(MyListingsDims.TOP_BAR_TITLE_SPACING))
            Text(
                text = MyListingsStrings.SCREEN_SUBTITLE,
                color = HomeTextSecondary,
                fontSize = MyListingsDims.TOP_BAR_SUBTITLE_FONT_SIZE,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        TextButton(
            onClick = onAddProperty,
            modifier = Modifier.height(MyListingsDims.ADD_BUTTON_HEIGHT),
            shape = RoundedCornerShape(MyListingsDims.ADD_BUTTON_CORNER_RADIUS),
            colors = ButtonDefaults.textButtonColors(
                containerColor = BrandBlue,
                contentColor = OnBrandContent
            ),
            contentPadding = PaddingValues(horizontal = MyListingsDims.ADD_BUTTON_HORIZONTAL_PADDING)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = MyListingsStrings.CD_ADD_PROPERTY,
                modifier = Modifier.size(MyListingsDims.ADD_BUTTON_ICON_SIZE)
            )
            Spacer(modifier = Modifier.width(MyListingsDims.ADD_BUTTON_ICON_TEXT_SPACING))
            Text(
                text = MyListingsStrings.ADD_PROPERTY,
                fontSize = MyListingsDims.ADD_BUTTON_FONT_SIZE,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatusFilterChips(
    listings: List<MyListing>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = listOf(
        MyListingsStrings.FILTER_ALL to listings.size,
        MyListingsStrings.FILTER_ACTIVE to listings.count { it.status == ListingStatus.ACTIVE },
        MyListingsStrings.FILTER_INACTIVE to listings.count { it.status == ListingStatus.INACTIVE },
        MyListingsStrings.FILTER_DRAFTS to listings.count { it.status == ListingStatus.DRAFT }
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MyListingsDims.CHIP_SPACING)
    ) {
        items(labels.size) { index ->
            val (label, count) = labels[index]
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(MyListingsDims.CHIP_CORNER_RADIUS))
                    .background(if (selected) BrandBlue else White)
                    .border(
                        BorderStroke(
                            MyListingsDims.CHIP_BORDER_WIDTH,
                            if (selected) BrandBlue else HomeSearchBarBorder
                        ),
                        RoundedCornerShape(MyListingsDims.CHIP_CORNER_RADIUS)
                    )
                    .clickable { onSelect(index) }
                    .padding(
                        horizontal = MyListingsDims.CHIP_HORIZONTAL_PADDING,
                        vertical = MyListingsDims.CHIP_VERTICAL_PADDING
                    )
            ) {
                Text(
                    text = "$label ($count)",
                    color = if (selected) OnBrandContent else Black,
                    fontSize = MyListingsDims.CHIP_FONT_SIZE,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun ListingsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MyListingsDims.SEARCH_FILTER_SPACING)
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(MyListingsDims.SEARCH_HEIGHT),
            shape = RoundedCornerShape(MyListingsDims.SEARCH_CORNER_RADIUS),
            color = White,
            border = BorderStroke(MyListingsDims.SEARCH_BORDER_WIDTH, HomeSearchBarBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MyListingsDims.CARD_CONTENT_SPACING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = MyListingsStrings.CD_SEARCH,
                    tint = HomeTextSecondary,
                    modifier = Modifier.size(MyListingsDims.SEARCH_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(MyListingsDims.SEARCH_CONTENT_SPACING))
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Black,
                        fontSize = MyListingsDims.SEARCH_FONT_SIZE
                    ),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = MyListingsStrings.SEARCH_HINT,
                                color = HomeTextSecondary,
                                fontSize = MyListingsDims.SEARCH_FONT_SIZE,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .size(MyListingsDims.FILTER_BUTTON_SIZE)
                .clip(RoundedCornerShape(MyListingsDims.FILTER_BUTTON_CORNER_RADIUS))
                .border(
                    BorderStroke(MyListingsDims.SEARCH_BORDER_WIDTH, HomeSearchBarBorder),
                    RoundedCornerShape(MyListingsDims.FILTER_BUTTON_CORNER_RADIUS)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = MyListingsStrings.CD_FILTER,
                tint = Black,
                modifier = Modifier.size(MyListingsDims.FILTER_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun ListingCard(
    listing: MyListing,
    onEdit: () -> Unit,
    onViewDetails: () -> Unit,
    onMoreOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MyListingsDims.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = MyListingsDims.CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(MyListingsDims.CARD_PADDING)
        ) {
            ListingImage(listing = listing)

            Spacer(modifier = Modifier.width(MyListingsDims.CARD_CONTENT_SPACING))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = listing.title,
                        color = Black,
                        fontSize = MyListingsDims.TITLE_FONT_SIZE,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = MyListingsStrings.CD_MORE,
                        tint = HomeTextSecondary,
                        modifier = Modifier
                            .size(MyListingsDims.MORE_ICON_SIZE)
                            .clickable(onClick = onMoreOptions)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = HomeTextSecondary,
                        modifier = Modifier.size(MyListingsDims.LOCATION_ICON_SIZE)
                    )
                    Spacer(modifier = Modifier.width(MyListingsDims.LOCATION_ICON_TEXT_SPACING))
                    Text(
                        text = listing.location,
                        color = HomeTextSecondary,
                        fontSize = MyListingsDims.LOCATION_FONT_SIZE,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(MyListingsDims.DETAIL_LINE_SPACING))

                Text(
                    text = formatIndianPrice(listing.price),
                    color = BrandBlue,
                    fontSize = MyListingsDims.PRICE_FONT_SIZE,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(MyListingsDims.DETAIL_LINE_SPACING))

                ListingSpecs(listing = listing)

                ListingStats(listing = listing)

                Spacer(modifier = Modifier.height(MyListingsDims.ACTION_ROW_TOP_SPACING))

                ListingActions(
                    listing = listing,
                    onEdit = onEdit,
                    onViewDetails = onViewDetails
                )
            }
        }
    }
}

@Composable
private fun ListingImage(listing: MyListing, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(MyListingsDims.IMAGE_WIDTH)
            .fillMaxHeight()
            .clip(RoundedCornerShape(MyListingsDims.IMAGE_CORNER_RADIUS))
    ) {
        AsyncImage(
            model = listing.imageUrl,
            contentDescription = MyListingsStrings.CD_PROPERTY_IMAGE,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Status badge overlaid at the top-left of the image.
        val (statusLabel, statusColor) = when (listing.status) {
            ListingStatus.ACTIVE -> MyListingsStrings.STATUS_ACTIVE to VerifiedGreen
            ListingStatus.INACTIVE -> MyListingsStrings.STATUS_INACTIVE to HomeTextSecondary
            ListingStatus.DRAFT -> MyListingsStrings.STATUS_DRAFT to HomeTextSecondary
        }
        Text(
            text = statusLabel,
            color = statusColor,
            fontSize = MyListingsDims.STATUS_BADGE_FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(MyListingsDims.STATUS_BADGE_PADDING)
                .clip(RoundedCornerShape(MyListingsDims.STATUS_BADGE_CORNER_RADIUS))
                .background(White)
                .padding(
                    horizontal = MyListingsDims.STATUS_BADGE_HORIZONTAL_PADDING,
                    vertical = MyListingsDims.STATUS_BADGE_VERTICAL_PADDING
                )
        )

        // Photo-count badge overlaid at the bottom-left of the image.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MyListingsDims.PHOTOS_BADGE_ICON_TEXT_SPACING),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(MyListingsDims.PHOTOS_BADGE_PADDING)
                .clip(RoundedCornerShape(MyListingsDims.PHOTOS_BADGE_CORNER_RADIUS))
                .background(MediaScrim.copy(alpha = 0.6f))
                .padding(
                    horizontal = MyListingsDims.PHOTOS_BADGE_HORIZONTAL_PADDING,
                    vertical = MyListingsDims.PHOTOS_BADGE_VERTICAL_PADDING
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoLibrary,
                contentDescription = MyListingsStrings.CD_PHOTOS,
                tint = OnMediaContent,
                modifier = Modifier.size(MyListingsDims.PHOTOS_BADGE_ICON_SIZE)
            )
            Text(
                text = "${listing.photoCount} ${MyListingsStrings.PHOTOS_LABEL}",
                color = OnMediaContent,
                fontSize = MyListingsDims.PHOTOS_BADGE_FONT_SIZE
            )
        }
    }
}

@Composable
private fun ListingSpecs(listing: MyListing, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MyListingsDims.SPEC_ITEM_SPACING)
    ) {
        listing.beds?.let {
            SpecItem(icon = Icons.Outlined.KingBed, text = "$it ${MyListingsStrings.BEDS_LABEL}")
        }
        listing.baths?.let {
            SpecItem(icon = Icons.Filled.Bathtub, text = "$it ${MyListingsStrings.BATHS_LABEL}")
        }
        SpecItem(
            icon = Icons.Outlined.SquareFoot,
            text = "${NumberFormat.getNumberInstance(Locale.getDefault()).format(listing.sqft)} ${MyListingsStrings.SQFT_LABEL}"
        )
    }
}

@Composable
private fun SpecItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MyListingsDims.SPEC_ICON_TEXT_SPACING)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HomeTextSecondary,
            modifier = Modifier.size(MyListingsDims.SPEC_ICON_SIZE)
        )
        Text(
            text = text,
            color = HomeTextSecondary,
            fontSize = MyListingsDims.SPEC_FONT_SIZE,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun ListingStats(listing: MyListing, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MyListingsDims.STATS_VERTICAL_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(value = listing.views, label = MyListingsStrings.STAT_VIEWS, modifier = Modifier.weight(1f))
        VerticalDivider(
            modifier = Modifier.height(MyListingsDims.STAT_DIVIDER_HEIGHT),
            thickness = MyListingsDims.STAT_DIVIDER_WIDTH,
            color = HomeSearchBarBorder
        )
        StatItem(value = listing.enquiries, label = MyListingsStrings.STAT_ENQUIRIES, modifier = Modifier.weight(1f))
        VerticalDivider(
            modifier = Modifier.height(MyListingsDims.STAT_DIVIDER_HEIGHT),
            thickness = MyListingsDims.STAT_DIVIDER_WIDTH,
            color = HomeSearchBarBorder
        )
        StatItem(value = listing.shortlisted, label = MyListingsStrings.STAT_SHORTLISTED, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatItem(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value",
            color = Black,
            fontSize = MyListingsDims.STAT_VALUE_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(MyListingsDims.STAT_SPACING))
        Text(
            text = label,
            color = HomeTextSecondary,
            fontSize = MyListingsDims.STAT_LABEL_FONT_SIZE,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun ListingActions(
    listing: MyListing,
    onEdit: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MyListingsDims.ACTION_BUTTON_SPACING)
    ) {
        // Outlined Edit button.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .height(MyListingsDims.ACTION_BUTTON_HEIGHT)
                .clip(RoundedCornerShape(MyListingsDims.ACTION_BUTTON_CORNER_RADIUS))
                .border(
                    BorderStroke(MyListingsDims.ACTION_BUTTON_BORDER_WIDTH, BrandBlue),
                    RoundedCornerShape(MyListingsDims.ACTION_BUTTON_CORNER_RADIUS)
                )
                .clickable(onClick = onEdit)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = MyListingsStrings.CD_EDIT,
                tint = BrandBlue,
                modifier = Modifier.size(MyListingsDims.ACTION_BUTTON_ICON_SIZE)
            )
            Spacer(modifier = Modifier.width(MyListingsDims.ACTION_BUTTON_ICON_TEXT_SPACING))
            Text(
                text = MyListingsStrings.ACTION_EDIT,
                color = BrandBlue,
                fontSize = MyListingsDims.ACTION_BUTTON_FONT_SIZE,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
        }

        // Filled light-blue primary action: View Details for active, Reactivate for inactive.
        val isInactive = listing.status == ListingStatus.INACTIVE
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .height(MyListingsDims.ACTION_BUTTON_HEIGHT)
                .clip(RoundedCornerShape(MyListingsDims.ACTION_BUTTON_CORNER_RADIUS))
                .background(BrandBlue.copy(alpha = 0.1f))
                .clickable(onClick = onViewDetails)
        ) {
            if (isInactive) {
                Icon(
                    imageVector = Icons.Outlined.Autorenew,
                    contentDescription = MyListingsStrings.CD_REACTIVATE,
                    tint = BrandBlue,
                    modifier = Modifier.size(MyListingsDims.ACTION_BUTTON_ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(MyListingsDims.ACTION_BUTTON_ICON_TEXT_SPACING))
            }
            Text(
                text = if (isInactive) MyListingsStrings.ACTION_REACTIVATE else MyListingsStrings.ACTION_VIEW_DETAILS,
                color = BrandBlue,
                fontSize = MyListingsDims.ACTION_BUTTON_FONT_SIZE,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
            if (!isInactive) {
                Spacer(modifier = Modifier.width(MyListingsDims.ACTION_BUTTON_ICON_TEXT_SPACING))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = MyListingsStrings.CD_VIEW_DETAILS,
                    tint = BrandBlue,
                    modifier = Modifier.size(MyListingsDims.ACTION_BUTTON_ICON_SIZE)
                )
            }
        }
    }
}
