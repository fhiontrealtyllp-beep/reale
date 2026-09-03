package com.realeapp.feature.search.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.presentation.components.formatIndianPrice
import kotlinx.coroutines.delay

private val RealeMain = Color(0xFF141C3D)
private val RealeCard = Color(0xFF1C2755)
private val RealeYellow = Color(0xFFFDD60D)
private val RealeWhite = Color(0xFFFBFBFB)
private val RealeBlue = Color(0xFF8F9FDC)
private val RealeGrey = Color(0xFF71737E)

private const val AUTO_SLIDE_DELAY_MS = 2000L


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailScreen(
    property: Property,
    onClose: () -> Unit,
    onLike: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = RealeMain,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Property Details",
                        color = RealeWhite,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = RealeWhite
                        )
                    }
                },
                actions = {
                    // Optional favorite action shown when the parent provides like handling.
                    if (onLike != null) {
                        IconButton(onClick = onLike) {
                            Icon(
                                imageVector = if (property.isLiked == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (property.isLiked == true) RealeYellow else RealeWhite
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RealeMain,
                    titleContentColor = RealeWhite,
                    navigationIconContentColor = RealeWhite,
                    actionIconContentColor = RealeWhite
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                // Property image header UI with an optional additional-image count.
                PropertyImageHeader(property = property)
            }

            item {
                // Primary property summary UI: title, price, and location.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = property.title,
                        color = RealeWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = formatIndianPrice(property.price, property.isRentProperty()),
                        color = RealeYellow,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = RealeBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = buildLocationString(property),
                            color = RealeWhite.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            item {
                // Structured overview UI containing the core property attributes.
                OverviewSection(property = property)
            }

            // Description UI is shown only when listing text is available.
            if (!property.description.isNullOrBlank()) {
                item {
                    DetailSection(title = "Description") {
                        Text(
                            text = property.description,
                            color = RealeWhite.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Amenities UI is shown only for listings with selected amenities.
            if (property.amenities.isNotEmpty()) {
                item {
                    DetailSection(title = "Amenities") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            property.amenities.forEach { amenity ->
                                AssistChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            text = amenity.label,
                                            color = RealeWhite,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = RealeCard
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Area measurements UI is shown when at least one area value exists.
            if (property.carpetArea != null || property.builtUpArea != null || property.superBuiltUpArea != null) {
                item {
                    AreasSection(property = property)
                }
            }

            // Location UI shows a map when possible and otherwise an address fallback.
            if ((property.latitude != null && property.longitude != null) || !property.address.isNullOrBlank() || property.city.isNotBlank()) {
                item {
                    MapSection(property = property)
                }
            }

            // Agent contact UI is shown only when a phone number is available.
            if (property.agentPhone.isNotBlank()) {
                item {
                    val context = LocalContext.current
                    DetailSection(title = "Contact Agent") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = property.agentPhone,
                                color = RealeBlue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Button(
                                onClick = { dialAgent(context, property.agentPhone) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RealeYellow,
                                    contentColor = RealeMain
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Call",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PropertyImageHeader(property: Property) {
    val images = remember(property.id, property.images) {
        property.images.filter { it.isNotBlank() }
            .ifEmpty { listOf("https://picsum.photos/seed/${property.id}/400/260") }
    }
    val pagerState = rememberPagerState(pageCount = { images.size })

    // Auto-advance UI: slides to the next image every 2 seconds and loops back to the start.
    if (images.size > 1) {
        LaunchedEffect(pagerState, images.size) {
            while (true) {
                delay(AUTO_SLIDE_DELAY_MS)
                val nextPage = (pagerState.currentPage + 1) % images.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = property.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (images.size > 1) {
            // Page indicator UI reflecting the currently visible image.
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) RealeYellow
                                else RealeWhite.copy(alpha = 0.5f)
                            )
                    )
                }
            }

            Text(
                text = "${pagerState.currentPage + 1}/${images.size}",
                color = RealeWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(RealeMain.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun OverviewSection(property: Property) {
    DetailSection(title = "Overview") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OverviewColumn(
                items = listOfNotNull(
                    property.rentBuy?.let { "Type" to it.label },
                    property.residentialCommercial?.let { "Category" to it.label },
                    property.bedroomType?.let { "Bedrooms" to it.label },
                    property.furnishing?.let { "Furnishing" to it.label }
                ),
                modifier = Modifier.weight(1f)
            )
            OverviewColumn(
                items = listOfNotNull(
                    property.propertyType?.let { "Property" to it.label },
                    property.facing?.let { "Facing" to it.label },
                    property.age?.let { "Age" to it.label },
                    property.status?.let { "Status" to property.capitalizeStatus() }
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OverviewColumn(
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { (label, value) ->
            Column {
                Text(
                    text = label,
                    color = RealeGrey,
                    fontSize = 12.sp
                )
                Text(
                    text = value,
                    color = RealeWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AreasSection(property: Property) {
    DetailSection(title = "Area") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AreaItem(label = "Carpet", value = formatArea(property.carpetArea))
            AreaItem(label = "Built-up", value = formatArea(property.builtUpArea))
            AreaItem(label = "Super Built-up", value = formatArea(property.superBuiltUpArea))
        }
    }
}

@Composable
private fun AreaItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = RealeGrey,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = RealeWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DetailSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = RealeCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = RealeWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

private fun buildLocationString(property: Property): String {
    return buildString {
        append(property.locality)
        if (property.city.isNotBlank()) {
            if (isNotBlank()) append(", ")
            append(property.city)
        }
        if (!property.pincode.isNullOrBlank()) {
            if (isNotBlank()) append(" - ")
            append(property.pincode)
        }
        if (!property.address.isNullOrBlank()) {
            if (isNotBlank()) append("\n")
            append(property.address)
        }
    }
}

@Composable
private fun MapSection(property: Property) {
    val context = LocalContext.current
    val apiKey = remember { readMapApiKey(context) }
    val lat = property.latitude
    val lng = property.longitude

    if (lat != null && lng != null && !apiKey.isNullOrBlank() && apiKey != "YOUR_API_KEY") {
        val propertyLatLng = LatLng(lat, lng)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(propertyLatLng, 15f)
        }
        var isMapLoaded by remember { mutableStateOf(false) }

        DetailSection(title = "Location") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false
                    ),
                    onMapLoaded = { isMapLoaded = true }
                ) {
                    Marker(
                        state = MarkerState(position = propertyLatLng),
                        title = property.title,
                        snippet = buildLocationString(property)
                    )
                }

                // Map loading UI remains visible until Google Maps reports readiness.
                if (!isMapLoaded) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = RealeYellow
                    )
                }
            }
        }
    } else if (!property.address.isNullOrBlank() || property.city.isNotBlank()) {
        // Address-only location UI used when an interactive map cannot be displayed.
        DetailSection(title = "Location") {
            LocationPlaceholder(property = property)
        }
    }
}

@Composable
private fun LocationPlaceholder(property: Property) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = RealeYellow,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = buildLocationString(property),
            color = RealeWhite.copy(alpha = 0.85f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

private fun readMapApiKey(context: Context): String? {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        appInfo.metaData?.getString("com.google.android.geo.API_KEY")
    } catch (e: Exception) {
        null
    }
}

private fun formatArea(area: Double?): String {
    return if (area != null) "${area.toInt()} sq.ft" else "-"
}

private fun Property.capitalizeStatus(): String {
    return status?.replaceFirstChar { it.uppercase() } ?: "-"
}

private fun dialAgent(context: Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:${phone.trim()}")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}
