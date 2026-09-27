package com.fhiont.feature.add.presentation

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.Error
import com.fhiont.ui.theme.HomeCategoryUnselected
import com.fhiont.ui.theme.HomeTextSecondary
import com.fhiont.ui.theme.OnMediaContent
import com.fhiont.ui.preview.PreviewData
import com.fhiont.ui.theme.FhiontTheme

private val photoSuggestions = AddStrings.PHOTO_SUGGESTIONS

@Composable
internal fun AddPropertyStep3Screen(
    images: List<String>,
    isUploadingImage: Boolean = false,
    uploadError: String? = null,
    onAddMore: () -> Unit = {},
    onRemoveImage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PhotoGrid(
            images = images,
            isUploadingImage = isUploadingImage,
            onRemoveImage = onRemoveImage,
            onAddMore = onAddMore
        )

        uploadError?.let { error ->
            Text(
                text = error,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PhotoGrid(
    images: List<String>,
    isUploadingImage: Boolean,
    onRemoveImage: (String) -> Unit,
    onAddMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    // A null cell renders the "Add More" tile at the end of the grid.
    val cells: List<String?> = if (images.size >= AddStrings.MAX_PROPERTY_PHOTOS) {
        images
    } else {
        images + listOf(null)
    }
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
                        AddMoreTile(
                            isUploadingImage = isUploadingImage,
                            onClick = onAddMore,
                            modifier = cellModifier
                        )
                    } else {
                        val index = rowIndex * 2 + columnIndex
                        PhotoCell(
                            url = url,
                            label = photoSuggestions.getOrElse(index) { AddStrings.PHOTO_LABEL_PREFIX + (index + 1) },
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
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
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
}

@Composable
private fun AddMoreTile(
    isUploadingImage: Boolean,
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
            .then(if (isUploadingImage) Modifier else Modifier.clickable(onClick = onClick)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isUploadingImage) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = ControlAccent,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = AddStrings.CD_ADD_MORE_PHOTOS,
                tint = ControlAccent,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPropertyStep3ScreenPreview() {
    FhiontTheme {
        AddPropertyStep3Screen(
            images = PreviewData.samplePropertyForm.images,
            onAddMore = {},
            onRemoveImage = {},
            modifier = Modifier
        )
    }
}
