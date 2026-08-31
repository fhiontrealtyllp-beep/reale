package com.realeapp.feature.add.presentation

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerDialog(
    initialLat: String = "",
    initialLng: String = "",
    onDismiss: () -> Unit,
    onConfirm: (latitude: String, longitude: String, city: String?, locality: String?, pincode: String?, address: String?) -> Unit
) {
    val context = LocalContext.current
    val apiKey = remember { readMapApiKey(context) }
    val coroutineScope = rememberCoroutineScope()

    val initialLatLng = remember(initialLat, initialLng) {
        LatLng(
            initialLat.toDoubleOrNull() ?: 12.97,
            initialLng.toDoubleOrNull() ?: 77.75
        )
    }

    var selectedLatLng by remember { mutableStateOf(initialLatLng) }
    var isGeocoding by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLatLng, 15f)
    }

    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.position.target }
            .collect { selectedLatLng = it }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF141C3D),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Pick Location",
                            color = Color(0xFFFBFBFB),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFFFBFBFB)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF141C3D)
                    )
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selected: %.4f, %.4f".format(
                            Locale.US,
                            selectedLatLng.latitude,
                            selectedLatLng.longitude
                        ),
                        color = Color(0xFFFBFBFB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            isGeocoding = true
                            coroutineScope.launch {
                                val geocoded = reverseGeocode(
                                    context,
                                    selectedLatLng.latitude,
                                    selectedLatLng.longitude
                                )
                                onConfirm(
                                    selectedLatLng.latitude.toString(),
                                    selectedLatLng.longitude.toString(),
                                    geocoded?.city,
                                    geocoded?.locality,
                                    geocoded?.pincode,
                                    geocoded?.address
                                )
                                isGeocoding = false
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !isGeocoding,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFDD60D),
                            contentColor = Color(0xFF141C3D)
                        )
                    ) {
                        if (isGeocoding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF141C3D),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Confirm Location",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (!apiKey.isNullOrBlank() && apiKey != "YOUR_API_KEY") {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        ),
                        onMapClick = { latLng ->
                            coroutineScope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.newLatLng(latLng))
                            }
                        }
                    ) {}

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Selected location",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                            .offset(y = (-24).dp)
                    )
                } else {
                    PlaceholderLocationPicker(
                        initialLat = initialLat,
                        initialLng = initialLng,
                        onConfirm = { lat, lng ->
                            selectedLatLng = LatLng(lat, lng)
                            coroutineScope.launch {
                                val geocoded = reverseGeocode(context, lat, lng)
                                onConfirm(
                                    lat.toString(),
                                    lng.toString(),
                                    geocoded?.city,
                                    geocoded?.locality,
                                    geocoded?.pincode,
                                    geocoded?.address
                                )
                                onDismiss()
                            }
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderLocationPicker(
    initialLat: String,
    initialLng: String,
    onConfirm: (lat: Double, lng: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var latitude by remember { mutableStateOf(initialLat) }
    var longitude by remember { mutableStateOf(initialLng) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Google Maps API key not set.",
            color = Color(0xFFFF6B6B),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Set MAPS_API_KEY in AndroidManifest to enable the map picker. You can still enter coordinates manually below.",
            color = Color(0xFFFBFBFB),
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                colors = dialogFieldColors(),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                colors = dialogFieldColors(),
                modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = {
                val lat = latitude.toDoubleOrNull() ?: 0.0
                val lng = longitude.toDoubleOrNull() ?: 0.0
                onConfirm(lat, lng)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFDD60D),
                contentColor = Color(0xFF141C3D)
            )
        ) {
            Text(
                text = "Confirm Coordinates",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFFFBFBFB),
    unfocusedTextColor = Color(0xFFFBFBFB),
    focusedBorderColor = Color(0xFFFDD60D),
    unfocusedBorderColor = Color(0xFF8F9FDC),
    focusedLabelColor = Color(0xFFFDD60D),
    unfocusedLabelColor = Color(0xFF8F9FDC),
    cursorColor = Color(0xFFFDD60D)
)

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

private suspend fun reverseGeocode(
    context: Context,
    latitude: Double,
    longitude: Double
): GeocodedAddress? = withContext(Dispatchers.IO) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        addresses?.firstOrNull()?.toGeocodedAddress()
    } catch (e: Exception) {
        null
    }
}

private fun android.location.Address.toGeocodedAddress(): GeocodedAddress {
    return GeocodedAddress(
        city = locality ?: subAdminArea ?: adminArea,
        locality = subLocality ?: subAdminArea ?: locality,
        pincode = postalCode,
        address = getAddressLine(0) ?: ""
    )
}

private data class GeocodedAddress(
    val city: String?,
    val locality: String?,
    val pincode: String?,
    val address: String?
)
