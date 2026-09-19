package com.realeapp.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.feature.search.presentation.PropertyResultCard
import com.realeapp.ui.components.BOTTOM_NAV_CLEARANCE
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.ControlAccent
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.OnControlAccent
import com.realeapp.ui.theme.White
import com.realeapp.ui.theme.RealeTheme
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

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

@Composable
internal fun MyListingsScreen(
    onBack: () -> Unit,
    onAddProperty: () -> Unit = {},
    onViewDetails: (MyListing) -> Unit = {},
    onMoreOptions: (MyListing) -> Unit = {},
    onViewEnquiries: (String) -> Unit = {},
    viewModel: MyListingsViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    var selectedFilter by rememberSaveable { mutableStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedListing by remember { mutableStateOf<MyListing?>(null) }

    val allListings = uiState.listings
    val filteredListings = remember(allListings, selectedFilter, searchQuery) {
        allListings.filter { listing ->
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
                listings = allListings,
                selectedIndex = selectedFilter,
                onSelect = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(MyListingsDims.SECTION_SPACING))

            ListingsSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(MyListingsDims.SECTION_SPACING))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ControlAccent,
                            modifier = Modifier.size(MyListingsDims.LOADING_SIZE)
                        )
                    }
                }
                uiState.errorMessage != null -> {
                    ErrorMessage(
                        message = uiState.errorMessage.orEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                filteredListings.isEmpty() -> {
                    EmptyListings(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(MyListingsDims.CARD_SPACING),
                        // Extra bottom space so the last card clears the floating glass nav capsule.
                        contentPadding = PaddingValues(bottom = MyListingsDims.SCREEN_PADDING + BOTTOM_NAV_CLEARANCE)
                    ) {
                        items(filteredListings, key = { it.id }) { listing ->
                            ListingCard(
                                listing = listing,
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
    }

    // Full-screen property details UI shown after selecting a listing.
    selectedListing?.let { listing ->
        val property = remember(listing.id, uiState.properties) {
            uiState.properties.find { it.id == listing.id } ?: listing.toProperty()
        }
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
                    enquiryCount = listing.enquiries,
                    onViewEnquiries = {
                        selectedListing = null
                        onViewEnquiries(listing.id)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun EmptyListings(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = MyListingsStrings.EMPTY_TITLE,
            color = Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(MyListingsDims.CARD_CONTENT_SPACING))
        Text(
            text = MyListingsStrings.EMPTY_SUBTITLE,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Black,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(MyListingsDims.SCREEN_PADDING)
        )
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
        carpetArea = sqft.toDouble(),
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
                containerColor = ControlAccent,
                contentColor = OnControlAccent
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
                    .background(if (selected) ControlAccent else White)
                    .border(
                        BorderStroke(
                            MyListingsDims.CHIP_BORDER_WIDTH,
                            if (selected) ControlAccent else HomeSearchBarBorder
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
                    color = if (selected) OnControlAccent else Black,
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


    }
}

@Composable
private fun ListingCard(
    listing: MyListing,
    onViewDetails: () -> Unit,
    onMoreOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    PropertyResultCard(
        property = listing.toProperty(),
        onClick = onViewDetails,
        modifier = modifier,
        trailingContent = {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = MyListingsStrings.CD_MORE,
                tint = HomeTextSecondary,
                modifier = Modifier
                    .size(MyListingsDims.MORE_ICON_SIZE)
                    .clickable(onClick = onMoreOptions)
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MyListingsScreenPreview() {
    RealeTheme {
        MyListingsScreen(
            onBack = {},
            viewModel = MyListingsViewModel(
                getMyPropertiesUseCase = object : com.realeapp.feature.add.domain.usecase.GetMyPropertiesUseCase {
                    override suspend fun invoke(userId: String): com.realeapp.feature.search.domain.utils.Result<List<com.realeapp.feature.search.domain.model.Property>> {
                        return com.realeapp.feature.search.domain.utils.Result.Success(emptyList())
                    }
                },
                getEnquiryCountsForPropertiesUseCase = object : com.realeapp.feature.search.domain.usecase.GetEnquiryCountsForPropertiesUseCase {
                    override suspend fun invoke(propertyIds: List<String>): com.realeapp.feature.search.domain.utils.Result<Map<String, Int>> {
                        return com.realeapp.feature.search.domain.utils.Result.Success(emptyMap())
                    }
                },
                userSession = object : com.realeapp.feature.search.data.session.UserSession {
                    override val user: kotlinx.coroutines.flow.StateFlow<com.realeapp.feature.auth.domain.model.User?> = kotlinx.coroutines.flow.MutableStateFlow(null)
                    override fun getUserId(): String? = "u1"
                    override fun getUser(): com.realeapp.feature.auth.domain.model.User? = null
                    override fun setUser(user: com.realeapp.feature.auth.domain.model.User?) {}
                    override fun clear() {}
                }
            )
        )
    }
}
