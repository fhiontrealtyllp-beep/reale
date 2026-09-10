package com.realeapp.feature.city.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.NavyText
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White
import com.realeapp.util.Logger

private const val SELECTED_CITY_BACKGROUND_ALPHA = 0.08f
private const val CITY_ICON_BACKGROUND_ALPHA = 0.1f

/**
 * A selectable city shown in the city picker.
 */
data class City(val name: String, val region: String)

// TODO: Fetch the city list from the backend API once the cities endpoint is
// available, and move this into a repository/view model.
private val famousCities = listOf(
    City("Mumbai", "India"),
    City("Delhi", "India"),
    City("Bengaluru", "India"),
    City("Hyderabad", "India"),
    City("Chennai", "India"),
    City("Pune", "India"),
    City("Kolkata", "India"),
    City("Dubai", "UAE"),
    City("Singapore", "Singapore"),
    City("London", "United Kingdom"),
    City("New York", "United States"),
    City("Paris", "France")
)

/**
 * City selection screen shown after onboarding when the user skips location access.
 *
 * @param onCitySelected Called with the chosen city when the user taps Continue.
 * @param modifier Optional modifier for the root container.
 */
@Composable
fun CityScreen(
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCity by remember { mutableStateOf<City?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(CityDims.SCREEN_PADDING)
    ) {
        Text(
            text = CityStrings.CITY_TITLE,
            color = NavyText,
            fontSize = CityDims.TITLE_FONT_SIZE,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(CityDims.SPACE_8))

        Text(
            text = CityStrings.CITY_SUBTITLE,
            color = Gray,
            fontSize = CityDims.SUBTITLE_FONT_SIZE
        )

        Spacer(modifier = Modifier.height(CityDims.SPACE_24))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(CityDims.SPACE_12)
        ) {
            items(famousCities) { city ->
                CityRow(
                    city = city,
                    selected = city == selectedCity,
                    onClick = { selectedCity = city }
                )
            }
        }

        Spacer(modifier = Modifier.height(CityDims.SPACE_16))

        Button(
            onClick = {
                Logger.d("CityScreen", "Continue clicked, selectedCity=$selectedCity")
                selectedCity?.let(onCitySelected)
            },
            enabled = selectedCity != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(CityDims.BUTTON_HEIGHT),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandBlue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(CityDims.BUTTON_CORNER_RADIUS)
        ) {
            Text(
                text = CityStrings.BUTTON_CONTINUE,
                fontSize = CityDims.BUTTON_TEXT_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CityRow(
    city: City,
    selected: Boolean,
    onClick: () -> Unit
) {
    val rowShape = RoundedCornerShape(CityDims.CITY_ROW_CORNER_RADIUS)
    val borderColor = if (selected) BrandBlue else HomeSearchBarBorder
    val backgroundColor = if (selected) {
        BrandBlue.copy(alpha = SELECTED_CITY_BACKGROUND_ALPHA)
    } else {
        White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(backgroundColor)
            .border(CityDims.CITY_ROW_BORDER_WIDTH, borderColor, rowShape)
            .clickable(onClick = onClick)
            .padding(CityDims.SPACE_12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(CityDims.CITY_ICON_SIZE)
                .clip(CircleShape)
                .background(BrandBlue.copy(alpha = CITY_ICON_BACKGROUND_ALPHA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = CityStrings.CD_CITY_ICON,
                modifier = Modifier.size(CityDims.CITY_ICON_INNER_SIZE),
                tint = BrandBlue
            )
        }

        Spacer(modifier = Modifier.width(CityDims.SPACE_12))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = city.name,
                color = Black,
                fontSize = CityDims.CITY_NAME_FONT_SIZE,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = city.region,
                color = Gray,
                fontSize = CityDims.CITY_REGION_FONT_SIZE
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = CityStrings.CD_CITY_SELECTED,
                tint = BrandBlue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityScreenPreview() {
    RealeTheme {
        CityScreen(onCitySelected = {})
    }
}
