package com.realeapp.feature.search.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.KingBed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.MediaScrim
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.ChipBackground
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary
import com.realeapp.ui.theme.OnMediaContent

@Composable
fun PropertyListItem(
    property: Property,
    onLike: () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = property.images.firstOrNull()
                        ?: "https://picsum.photos/seed/${property.id}/300/200",
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                // Gradient scrim UI keeps overlaid chips legible on bright photos.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MediaScrim.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    MediaScrim.copy(alpha = 0.55f)
                                )
                            )
                        )
                )

                // Listing-type badge UI (rent / sale).
                property.rentBuy?.let { rentBuy ->
                    Text(
                        text = if (property.isRentProperty()) "FOR RENT" else "FOR SALE",
                        color = OnAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Accent)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onLike,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MediaScrim.copy(alpha = 0.35f))
                ) {
                    Icon(
                        imageVector = if (property.isLiked == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (property.isLiked == true) Accent else OnMediaContent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Photo-count chip UI shown when the listing has multiple images.
                if (property.images.size > 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MediaScrim.copy(alpha = 0.45f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = null,
                            tint = OnMediaContent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${property.images.size}",
                            color = OnMediaContent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = formatIndianPrice(property.price, property.isRentProperty()),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = property.title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    property.propertyType?.let {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it.label,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ChipBackground)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${property.locality}, ${property.city}",
                        color = TextPrimary.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Key specs UI: bedrooms, furnishing and carpet area when available.
                val specs = listOfNotNull(
                    property.bedroomType?.let { Icons.Outlined.KingBed to it.label },
                    property.furnishing?.let { Icons.Outlined.Chair to it.label },
                    property.carpetArea?.let { Icons.Outlined.SquareFoot to "${it.toInt()} sq.ft" }
                )
                if (specs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        specs.forEach { (icon, label) ->
                            SpecItem(icon = icon, label = label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecItem(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = label,
            color = TextPrimary.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
