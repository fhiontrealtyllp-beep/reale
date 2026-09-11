package com.realeapp.feature.add.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.MyLocation
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
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.realeapp.core.location.CurrentLocationProvider
import com.realeapp.util.Logger
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.MapMarker
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.RealeTheme

private const val TAG = "LocationPickerDialog"
private val DEFAULT_FALLBACK_LOCATION = LatLng(20.5937, 78.9629)

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

    val hasInitialCoordinates = initialLat.isNotBlank() && initialLng.isNotBlank()
    val initialLatLng: LatLng? = remember(initialLat, initialLng) {
        if (hasInitialCoordinates) {
            LatLng(
                initialLat.toDoubleOrNull() ?: 0.0,
                initialLng.toDoubleOrNull() ?: 0.0
            )
        } else {
            null
        }
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(initialLatLng) }
    var isGeocoding by remember { mutableStateOf(false) }
    var isLocating by remember { mutableStateOf(initialLatLng == null) }
    var selectedGeocodedAddress by remember { mutableStateOf<GeocodedAddress?>(null) }

    val initialZoom = if (initialLatLng != null) 15f else 4f
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLatLng ?: DEFAULT_FALLBACK_LOCATION, initialZoom)
    }

    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.position.target }
            .collect { target ->
                if (!isLocating) {
                    selectedLatLng = target
                }
            }
    }

    LaunchedEffect(selectedLatLng, isLocating) {
        if (isLocating) return@LaunchedEffect
        val latLng = selectedLatLng ?: return@LaunchedEffect
        Logger.d(TAG, "Selected location changed to $latLng, waiting to reverse geocode")
        delay(300)
        val geocoded = reverseGeocode(context, latLng.latitude, latLng.longitude)
        if (latLng == selectedLatLng) {
            Logger.d(TAG, "Reverse geocoded selected location: $geocoded")
            selectedGeocodedAddress = geocoded
        }
    }

    val requestCurrentLocation: () -> Unit = {
        coroutineScope.launch {
            Logger.d(TAG, "User requested current location")
            navigateToCurrentLocation(
                context = context,
                cameraPositionState = cameraPositionState,
                setLocating = { isLocating = it },
                onLocation = { latLng ->
                    Logger.d(TAG, "Current location found: $latLng")
                    selectedLatLng = latLng
                },
                onError = { message ->
                    Logger.w(TAG, "Current location error: $message")
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }
        Logger.d(TAG, "Permission result: granted=$granted, permissions=$permissions")
        if (granted) {
            requestCurrentLocation()
        } else {
            Toast.makeText(context, AddStrings.LOCATION_PERMISSION_REQUIRED, Toast.LENGTH_SHORT).show()
            isLocating = false
        }
    }

    LaunchedEffect(Unit) {
        Logger.d(TAG, "LocationPickerDialog opened. initial=$initialLatLng, hasApiKey=${!apiKey.isNullOrBlank()}")
        if (initialLatLng == null) {
            val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            Logger.d(TAG, "Initial permission check: fine=$fineGranted, coarse=$coarseGranted")
            if (fineGranted || coarseGranted) {
                requestCurrentLocation()
            } else {
                Logger.d(TAG, "No permission, launching permission request")
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            Logger.d(TAG, "Using provided initial coordinates, no auto-fetch needed")
        }
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
            containerColor = AppBackground,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = AddStrings.PICK_LOCATION_TITLE,
                            color = Black,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = AddStrings.CD_CLOSE,
                                tint = Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppBackground
                    )
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp,16.dp,16.dp,   60.dp)
                ) {
                    val selectedAddress = selectedGeocodedAddress?.address
                        ?.takeIf { it.isNotBlank() }
                        ?: AddStrings.SELECTED_LOCATION_FALLBACK
                    Text(
                        text = selectedAddress,
                        color = Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val latLng = selectedLatLng ?: return@Button
                            Logger.d(TAG, "Confirming location: $latLng")
                            isGeocoding = true
                            coroutineScope.launch {
                                val geocoded = reverseGeocode(context, latLng.latitude, latLng.longitude)
                                onConfirm(
                                    latLng.latitude.toString(),
                                    latLng.longitude.toString(),
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
                        enabled = selectedLatLng != null && !isGeocoding && !isLocating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = OnBrandContent
                        )
                    ) {
                        if (isGeocoding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = OnBrandContent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = AddStrings.ACTION_CONFIRM_LOCATION,
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
                if (apiKey.isNullOrBlank() || apiKey == AddStrings.MAPS_API_KEY_PLACEHOLDER) {
                    Logger.w(TAG, "No valid Maps API key, showing placeholder")
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
                } else {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            myLocationButtonEnabled = false
                        ),
                        onMapLoaded = { Logger.d(TAG, "GoogleMap loaded") },
                        onMapClick = { latLng ->
                            Logger.d(TAG, "Map clicked: $latLng")
                            coroutineScope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.newLatLng(latLng))
                            }
                        }
                    ) {}

                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = AddStrings.CD_SELECTED_LOCATION,
                        tint = MapMarker,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                            .offset(y = (-24).dp)
                    )

                    SmallFloatingActionButton(
                        onClick = {
                            if (isLocating) return@SmallFloatingActionButton
                            Logger.d(TAG, "My location button clicked")

                            val fineGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                            val coarseGranted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (fineGranted || coarseGranted) {
                                requestCurrentLocation()
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 16.dp, end = 16.dp),
                        containerColor = BrandBlue,
                        contentColor = OnBrandContent
                    ) {
                        if (isLocating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = OnBrandContent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = AddStrings.CD_MY_LOCATION,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    if (isLocating) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = BrandBlue)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = AddStrings.LOCATION_LOADING,
                                color = HomeTextSecondary
                            )
                        }
                    }
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
            text = AddStrings.MAPS_KEY_MISSING_TITLE,
            color = Error,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = AddStrings.MAPS_KEY_MISSING_BODY,
            color = Black,
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text(AddStrings.LABEL_LATITUDE) },
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
                label = { Text(AddStrings.LABEL_LONGITUDE) },
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
                containerColor = BrandBlue,
                contentColor = OnBrandContent
            )
        ) {
            Text(
                text = AddStrings.ACTION_CONFIRM_COORDINATES,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dialogFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Black,
    unfocusedTextColor = Black,
    focusedBorderColor = BrandBlue,
    unfocusedBorderColor = HomeTextSecondary,
    focusedLabelColor = BrandBlue,
    unfocusedLabelColor = HomeTextSecondary,
    cursorColor = BrandBlue
)

private fun readMapApiKey(context: Context): String? {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        appInfo.metaData?.getString(AddStrings.MAPS_API_KEY_METADATA)
    } catch (e: Exception) {
        Logger.w(TAG, "Unable to read Maps API key: ${e.message}")
        null
    }
}

private suspend fun reverseGeocode(
    context: Context,
    latitude: Double,
    longitude: Double
): GeocodedAddress? = withContext(Dispatchers.IO) {
    Logger.d(TAG, "reverseGeocode: lat=$latitude, lng=$longitude")
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val result = addresses?.firstOrNull()?.toGeocodedAddress()
        Logger.d(TAG, "reverseGeocode: result=$result")
        result
    } catch (e: Exception) {
        Logger.e(TAG, "reverseGeocode failed", e)
        null
    }
}

private fun android.location.Address.toFormattedAddress(): String? {
    getAddressLine(0)?.takeIf { it.isNotBlank() }?.let { return it }
    return listOfNotNull(
        subLocality,
        locality,
        subAdminArea,
        adminArea,
        postalCode,
        countryName
    )
        .filter { it.isNotBlank() }
        .joinToString(", ")
        .takeIf { it.isNotBlank() }
}

private fun android.location.Address.toGeocodedAddress(): GeocodedAddress {
    return GeocodedAddress(
        city = locality ?: subAdminArea ?: adminArea,
        locality = subLocality ?: subAdminArea ?: locality,
        pincode = postalCode,
        address = toFormattedAddress() ?: ""
    ).also {
        Logger.d(TAG, "toGeocodedAddress: $it")
    }
}

private data class GeocodedAddress(
    val city: String?,
    val locality: String?,
    val pincode: String?,
    val address: String?
)

private suspend fun navigateToCurrentLocation(
    context: Context,
    cameraPositionState: CameraPositionState,
    setLocating: (Boolean) -> Unit,
    onLocation: (LatLng) -> Unit,
    onError: (String) -> Unit
) {
    setLocating(true)
    try {
        val location = getCurrentLocation(context)
        if (location != null) {
            Logger.d(TAG, "navigateToCurrentLocation: got location $location")
            val target = LatLng(location.latitude, location.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 15f)
            onLocation(target)
        } else {
            Logger.w(TAG, "navigateToCurrentLocation: no location returned")
            onError(AddStrings.ERROR_CURRENT_LOCATION)
        }
    } catch (e: Exception) {
        Logger.e(TAG, "navigateToCurrentLocation: failed", e)
        onError(AddStrings.ERROR_CURRENT_LOCATION)
    } finally {
        setLocating(false)
    }
}

private suspend fun getCurrentLocation(context: Context): Location? =
    CurrentLocationProvider.getCurrentLocation(context)

@Preview(showBackground = true, name = "Location Picker Dialog")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationPickerDialogPreview() {
    RealeTheme {
        LocationPickerDialog(
            initialLat = "12.97",
            initialLng = "77.75",
            onDismiss = {},
            onConfirm = { _, _, _, _, _, _ -> }
        )
    }
}
