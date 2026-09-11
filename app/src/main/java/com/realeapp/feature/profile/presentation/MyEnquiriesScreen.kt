package com.realeapp.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import coil.compose.AsyncImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realeapp.feature.search.domain.model.Enquiry
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.Gray
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.RealeTheme
import com.realeapp.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEnquiriesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyEnquiriesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = MyEnquiriesStrings.SCREEN_TITLE,
                            color = Black,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = MyEnquiriesStrings.CD_BACK,
                            tint = Black,
                            modifier = Modifier.size(MyEnquiriesDims.BACK_ICON_SIZE)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BrandBlue,
                        strokeWidth = MyEnquiriesDims.LOADING_STROKE
                    )
                }
                uiState.errorMessage != null -> {
                    ErrorMessage(
                        message = uiState.errorMessage.orEmpty(),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.enquiries.isEmpty() -> {
                    EmptyEnquiries(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = MyEnquiriesDims.SCREEN_PADDING,
                            vertical = MyEnquiriesDims.SECTION_SPACING
                        ),
                        verticalArrangement = Arrangement.spacedBy(MyEnquiriesDims.CARD_SPACING)
                    ) {
                        items(
                            items = uiState.enquiries,
                            key = { it.id }
                        ) { enquiry ->
                            EnquiryCard(enquiry = enquiry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnquiryCard(
    enquiry: Enquiry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(MyEnquiriesDims.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = MyEnquiriesDims.CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MyEnquiriesDims.CARD_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EnquiryImage(imageUrl = enquiry.propertyImage)

            Spacer(modifier = Modifier.width(MyEnquiriesDims.IMAGE_TO_CONTENT_SPACING))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MyEnquiriesDims.CARD_CONTENT_SPACING)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = enquiry.propertyTitle,
                        color = Black,
                        fontSize = MyEnquiriesDims.TITLE_FONT_SIZE,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(MyEnquiriesDims.CARD_CONTENT_SPACING))

                    Text(
                        text = enquiry.status.replaceFirstChar { it.uppercase() },
                        color = White,
                        fontSize = MyEnquiriesDims.STATUS_FONT_SIZE,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(MyEnquiriesDims.STATUS_BADGE_CORNER_RADIUS))
                            .background(BrandBlue)
                            .padding(
                                horizontal = MyEnquiriesDims.STATUS_BADGE_HORIZONTAL_PADDING,
                                vertical = MyEnquiriesDims.STATUS_BADGE_VERTICAL_PADDING
                            )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = HomeTextSecondary,
                        modifier = Modifier.size(MyEnquiriesDims.BACK_ICON_SIZE)
                    )
                    Spacer(modifier = Modifier.width(MyEnquiriesDims.CARD_CONTENT_SPACING))
                    Text(
                        text = enquiry.propertyLocation,
                        color = HomeTextSecondary,
                        fontSize = MyEnquiriesDims.LOCATION_FONT_SIZE,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = enquiry.message,
                    color = Gray,
                    fontSize = MyEnquiriesDims.MESSAGE_FONT_SIZE,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EnquiryImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(MyEnquiriesDims.IMAGE_SIZE)
            .clip(RoundedCornerShape(MyEnquiriesDims.IMAGE_CORNER_RADIUS))
            .background(HomeSearchBarBorder),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = MyEnquiriesStrings.CD_PROPERTY_IMAGE,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = MyEnquiriesStrings.CD_PROPERTY_IMAGE,
                tint = Gray,
                modifier = Modifier.size(MyEnquiriesDims.IMAGE_PLACEHOLDER_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun EmptyEnquiries(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(MyEnquiriesDims.SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = MyEnquiriesStrings.EMPTY_TITLE,
            color = Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(MyEnquiriesDims.CARD_CONTENT_SPACING))
        Text(
            text = MyEnquiriesStrings.EMPTY_SUBTITLE,
            color = HomeTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        color = Black,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(MyEnquiriesDims.SCREEN_PADDING)
    )
}

@Preview(showBackground = true)
@Composable
private fun MyEnquiriesScreenPreview() {
    RealeTheme {
        MyEnquiriesScreen(
            onBack = {},
            viewModel = MyEnquiriesViewModel(
                getMyEnquiriesUseCase = object : com.realeapp.feature.search.domain.usecase.GetMyEnquiriesUseCase {
                    override suspend fun invoke(userId: String): com.realeapp.feature.search.domain.utils.Result<List<Enquiry>> {
                        return com.realeapp.feature.search.domain.utils.Result.Success(
                            listOf(
                                Enquiry(
                                    id = "1",
                                    propertyId = "p1",
                                    propertyTitle = "2 BHK Apartment",
                                    propertyLocation = "Porvorim, Goa",
                                    propertyImage = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=400&q=80",
                                    agentPhone = "1234567890",
                                    message = "I am interested in this property. Please contact me.",
                                    userId = "u1",
                                    status = "new",
                                    createdAt = "2026-09-10T10:00:00.000Z"
                                )
                            )
                        )
                    }
                },
                userSession = object : com.realeapp.feature.search.data.session.UserSession {
                    override val user: kotlinx.coroutines.flow.StateFlow<com.realeapp.feature.auth.domain.model.User?> = kotlinx.coroutines.flow.MutableStateFlow(null)
                    override fun getUserId(): String? = "u1"
                    override fun getUser(): com.realeapp.feature.auth.domain.model.User? = null
                    override fun setUser(user: com.realeapp.feature.auth.domain.model.User?) {}
                    override fun clear() {}
                }
            )
        )
    }
}
