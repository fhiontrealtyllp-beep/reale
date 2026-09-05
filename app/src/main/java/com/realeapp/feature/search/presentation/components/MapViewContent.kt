package com.realeapp.feature.search.presentation.components

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realeapp.feature.search.domain.model.Property
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary
import com.realeapp.ui.theme.White

@Composable
fun MapViewContent(
    properties: List<Property>,
    onPropertyTap: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val apiKey = remember { readMapApiKey(context) }

    if (properties.isEmpty()) {
        EmptySearchResults(modifier = modifier.fillMaxSize())
        return
    }

    if (apiKey.isNullOrBlank() || apiKey == "YOUR_API_KEY") {
        PlaceholderMapContent(
            properties = properties,
            onPropertyTap = onPropertyTap,
            modifier = modifier
        )
        return
    }

    val first = properties.firstOrNull { it.latitude != null && it.longitude != null }
    val cameraPositionState = rememberCameraPositionState {
        position = if (first != null) {
            CameraPosition.fromLatLngZoom(LatLng(first.latitude!!, first.longitude!!), 12f)
        } else {
            CameraPosition.fromLatLngZoom(LatLng(12.97, 77.75), 10f)
        }
    }

    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(isMapLoaded, properties) {
        if (isMapLoaded) {
            val bounds = buildBounds(properties)
            if (bounds != null) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 80))
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
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
            properties.forEach { property ->
                val lat = property.latitude ?: return@forEach
                val lng = property.longitude ?: return@forEach
                Marker(
                    state = MarkerState(position = LatLng(lat, lng)),
                    title = property.title,
                    snippet = formatIndianPrice(property.price, property.isRentProperty()),
                    onClick = { _ ->
                        onPropertyTap(property)
                        true
                    }
                )
            }
        }

        if (!isMapLoaded) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Accent
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { cameraPositionState.move(CameraUpdateFactory.zoomIn()) },
                containerColor = CardBackground,
                contentColor = TextPrimary,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom in")
            }
            FloatingActionButton(
                onClick = { cameraPositionState.move(CameraUpdateFactory.zoomOut()) },
                containerColor = CardBackground,
                contentColor = TextPrimary,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom out")
            }
        }
    }
}

@Composable
private fun PlaceholderMapContent(
    properties: List<Property>,
    onPropertyTap: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Map view",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Set MAPS_API_KEY in AndroidManifest to enable Google Maps.",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(properties, key = { it.id }) { property ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(32.dp)
                    )
                    Column(
                        modifier = Modifier.clickable { onPropertyTap(property) },
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = property.title,
                            color = White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${property.locality}, ${property.city}",
                            color = White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatIndianPrice(property.price, property.isRentProperty()),
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
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

private fun buildBounds(properties: List<Property>): LatLngBounds? {
    val builder = LatLngBounds.Builder()
    var hasPoint = false
    properties.forEach { property ->
        val lat = property.latitude
        val lng = property.longitude
        if (lat != null && lng != null) {
            builder.include(LatLng(lat, lng))
            hasPoint = true
        }
    }
    return if (hasPoint) builder.build() else null
}
