package com.fhiont.feature.city.presentation

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.fhiont.ui.components.GenericLoader
import com.fhiont.ui.theme.AppBackground
import com.fhiont.ui.theme.Black
import com.fhiont.ui.theme.ControlAccent
import com.fhiont.ui.theme.Gray
import com.fhiont.ui.theme.HomeSearchBarBorder
import com.fhiont.ui.theme.NavyText
import com.fhiont.ui.theme.OnControlAccent
import com.fhiont.ui.theme.FhiontTheme
import com.fhiont.ui.theme.White
import com.fhiont.util.Logger

private const val SELECTED_CITY_BACKGROUND_ALPHA = 0.08f
private const val CITY_ICON_BACKGROUND_ALPHA = 0.1f

/**
 * A selectable city shown in the city picker.
 */
data class City(val name: String, val region: String)

/**
 * City selection screen shown after onboarding when the user skips location access.
 *
 * @param onCitySelected Called with the chosen city when the user taps Continue.
 * @param modifier Optional modifier for the root container.
 */
@Composable
fun CityScreen(
    onCitySelected: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityViewModel = koinViewModel()
) {
    var selectedCity by remember { mutableStateOf<City?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

        when {
            uiState.isLoading -> {
                GenericLoader(modifier = Modifier.weight(1f))
            }
            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.errorMessage ?: CityStrings.EMPTY_CITIES,
                        color = Gray,
                        fontSize = CityDims.SUBTITLE_FONT_SIZE
                    )
                    TextButton(onClick = viewModel::load) {
                        Text(
                            text = CityStrings.BUTTON_RETRY,
                            color = ControlAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            uiState.cities.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = CityStrings.EMPTY_CITIES,
                        color = Gray,
                        fontSize = CityDims.SUBTITLE_FONT_SIZE
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(CityDims.SPACE_12)
                ) {
                    items(uiState.cities) { city ->
                        CityRow(
                            city = city,
                            selected = city == selectedCity,
                            onClick = { selectedCity = city }
                        )
                    }
                }
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
                containerColor = ControlAccent,
                contentColor = OnControlAccent
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
    val borderColor = if (selected) ControlAccent else HomeSearchBarBorder
    val backgroundColor = if (selected) {
        ControlAccent.copy(alpha = SELECTED_CITY_BACKGROUND_ALPHA)
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
                .background(ControlAccent.copy(alpha = CITY_ICON_BACKGROUND_ALPHA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = CityStrings.CD_CITY_ICON,
                modifier = Modifier.size(CityDims.CITY_ICON_INNER_SIZE),
                tint = ControlAccent
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
            if (city.region.isNotBlank()) {
                Text(
                    text = city.region,
                    color = Gray,
                    fontSize = CityDims.CITY_REGION_FONT_SIZE
                )
            }
        }

        if (selected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = CityStrings.CD_CITY_SELECTED,
                tint = ControlAccent
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityScreenPreview() {
    val previewRepository = object : com.fhiont.feature.search.domain.repository.LocationSuggestionRepository {
        override suspend fun getSuggestions(query: String) =
            com.fhiont.feature.search.domain.utils.Result.Success(
                emptyList<com.fhiont.feature.search.domain.model.LocationSuggestion>()
            )

        override suspend fun getCities(query: String) =
            com.fhiont.feature.search.domain.utils.Result.Success(
                listOf("Goa", "Mumbai", "Delhi", "Bengaluru")
            )

        override suspend fun getLocalities(city: String, query: String) =
            com.fhiont.feature.search.domain.utils.Result.Success(emptyList<String>())

        override fun invalidateCache() {}
    }
    FhiontTheme {
        CityScreen(
            onCitySelected = {},
            viewModel = CityViewModel(previewRepository)
        )
    }
}
