package com.realeapp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.MainBackground
import com.realeapp.ui.theme.OnAccent
import com.realeapp.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSavedClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = HomeStrings.SCREEN_TITLE,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainBackground
                )
            )
        },
        containerColor = MainBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(HomeDims.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(HomeDims.SECTION_SPACING)
        ) {
            item { WelcomeSection() }
            item { SearchCard(onSearchClick = onSearchClick) }
            item {
                QuickActions(
                    onSavedClick = onSavedClick,
                    onAddClick = onAddClick,
                    onProfileClick = onProfileClick
                )
            }
        }
    }
}

@Composable
private fun WelcomeSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = HomeStrings.WELCOME_TITLE,
            color = TextPrimary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(HomeDims.TITLE_SPACING))
        Text(
            text = HomeStrings.WELCOME_SUBTITLE,
            color = TextPrimary.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SearchCard(onSearchClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(HomeDims.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(HomeDims.CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(HomeDims.CARD_CONTENT_SPACING)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = HomeStrings.CD_SEARCH_ICON,
                    tint = Accent,
                    modifier = Modifier.size(HomeDims.CARD_ICON_SIZE)
                )
                Text(
                    text = HomeStrings.SEARCH_CARD_TITLE,
                    modifier = Modifier.padding(start = HomeDims.ICON_SPACING),
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = HomeStrings.SEARCH_CARD_BODY,
                color = TextPrimary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = onSearchClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(HomeDims.BUTTON_CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = OnAccent
                )
            ) {
                Text(text = HomeStrings.SEARCH_CARD_ACTION)
            }
        }
    }
}

@Composable
private fun QuickActions(
    onSavedClick: () -> Unit,
    onAddClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(HomeDims.CARD_SPACING)
    ) {
        Text(
            text = HomeStrings.QUICK_ACTIONS_TITLE,
            color = TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HomeDims.CARD_SPACING)
        ) {
            QuickActionCard(
                icon = Icons.Filled.Favorite,
                label = HomeStrings.SAVED_CARD_TITLE,
                onClick = onSavedClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Filled.Add,
                label = HomeStrings.ADD_CARD_TITLE,
                onClick = onAddClick,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Filled.Person,
                label = HomeStrings.PROFILE_CARD_TITLE,
                onClick = onProfileClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(HomeDims.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HomeDims.CARD_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(HomeDims.ICON_SPACING)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Accent,
                modifier = Modifier.size(HomeDims.QUICK_ACTION_ICON_SIZE)
            )
            Text(
                text = label,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
