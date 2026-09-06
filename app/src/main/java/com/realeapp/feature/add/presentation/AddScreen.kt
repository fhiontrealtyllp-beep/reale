package com.realeapp.feature.add.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.MediaScrim
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.OnMediaContent
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary
import com.realeapp.ui.theme.White
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.window.DialogProperties
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.presentation.PropertyDetailScreen
import com.realeapp.ui.components.LoginPrompt
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    startWithAddForm: Boolean = false,
    viewModel: AddViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }

    LaunchedEffect(Unit) {
        // Opened from an external entry point (e.g. Profile "List Your Property") straight into the form.
        if (startWithAddForm) {
            viewModel.onShowAddForm()
        }
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = MainBackground,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CardBackground,
                    contentColor = TextPrimary
                )
            }
        },
        topBar = {
            // Logged-in navigation UI; the add form uses the light-theme header
            // (logo + Save Draft + back + title), the listings view keeps TopAppBar.
            if (uiState.isLoggedIn) {
                if (uiState.isShowingAddForm || uiState.isSubmitSuccess) {
                    AddFormTopBar(
                        onBack = {
                            if (uiState.isSubmitSuccess) {
                                viewModel.onDismissSuccess()
                            } else {
                                viewModel.onHideAddForm()
                            }
                        },
                        onSaveDraft = { }
                    )
                } else {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = AddStrings.TITLE_MY_LISTINGS,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = AddStrings.SUBTITLE_MANAGE_PROPERTIES,
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        actions = {
                            Button(
                                onClick = viewModel::onShowAddForm,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Accent,
                                    contentColor = OnAccent
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = AddStrings.ACTION_ADD_PROPERTY,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MainBackground
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                // Full-screen loading UI while the add feature initializes.
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Accent
                    )
                }

                // Logged-out UI prompting the user to open the login flow.
                !uiState.isLoggedIn -> LoginPrompt(
                    title = AddStrings.LOGIN_PROMPT_TITLE,
                    onLoginClick = onLoginClick,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Property creation form UI.
                uiState.isShowingAddForm -> AddPropertySteps(
                    uiState = uiState,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )

                // Success UI shown after a property is published.
                uiState.isSubmitSuccess -> PropertySuccessScreen(
                    property = uiState.submittedProperty ?: uiState.myProperties.firstOrNull(),
                    onViewListing = { property ->
                        viewModel.onDismissSuccess()
                        selectedProperty = property
                    },
                    onAddAnother = viewModel::onShowAddForm,
                    modifier = Modifier.fillMaxSize()
                )

                // Logged-in UI listing properties created by the current user.
                else -> MyPropertiesContent(
                    properties = uiState.myProperties,
                    isLoading = uiState.isLoadingMyProperties,
                    errorMessage = uiState.myPropertiesError,
                    onRefresh = viewModel::refresh,
                    onPropertyClick = { selectedProperty = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Full-screen property details UI shown after selecting a property.
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private val SuccessGreen = Color(0xFF22C55E)
private val SuccessGreenLight = Color(0xFFDCFCE7)

@Composable
private fun PropertySuccessScreen(
    property: Property?,
    onViewListing: (Property) -> Unit,
    onAddAnother: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(96.dp)
                .background(SuccessGreenLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(56.dp)
            )
        }

        Text(
            text = AddStrings.SUCCESS_TITLE,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = AddStrings.SUCCESS_MESSAGE,
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        if (property != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
            ) {
                if (property.images.isNotEmpty()) {
                    AsyncImage(
                        model = property.images.first(),
                        contentDescription = property.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = property.title,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = listOf(property.locality, property.city)
                                .filter { it.isNotBlank() }
                                .joinToString(", "),
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = successPriceText(property.price),
                        color = Accent,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { property?.let(onViewListing) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = property != null,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = OnAccent
            )
        ) {
            Text(
                text = AddStrings.ACTION_VIEW_LISTING,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        OutlinedButton(
            onClick = onAddAnother,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Accent),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Accent
            )
        ) {
            Text(
                text = AddStrings.ACTION_ADD_ANOTHER_PROPERTY,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private fun successPriceText(price: Double): String {
    if (price <= 0.0) return AddStrings.PLACEHOLDER_DASH
    return when {
        price >= 1_00_00_000 -> AddStrings.RUPEE_PREFIX + (price / 1_00_00_000).let { "%.2f".format(it).trimEnd('0').trimEnd('.') } + AddStrings.CRORE_SUFFIX
        price >= 1_00_000 -> AddStrings.RUPEE_PREFIX + (price / 1_00_000).let { "%.2f".format(it).trimEnd('0').trimEnd('.') } + AddStrings.LAKH_SUFFIX
        else -> AddStrings.RUPEE_PREFIX + NumberFormat.getNumberInstance(Locale(AddStrings.LOCALE_LANGUAGE, AddStrings.LOCALE_COUNTRY)).apply {
            maximumFractionDigits = 0
        }.format(price)
    }
}

private val listingTabs = AddStrings.LISTING_TABS

@Composable
private fun MyPropertiesContent(
    properties: List<Property>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AddStrings.TAB_ALL) }
    var searchQuery by remember { mutableStateOf("") }

    val counts = remember(properties) {
        mapOf(
            AddStrings.TAB_ALL to properties.size,
            AddStrings.TAB_ACTIVE to properties.count { it.isActiveListing() },
            AddStrings.TAB_INACTIVE to properties.count { it.isInactiveListing() },
            AddStrings.TAB_DRAFTS to properties.count { it.isDraftListing() }
        )
    }

    val query = searchQuery.trim().lowercase()
    val filtered = properties.filter { property ->
        val matchesTab = when (selectedTab) {
            AddStrings.TAB_ACTIVE -> property.isActiveListing()
            AddStrings.TAB_INACTIVE -> property.isInactiveListing()
            AddStrings.TAB_DRAFTS -> property.isDraftListing()
            else -> true
        }
        val matchesSearch = query.isEmpty() ||
            property.title.lowercase().contains(query) ||
            property.locality.lowercase().contains(query) ||
            property.city.lowercase().contains(query) ||
            property.propertyType?.label?.lowercase()?.contains(query) == true
        matchesTab && matchesSearch
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ─────────────────────────────
        // TABS
        // ─────────────────────────────
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listingTabs.forEach { tab ->

                    val selected = selectedTab == tab

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selected) Accent
                                else CardBackground
                            )
                            .clickable {
                                selectedTab = tab
                            }
                            .padding(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            )
                    ) {
                        Text(
                            text = "$tab (${counts[tab] ?: 0})",
                            color = if (selected) OnAccent else TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }
        }

        // ─────────────────────────────
        // SEARCH
        // ─────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    placeholder = {
                        Text(AddStrings.SEARCH_LISTINGS_PLACEHOLDER)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = formFieldColors()
                )

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBackground)
                        .clickable {
                            onRefresh()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = AddStrings.CD_REFRESH_FILTERS,
                        tint = TextPrimary
                    )
                }
            }
        }

        // ─────────────────────────────
        // ERROR
        // ─────────────────────────────
        if (!errorMessage.isNullOrBlank()) {
            item {
                Text(
                    text = errorMessage,
                    color = Error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // ─────────────────────────────
        // LOADING
        // ─────────────────────────────
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Accent)
                }
            }
        }

        // ─────────────────────────────
        // EMPTY
        // ─────────────────────────────
        else if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = AddStrings.EMPTY_LISTINGS,
                        color = TextSecondary
                    )
                }
            }
        }

        // ─────────────────────────────
        // LISTINGS
        // ─────────────────────────────
        else {
            items(
                items = filtered,
                key = { it.id }
            ) { property ->

                MyListingCard(
                    property = property,
                    onViewDetails = {
                        onPropertyClick(property)
                    }
                )
            }
        }
    }

}

@Composable
private fun MyListingCard(
    property: Property,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val statusLabel = when {
        property.isDraftListing() -> AddStrings.STATUS_DRAFT
        property.isActiveListing() -> AddStrings.STATUS_ACTIVE
        else -> AddStrings.STATUS_INACTIVE
    }
    val statusColor = when (statusLabel) {
        AddStrings.STATUS_ACTIVE -> SuccessGreen
        AddStrings.STATUS_DRAFT -> Accent
        else -> TextSecondary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .padding(10.dp)
    ) {
        // Photo with status + photo-count badges.
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MainBackground)
        ) {
            if (property.images.isNotEmpty()) {
                AsyncImage(
                    model = property.images.first(),
                    contentDescription = property.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp)
                )
            }
            Text(
                text = statusLabel,
                color = OnBrandContent,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .background(statusColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
            if (property.images.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .background(MediaScrim.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = OnMediaContent,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "${property.images.size}${AddStrings.PHOTOS_SUFFIX}",
                        color = OnMediaContent,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = property.title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = AddStrings.CD_MORE_OPTIONS,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(AddStrings.ACTION_VIEW_DETAILS) },
                            onClick = {
                                menuExpanded = false
                                onViewDetails()
                            }
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = listOf(property.locality, property.city)
                        .filter { it.isNotBlank() }
                        .joinToString(", "),
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = successPriceText(property.price),
                color = Accent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val beds = listingBedCount(property.bedroomType)
                if (beds > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bed,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "$beds${AddStrings.BEDS_SUFFIX}",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                property.builtUpArea?.let { area ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SquareFoot,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = formatIndianNumber(area) + AddStrings.SQ_FT_SUFFIX,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Accent),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = AddStrings.ACTION_EDIT,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent.copy(alpha = 0.15f),
                        contentColor = Accent
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text(
                        text = if (statusLabel == AddStrings.STATUS_INACTIVE) AddStrings.ACTION_REACTIVATE else AddStrings.ACTION_VIEW_DETAILS,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Icon(
                        imageVector = if (statusLabel == AddStrings.STATUS_INACTIVE) {
                            Icons.Default.Refresh
                        } else {
                            Icons.AutoMirrored.Filled.ArrowForward
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

private fun Property.isActiveListing(): Boolean =
    status == null || status.equals(AddStrings.STATUS_LIVE, ignoreCase = true) || status.equals(AddStrings.STATUS_VALUE_ACTIVE, ignoreCase = true)

private fun Property.isDraftListing(): Boolean =
    status.equals(AddStrings.STATUS_VALUE_DRAFT, ignoreCase = true)

private fun Property.isInactiveListing(): Boolean =
    !isActiveListing() && !isDraftListing()

private fun listingBedCount(bedroomType: BedroomType?): Int = when (bedroomType) {
    BedroomType.ONE_RK, BedroomType.ONE_BHK, BedroomType.STUDIO_APARTMENT -> 1
    BedroomType.TWO_BHK -> 2
    BedroomType.THREE_BHK -> 3
    BedroomType.FOUR_BHK -> 4
    BedroomType.FIVE_BHK -> 5
    BedroomType.SIX_BHK -> 6
    BedroomType.SIX_PLUS_BHK -> 7
    null -> 0
}

private fun formatIndianNumber(value: Double): String {
    return NumberFormat.getNumberInstance(Locale(AddStrings.LOCALE_LANGUAGE, AddStrings.LOCALE_COUNTRY)).apply {
        maximumFractionDigits = 0
    }.format(value)
}

// Light-theme header shown while the add-property form (or success view) is open.
@Composable
private fun AddFormTopBar(
    onBack: () -> Unit,
    onSaveDraft: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth(), color = White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AddDims.SCREEN_PADDING,
                    vertical = AddDims.HEADER_VERTICAL_PADDING
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AddStrings.APP_NAME_FIRST,
                    color = BrandBlue,
                    fontSize = AddDims.HEADER_LOGO_FONT_SIZE,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = AddStrings.APP_NAME_ACCENT,
                    color = BrandCoral,
                    fontSize = AddDims.HEADER_LOGO_FONT_SIZE,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.clickable(onClick = onSaveDraft),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = AddStrings.CD_SAVE_DRAFT,
                        tint = BrandBlue,
                        modifier = Modifier.size(AddDims.SAVE_DRAFT_ICON_SIZE)
                    )
                    Spacer(modifier = Modifier.width(AddDims.SAVE_DRAFT_ICON_TEXT_SPACING))
                    Text(
                        text = AddStrings.ACTION_SAVE_DRAFT,
                        color = BrandBlue,
                        fontSize = AddDims.SAVE_DRAFT_FONT_SIZE,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = AddStrings.CD_BACK,
                    tint = Black,
                    modifier = Modifier
                        .size(AddDims.HEADER_BACK_ICON_SIZE)
                        .clickable(onClick = onBack)
                )
                Spacer(modifier = Modifier.width(AddDims.HEADER_ROW_SPACING))
                Column {
                    Text(
                        text = AddStrings.TITLE_ADD_PROPERTY,
                        color = Black,
                        fontSize = AddDims.HEADER_TITLE_FONT_SIZE,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = AddStrings.ADD_FORM_SUBTITLE,
                        color = HomeTextSecondary,
                        fontSize = AddDims.HEADER_SUBTITLE_FONT_SIZE
                    )
                }
            }
        }
    }
}
