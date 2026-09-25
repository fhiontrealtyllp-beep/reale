package com.fhiont.feature.search.presentation.components

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
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
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.presentation.MapMarkerDims
import com.fhiont.feature.search.presentation.SearchDims
import com.fhiont.feature.search.presentation.SearchStrings
import com.fhiont.ui.theme.Accent
import com.fhiont.ui.theme.White
import com.fhiont.ui.theme.IsDarkAppTheme
import com.fhiont.ui.theme.Black
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.HomeTextSecondary
import com.fhiont.ui.theme.MapMarker
import com.fhiont.ui.theme.OnControlAccent
import kotlin.math.roundToInt

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

    // Booking.com-style pills: filled accent bubble, contrasting price text.
    // Favorites flip to the pink MapMarker fill and gain a heart prefix.
    val pillArgb = ControlAccent.toArgb()
    val onPillArgb = OnControlAccent.toArgb()
    val favoriteArgb = MapMarker.toArgb()
    // Marker bitmaps must scale with display density (like dp), not just font
    // scale — otherwise pills render tiny on high-density screens.
    val markerScale = context.resources.displayMetrics.scaledDensity

    val first = properties.firstOrNull { it.latitude != null && it.longitude != null }
    val cameraPositionState = rememberCameraPositionState {
        position = if (first != null) {
            CameraPosition.fromLatLngZoom(
                LatLng(first.latitude!!, first.longitude!!),
                SearchDims.MAP_FIRST_PROPERTY_ZOOM
            )
        } else {
            CameraPosition.fromLatLngZoom(
                LatLng(SearchDims.MAP_DEFAULT_LATITUDE, SearchDims.MAP_DEFAULT_LONGITUDE),
                SearchDims.MAP_DEFAULT_ZOOM
            )
        }
    }

    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(isMapLoaded, properties) {
        if (isMapLoaded) {
            val bounds = buildBounds(properties)
            if (bounds != null) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, SearchDims.MAP_BOUNDS_PADDING_PX)
                )
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
            mapColorScheme = if (IsDarkAppTheme) ComposeMapColorScheme.DARK else ComposeMapColorScheme.LIGHT,
            onMapLoaded = { isMapLoaded = true }
        ) {
            properties.forEach { property ->
                val lat = property.latitude ?: return@forEach
                val lng = property.longitude ?: return@forEach
                val isFavorite = property.isLiked == true
                val markerIcon = remember(
                    property.documentId,
                    property.id,
                    property.price,
                    property.isRentProperty(),
                    isFavorite,
                    markerScale,
                    pillArgb,
                    onPillArgb,
                    favoriteArgb
                ) {
                    BitmapDescriptorFactory.fromBitmap(
                        createPriceMarkerBitmap(
                            label = formatIndianPrice(property.price, property.isRentProperty()),
                            isFavorite = isFavorite,
                            markerScale = markerScale,
                            pillArgb = pillArgb,
                            onPillArgb = onPillArgb,
                            favoriteArgb = favoriteArgb
                        )
                    )
                }
                Marker(
                    state = MarkerState(position = LatLng(lat, lng)),
                    icon = markerIcon,
                    zIndex = if (isFavorite) {
                        MapMarkerDims.FAVORITE_Z_INDEX
                    } else {
                        MapMarkerDims.DEFAULT_Z_INDEX
                    },
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
                .padding(SearchDims.MAP_FAB_PADDING),
            verticalArrangement = Arrangement.spacedBy(SearchDims.MAP_FAB_SPACING)
        ) {
            FloatingActionButton(
                onClick = { cameraPositionState.move(CameraUpdateFactory.zoomIn()) },
                containerColor = White,
                contentColor = Black,
                modifier = Modifier.size(SearchDims.MAP_FAB_SIZE)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = SearchStrings.CD_ZOOM_IN
                )
            }
            FloatingActionButton(
                onClick = { cameraPositionState.move(CameraUpdateFactory.zoomOut()) },
                containerColor = White,
                contentColor = Black,
                modifier = Modifier.size(SearchDims.MAP_FAB_SIZE)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = SearchStrings.CD_ZOOM_OUT
                )
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
            contentPadding = PaddingValues(SearchDims.MAP_PLACEHOLDER_PADDING),
            verticalArrangement = Arrangement.spacedBy(SearchDims.MAP_PLACEHOLDER_ITEM_SPACING)
        ) {
            item {
                Text(
                    text = SearchStrings.MAP_VIEW_TITLE,
                    color = Black,
                    fontSize = SearchDims.MAP_PLACEHOLDER_TITLE_FONT,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(SearchDims.MAP_PLACEHOLDER_HEADER_SPACING))
                Text(
                    text = SearchStrings.MAP_PLACEHOLDER_HINT,
                    color = HomeTextSecondary,
                    fontSize = SearchDims.MAP_PLACEHOLDER_HINT_FONT
                )
                Spacer(modifier = Modifier.height(SearchDims.MAP_PLACEHOLDER_HEADER_BOTTOM_SPACING))
            }
            items(properties, key = { it.documentId ?: it.id }) { property ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SearchDims.MAP_PLACEHOLDER_ITEM_SPACING)
                ) {
                    Icon(
                        imageVector = if (property.isLiked == true) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.LocationOn
                        },
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(SearchDims.MAP_PLACEHOLDER_ICON_SIZE)
                    )
                    Column(
                        modifier = Modifier.clickable { onPropertyTap(property) },
                        verticalArrangement = Arrangement.spacedBy(SearchDims.MAP_PLACEHOLDER_TEXT_SPACING)
                    ) {
                        Text(
                            text = property.title,
                            color = Black,
                            fontSize = SearchDims.MAP_PLACEHOLDER_NAME_FONT,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${property.locality}, ${property.city}",
                            color = HomeTextSecondary,
                            fontSize = SearchDims.MAP_PLACEHOLDER_LOCATION_FONT
                        )
                        Text(
                            text = formatIndianPrice(property.price, property.isRentProperty()),
                            color = HomeTextSecondary,
                            fontSize = SearchDims.MAP_PLACEHOLDER_PRICE_FONT
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

/**
 * Draws a Booking.com-style price-pill marker: a filled rounded bubble with
 * the price in bold and a small tail pointing down to the property's exact
 * coordinate (default marker anchor is bottom-center). Favorites get the
 * [favoriteArgb] fill plus a heart prefix next to the price.
 */
private fun createPriceMarkerBitmap(
    label: String,
    isFavorite: Boolean,
    markerScale: Float,
    pillArgb: Int,
    onPillArgb: Int,
    favoriteArgb: Int
): Bitmap {
    fun Float.scaled(): Float = this * markerScale

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = MapMarkerDims.TEXT_SIZE.scaled()
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val heartSize = if (isFavorite) MapMarkerDims.HEART_SIZE.scaled() else 0f
    val heartGap = if (isFavorite) MapMarkerDims.HEART_TEXT_GAP.scaled() else 0f
    val contentWidth = heartSize + heartGap + textPaint.measureText(label)

    val bubbleWidth = contentWidth + MapMarkerDims.HORIZONTAL_PADDING.scaled() * 2
    val bubbleHeight = textPaint.textSize + MapMarkerDims.VERTICAL_PADDING.scaled() * 2
    val tailHeight = MapMarkerDims.TAIL_HEIGHT.scaled()

    val bitmap = Bitmap.createBitmap(
        bubbleWidth.roundToInt(),
        (bubbleHeight + tailHeight).roundToInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)

    val fillArgb = if (isFavorite) favoriteArgb else pillArgb
    val contentArgb = onPillArgb

    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = fillArgb
        style = Paint.Style.FILL
    }
    val cornerRadius = MapMarkerDims.CORNER_RADIUS.scaled()
    canvas.drawRoundRect(
        RectF(0f, 0f, bubbleWidth, bubbleHeight),
        cornerRadius,
        cornerRadius,
        fillPaint
    )

    // Tail triangle overlapping the bubble edge so the seam never shows.
    val tailHalfWidth = MapMarkerDims.TAIL_HALF_WIDTH.scaled()
    val tailPath = Path().apply {
        moveTo(bubbleWidth / 2f - tailHalfWidth, bubbleHeight - MapMarkerDims.TAIL_OVERLAP.scaled())
        lineTo(bubbleWidth / 2f + tailHalfWidth, bubbleHeight - MapMarkerDims.TAIL_OVERLAP.scaled())
        lineTo(bubbleWidth / 2f, bubbleHeight + tailHeight)
        close()
    }
    canvas.drawPath(tailPath, fillPaint)

    val contentLeft = (bubbleWidth - contentWidth) / 2f
    if (isFavorite) {
        val heartHeight = heartSize * MapMarkerDims.HEART_HEIGHT_RATIO
        val heartTop = (bubbleHeight - heartHeight) / 2f
        canvas.drawPath(
            heartPath(contentLeft, heartTop, heartSize, heartHeight),
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = contentArgb }
        )
    }

    val metrics = textPaint.fontMetrics
    val textX = contentLeft + heartSize + heartGap
    val textY = bubbleHeight / 2f - (metrics.ascent + metrics.descent) / 2f
    textPaint.color = contentArgb
    canvas.drawText(label, textX, textY, textPaint)

    return bitmap
}

/**
 * Material "favorite" heart scaled to fill a width × height box. Source
 * viewport: x 2–22, y 3–21.35 — the anchors span the full box so the drawn
 * heart keeps its natural wide proportions instead of shrinking inward.
 */
private fun heartPath(left: Float, top: Float, width: Float, height: Float): Path {
    val sx = width / 20f
    val sy = height / 18.35f
    fun px(v: Float) = left + (v - 2f) * sx
    fun py(v: Float) = top + (v - 3f) * sy
    return Path().apply {
        moveTo(px(12f), py(21.35f))
        lineTo(px(10.55f), py(20.03f))
        cubicTo(px(5.4f), py(15.36f), px(2f), py(12.28f), px(2f), py(8.5f))
        cubicTo(px(2f), py(5.42f), px(4.42f), py(3f), px(7.5f), py(3f))
        cubicTo(px(9.24f), py(3f), px(10.91f), py(3.81f), px(12f), py(5.09f))
        cubicTo(px(13.09f), py(3.81f), px(14.76f), py(3f), px(16.5f), py(3f))
        cubicTo(px(19.58f), py(3f), px(22f), py(5.42f), px(22f), py(8.5f))
        cubicTo(px(22f), py(12.28f), px(18.6f), py(15.36f), px(13.45f), py(20.04f))
        close()
    }
}
