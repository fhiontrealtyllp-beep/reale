package com.realeapp.feature.add.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.Error
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.OnMediaContent
import com.realeapp.ui.theme.White
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.HomeTextSecondary

private val photoSuggestions = AddStrings.PHOTO_SUGGESTIONS

private val photoTips = AddStrings.PHOTO_TIPS

@Composable
internal fun AddPropertyStep3Screen(
    images: List<String>,
    isUploadingImage: Boolean,
    imageUploadError: String?,
    onUploadImages: (List<Pair<ByteArray, String>>) -> Unit,
    onRemoveImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageLaunchers = rememberImageLaunchers(onUploadImages)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PhotoUploadBox(
            isUploading = isUploadingImage,
            onSelect = imageLaunchers.gallery
        )

        Text(
            text = AddStrings.PHOTOS_VISIBILITY_HINT,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (imageUploadError != null) {
            Text(
                text = imageUploadError,
                color = Error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (images.isNotEmpty()) {
            PhotoGrid(
                images = images,
                onRemoveImage = onRemoveImage,
                onAddMore = imageLaunchers.gallery
            )
        }

        PhotoTipsCard()
    }
}

@Composable
private fun PhotoUploadBox(
    isUploading: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dashColor = HomeTextSecondary
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f), 0f)
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isUploading, onClick = onSelect)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isUploading) {
            CircularProgressIndicator(
                color = BrandBlue,
                modifier = Modifier.size(36.dp),
                strokeWidth = 2.dp
            )
            Text(
                text = AddStrings.UPLOADING_PHOTOS,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = AddStrings.UPLOAD_PHOTOS_TITLE,
                color = Black,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = AddStrings.UPLOAD_PHOTOS_DRAG_HINT,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = AddStrings.UPLOAD_PHOTOS_FORMAT_HINT,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlue,
                    contentColor = OnBrandContent
                )
            ) {
                Text(
                    text = AddStrings.ACTION_SELECT_PHOTOS,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun PhotoGrid(
    images: List<String>,
    onRemoveImage: (String) -> Unit,
    onAddMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    // A null cell renders the "Add More" tile at the end of the grid.
    val cells: List<String?> = images + listOf(null)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cells.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEachIndexed { columnIndex, url ->
                    val cellModifier = Modifier.weight(1f)
                    if (url == null) {
                        AddMoreTile(onClick = onAddMore, modifier = cellModifier)
                    } else {
                        val index = rowIndex * 2 + columnIndex
                        PhotoCell(
                            url = url,
                            label = photoSuggestions.getOrElse(index) { AddStrings.PHOTO_LABEL_PREFIX + (index + 1) },
                            isCover = index == 0,
                            onRemove = { onRemoveImage(url) },
                            modifier = cellModifier
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PhotoCell(
    url: String,
    label: String,
    isCover: Boolean,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(HomeCategoryUnselected)
        ) {
            AsyncImage(
                model = url,
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (isCover) {
                Text(
                    text = AddStrings.BADGE_COVER,
                    color = OnMediaContent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(BrandBlue, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(24.dp)
                    .background(HomeCategoryUnselected, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = AddStrings.CD_REMOVE_PREFIX + label,
                    tint = OnMediaContent,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Text(
            text = label,
            color = Black,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun AddMoreTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dashColor = HomeTextSecondary
    Column(
        modifier = modifier
            .height(140.dp)
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 12f), 0f)
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = AddStrings.CD_ADD_MORE_PHOTOS,
            tint = BrandBlue,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = AddStrings.ACTION_ADD_MORE,
            color = BrandBlue,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PhotoTipsCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HomeCategoryUnselected, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = AddStrings.TIPS_TITLE,
                color = Black,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        photoTips.forEach { tip ->
            Text(
                text = AddStrings.BULLET_PREFIX + tip,
                color = HomeTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
