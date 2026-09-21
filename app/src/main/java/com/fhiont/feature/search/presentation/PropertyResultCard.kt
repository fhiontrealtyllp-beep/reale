package com.fhiont.feature.search.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fhiont.R
import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.model.RentBuy
import com.fhiont.feature.search.presentation.components.formatIndianPrice
import com.fhiont.ui.preview.PreviewData
import com.fhiont.ui.theme.Black
import com.fhiont.ui.theme.BrandCoral
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.Gray
import com.fhiont.ui.theme.MediaScrim
import com.fhiont.ui.theme.OnMediaContent
import com.fhiont.ui.theme.FhiontTheme
import com.fhiont.ui.theme.SurfaceLight
import com.fhiont.ui.theme.White
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun PropertyResultCard(
    property: Property,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLike: (() -> Unit)? = null,
    badgeContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    footerContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(PropertiesDims.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = PropertiesDims.CARD_ELEVATION)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(PropertiesDims.CARD_PADDING)
            ) {
                PropertyImage(
                    property = property,
                    modifier = Modifier.fillMaxHeight()
                )

                Spacer(modifier = Modifier.width(PropertiesDims.CARD_CONTENT_SPACING))

                PropertyInfo(
                    property = property,
                    onLike = onLike,
                    badgeContent = badgeContent,
                    trailingContent = trailingContent,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            footerContent?.invoke(this)
        }
    }
}

@Composable
private fun PropertyImage(
    property: Property,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(PropertiesDims.CARD_IMAGE_WIDTH)
            .clip(RoundedCornerShape(PropertiesDims.CARD_IMAGE_CORNER_RADIUS))
    ) {
        if (LocalInspectionMode.current) {
            Image(
                painter = painterResource(R.drawable.ic_welcome_home),
                contentDescription = PropertiesStrings.CD_PROPERTY_IMAGE,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = property.images.firstOrNull()
                    ?: "https://picsum.photos/seed/${property.id}/300/200",
                contentDescription = PropertiesStrings.CD_PROPERTY_IMAGE,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (property.images.size > 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PropertiesDims.FILTER_CHIP_ICON_TEXT_SPACING),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(PropertiesDims.FILTER_CHIP_ICON_TEXT_SPACING)
                    .clip(RoundedCornerShape(PropertiesDims.PHOTO_COUNT_CORNER_RADIUS))
                    .background(MediaScrim.copy(alpha = 0.6f))
                    .padding(
                        horizontal = PropertiesDims.PHOTO_COUNT_HORIZONTAL_PADDING,
                        vertical = PropertiesDims.PHOTO_COUNT_VERTICAL_PADDING
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoLibrary,
                    contentDescription = PropertiesStrings.CD_PHOTOS,
                    tint = OnMediaContent,
                    modifier = Modifier.size(PropertiesDims.PHOTO_COUNT_ICON_SIZE)
                )
                Text(
                    text = "${property.images.size} ${PropertiesStrings.PHOTOS_LABEL}",
                    color = OnMediaContent,
                    fontSize = PropertiesDims.PHOTO_COUNT_FONT_SIZE
                )
            }
        }
    }
}

@Composable
private fun PropertyInfo(
    property: Property,
    onLike: (() -> Unit)?,
    badgeContent: (@Composable () -> Unit)?,
    trailingContent: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (badgeContent != null) {
                badgeContent()
            } else {
                ListingBadge(rentBuy = property.rentBuy)
            }

            when {
                trailingContent != null -> trailingContent()
                onLike != null -> Icon(
                    imageVector = if (property.isLiked == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = PropertiesStrings.CD_LIKE,
                    tint = if (property.isLiked == true) BrandCoral else Gray,
                    modifier = Modifier
                        .size(PropertiesDims.LIKE_ICON_SIZE)
                        .clickable(onClick = onLike)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = property.title,
            color = Black,
            fontSize = PropertiesDims.TITLE_FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PropertiesDims.SPEC_ICON_TEXT_SPACING)
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Gray,
                modifier = Modifier.size(PropertiesDims.SPEC_ICON_SIZE)
            )
            Text(
                text = "${property.locality}, ${property.city}",
                color = Gray,
                fontSize = PropertiesDims.LOCATION_FONT_SIZE,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = formatIndianPrice(property.price, property.isRentProperty()),
            color = ControlAccent,
            fontSize = PropertiesDims.PRICE_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        PropertySpecs(property = property)
    }
}

@Composable
private fun ListingBadge(rentBuy: RentBuy?) {
    val text = when (rentBuy) {
        RentBuy.RENT -> PropertiesStrings.BADGE_FOR_RENT
        else -> PropertiesStrings.BADGE_FOR_SALE
    }

    Text(
        text = text,
        color = ControlAccent,
        fontSize = PropertiesDims.BADGE_FONT_SIZE,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(PropertiesDims.BADGE_CORNER_RADIUS))
            .background(SurfaceLight)
            .padding(
                horizontal = PropertiesDims.BADGE_HORIZONTAL_PADDING,
                vertical = PropertiesDims.BADGE_VERTICAL_PADDING
            )
    )
}

@Composable
private fun PropertySpecs(
    property: Property,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PropertiesDims.SPEC_ITEM_SPACING)
    ) {
        property.bedroomType?.let {
            val bedCount = it.label.takeWhile { char -> char.isDigit() }
            val label = if (bedCount.isNotEmpty()) "$bedCount ${PropertiesStrings.BEDS_LABEL}" else it.label
            SpecItem(
                icon = Icons.Outlined.KingBed,
                text = label
            )
        }

        property.bathrooms?.takeIf { it > 0 }?.let { count ->
            SpecItem(
                icon = Icons.Filled.Bathtub,
                text = "$count ${PropertiesStrings.BATHS_LABEL}"
            )
        }

        property.carpetArea?.toInt()?.let { area ->
            SpecItem(
                icon = Icons.Outlined.SquareFoot,
                text = "${NumberFormat.getNumberInstance(Locale.getDefault()).format(area)} ${PropertiesStrings.SQFT_LABEL}"
            )
        }
    }
}

@Composable
private fun SpecItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PropertiesDims.SPEC_ICON_TEXT_SPACING)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Gray,
            modifier = Modifier.size(PropertiesDims.SPEC_ICON_SIZE)
        )
        Text(
            text = text,
            color = Gray,
            fontSize = PropertiesDims.SPEC_FONT_SIZE,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Preview(showBackground = true, name = "Property Card Light")
@Composable
private fun PropertyResultCardPreview() {
    FhiontTheme(darkTheme = false) {
        PropertyResultCard(
            property = PreviewData.sampleProperty,
            onLike = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Property Card Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PropertyResultCardDarkPreview() {
    FhiontTheme(darkTheme = true) {
        PropertyResultCard(
            property = PreviewData.sampleProperty,
            onLike = {},
            onClick = {}
        )
    }
}
