package com.realeapp.feature.profile.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import java.io.File
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView.Guidelines
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.realeapp.core.theme.ThemeMode
import com.realeapp.feature.add.presentation.ImageSourceDialog
import com.realeapp.feature.add.presentation.toJpegBytes
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.Black
import com.realeapp.ui.theme.BrandBlue
import com.realeapp.ui.theme.BrandCoral
import com.realeapp.ui.theme.BrandRed
import com.realeapp.ui.theme.HomeCategoryUnselected
import com.realeapp.ui.theme.HomeSearchBarBorder
import com.realeapp.ui.theme.HomeTextSecondary
import com.realeapp.ui.theme.OnBrandContent
import com.realeapp.ui.theme.VerifiedGreen
import com.realeapp.ui.theme.White
import com.realeapp.util.Logger
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onLoginClick: () -> Unit,
    onListPropertyClick: () -> Unit = {},
    onMyListingsClick: () -> Unit = {},
    onMyEnquiriesClick: () -> Unit = {},
    onPersonalInfoClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var cameraOutputUri by remember { mutableStateOf<Uri?>(null) }

    val cropLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        result.uriContent?.let { uri ->
            val bytes = processImageFromUri(context, uri)
            if (bytes != null) {
                viewModel.uploadImage(bytes, PROFILE_IMAGE_FILENAME)
            }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri ->
            cropLauncher.launch(
                CropImageContractOptions(
                    uri = imageUri,
                    cropImageOptions = CropImageOptions(
                        fixAspectRatio = true,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        guidelines = Guidelines.ON
                    )
                )
            )
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraOutputUri != null) {
            cropLauncher.launch(
                CropImageContractOptions(
                    uri = cameraOutputUri!!,
                    cropImageOptions = CropImageOptions(
                        fixAspectRatio = true,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        guidelines = Guidelines.ON
                    )
                )
            )
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(ProfileDims.SNACKBAR_CORNER_RADIUS),
                    containerColor = Black,
                    contentColor = White
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                // Full-screen loading UI shown while the initial profile is being fetched.
                uiState.isLoading && uiState.user == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BrandBlue
                    )
                }

                // Logged-out UI prompting the user to open the login flow.
                !uiState.isLoggedIn -> ProfileLoginPrompt(
                    onLoginClick = onLoginClick,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Logged-in UI containing profile details, editing, support, and logout actions.
                else -> ProfileContent(
                    user = uiState.user,
                    isImageUploading = uiState.isImageUploading,
                    onPickImage = { showImageSourceDialog = true },
                    onListPropertyClick = onListPropertyClick,
                    onMyListingsClick = onMyListingsClick,
                    onMyEnquiriesClick = onMyEnquiriesClick,
                    onPersonalInfoClick = onPersonalInfoClick,
                    onNotificationsClick = onNotificationsClick,
                    onSettingsClick = onSettingsClick,
                    onHelpSupportClick = { launchEmail(context) },
                    onLogoutClick = viewModel::logout,
                    onUpdateField = { field, value -> viewModel.updateProfileField(field, value) },
                    themeMode = themeMode,
                    onThemeModeSelected = viewModel::setThemeMode,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (showImageSourceDialog) {
                ImageSourceDialog(
                    onCamera = {
                        showImageSourceDialog = false
                        cameraOutputUri = createImageUri(context)
                        cameraOutputUri?.let { cameraLauncher.launch(it) }
                    },
                    onGallery = {
                        showImageSourceDialog = false
                        imagePicker.launch(IMAGE_MIME_TYPE)
                    },
                    onDismiss = { showImageSourceDialog = false }
                )
            }
        }
    }
}

private data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String?,
    val contentDescription: String,
    val onClick: () -> Unit
)

@Composable
private fun ProfileContent(
    user: User?,
    isImageUploading: Boolean,
    onPickImage: () -> Unit,
    onListPropertyClick: () -> Unit,
    onMyListingsClick: () -> Unit,
    onMyEnquiriesClick: () -> Unit,
    onPersonalInfoClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpSupportClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onUpdateField: (String, String) -> Unit,
    themeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }

    val activityItems = listOf(
        ProfileMenuItem(
            icon = Icons.Outlined.Home,
            title = ProfileStrings.MY_LISTINGS,
            subtitle = ProfileStrings.MY_LISTINGS_SUBTITLE,
            contentDescription = ProfileStrings.MY_LISTINGS,
            onClick = {
                Logger.d(PROFILE_MENU_TAG, "My Listings row tapped")
                onMyListingsClick()
            }
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.Description,
            title = ProfileStrings.MY_ENQUIRIES,
            subtitle = ProfileStrings.MY_ENQUIRIES_SUBTITLE,
            contentDescription = ProfileStrings.MY_ENQUIRIES,
            onClick = onMyEnquiriesClick
        )
    )
    val accountItems = listOf(
        ProfileMenuItem(
            icon = Icons.Outlined.Person,
            title = ProfileStrings.PERSONAL_INFORMATION,
            subtitle = ProfileStrings.PERSONAL_INFORMATION_SUBTITLE,
            contentDescription = ProfileStrings.CD_PERSONAL_INFO,
            onClick = onPersonalInfoClick
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.Notifications,
            title = ProfileStrings.NOTIFICATIONS,
            subtitle = ProfileStrings.NOTIFICATIONS_SUBTITLE,
            contentDescription = ProfileStrings.CD_NOTIFICATIONS_ITEM,
            onClick = onNotificationsClick
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.Settings,
            title = ProfileStrings.SETTINGS,
            subtitle = ProfileStrings.SETTINGS_SUBTITLE,
            contentDescription = ProfileStrings.CD_SETTINGS,
            onClick = onSettingsClick
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.DarkMode,
            title = ProfileStrings.APPEARANCE,
            subtitle = ProfileStrings.APPEARANCE_SUBTITLE.format(themeModeLabel(themeMode)),
            contentDescription = ProfileStrings.CD_APPEARANCE,
            onClick = { showThemeDialog = true }
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.HelpOutline,
            title = ProfileStrings.HELP_SUPPORT,
            subtitle = ProfileStrings.HELP_SUPPORT_SUBTITLE,
            contentDescription = ProfileStrings.CD_HELP_SUPPORT,
            onClick = onHelpSupportClick
        ),
        ProfileMenuItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = ProfileStrings.LOGOUT,
            subtitle = null,
            contentDescription = ProfileStrings.CD_LOGOUT,
            onClick = onLogoutClick
        )
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ProfileDims.SCREEN_PADDING,
            vertical = ProfileDims.CARD_INNER_PADDING
        ),
        verticalArrangement = Arrangement.spacedBy(ProfileDims.SECTION_SPACING)
    ) {
        item { ProfileTopBar(onNotificationsClick = onNotificationsClick) }
        item {
            ProfileCard(
                user = user,
                isImageUploading = isImageUploading,
                onPickImage = onPickImage,
                onEditProfileClick = { showEditDialog = true }
            )
        }
        item { ListPropertyBanner(onClick = onListPropertyClick) }
        item { ProfileMenuSection(title = ProfileStrings.SECTION_MY_ACTIVITY, items = activityItems) }
        item { ProfileMenuSection(title = ProfileStrings.SECTION_ACCOUNT, items = accountItems) }
    }

    if (showEditDialog) {
        EditProfileDialog(
            user = user,
            onSave = onUpdateField,
            onDismiss = { showEditDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeModeDialog(
            selectedMode = themeMode,
            onSelect = { mode ->
                onThemeModeSelected(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}

private fun themeModeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> ProfileStrings.THEME_SYSTEM
    ThemeMode.LIGHT -> ProfileStrings.THEME_LIGHT
    ThemeMode.DARK -> ProfileStrings.THEME_DARK
}

@Composable
private fun ThemeModeDialog(
    selectedMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(ProfileDims.DIALOG_CORNER_RADIUS),
        containerColor = White,
        title = {
            Text(
                text = ProfileStrings.THEME_DIALOG_TITLE,
                color = Black,
                fontSize = ProfileDims.DIALOG_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(ProfileDims.DIALOG_FIELD_CORNER_RADIUS))
                            .clickable { onSelect(mode) }
                            .padding(vertical = ProfileDims.THEME_OPTION_VERTICAL_PADDING),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = mode == selectedMode,
                            onClick = { onSelect(mode) },
                            colors = RadioButtonDefaults.colors(selectedColor = BrandBlue)
                        )
                        Spacer(modifier = Modifier.width(ProfileDims.THEME_OPTION_RADIO_TEXT_SPACING))
                        Text(
                            text = themeModeLabel(mode),
                            color = Black,
                            fontSize = ProfileDims.THEME_OPTION_FONT_SIZE
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = ProfileStrings.ACTION_CANCEL,
                    color = HomeTextSecondary
                )
            }
        }
    )
}

@Composable
private fun ProfileTopBar(
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = ProfileStrings.APP_NAME_BLUE,
                color = BrandBlue,
                fontSize = ProfileDims.APP_NAME_FONT_SIZE,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = ProfileStrings.APP_NAME_ACCENT,
                color = BrandCoral,
                fontSize = ProfileDims.APP_NAME_FONT_SIZE,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = ProfileStrings.CD_NOTIFICATIONS,
                tint = Black,
                modifier = Modifier
                    .size(ProfileDims.TOP_BAR_ICON_SIZE)
                    .clickable(onClick = onNotificationsClick)
            )
            Box(
                modifier = Modifier
                    .size(ProfileDims.NOTIFICATION_BADGE_SIZE)
                    .clip(CircleShape)
                    .background(BrandRed)
            )
        }
    }
}

@Composable
private fun ProfileCard(
    user: User?,
    isImageUploading: Boolean,
    onPickImage: () -> Unit,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ProfileDims.CARD_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = ProfileDims.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProfileDims.CARD_INNER_PADDING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                ProfileAvatar(
                    user = user,
                    isImageUploading = isImageUploading,
                    onPickImage = onPickImage
                )

                Spacer(modifier = Modifier.width(ProfileDims.AVATAR_TO_DETAILS_SPACING))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user?.name.orEmpty(),
                            color = Black,
                            fontSize = ProfileDims.PROFILE_NAME_FONT_SIZE,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(ProfileDims.VERIFIED_BADGE_SPACING))
                        EditProfileButton(onClick = onEditProfileClick)
                    }

                    Spacer(modifier = Modifier.height(ProfileDims.PROFILE_DETAIL_SPACING))

                    Text(
                        text = user?.phone.orEmpty(),
                        color = HomeTextSecondary,
                        fontSize = ProfileDims.PROFILE_DETAIL_FONT_SIZE
                    )
                    Text(
                        text = user?.email.orEmpty(),
                        color = HomeTextSecondary,
                        fontSize = ProfileDims.PROFILE_DETAIL_FONT_SIZE
                    )
                }
            }

            // Verified badges span the full card width so both fit on one line.
            if (!user?.phone.isNullOrBlank() || !user?.email.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(ProfileDims.VERIFIED_BADGE_TOP_SPACING))

                Row(horizontalArrangement = Arrangement.spacedBy(ProfileDims.VERIFIED_BADGE_SPACING)) {
                    if (!user?.phone.isNullOrBlank()) {
                        VerifiedBadge(
                            icon = Icons.Filled.VerifiedUser,
                            label = ProfileStrings.PHONE_VERIFIED,
                            contentDescription = ProfileStrings.CD_PHONE_VERIFIED,
                            color = VerifiedGreen
                        )
                    }
                    if (!user?.email.isNullOrBlank()) {
                        VerifiedBadge(
                            icon = Icons.Outlined.MarkEmailRead,
                            label = ProfileStrings.EMAIL_VERIFIED,
                            contentDescription = ProfileStrings.CD_EMAIL_VERIFIED,
                            color = BrandBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    user: User?,
    isImageUploading: Boolean,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(ProfileDims.AVATAR_SIZE),
        contentAlignment = Alignment.Center
    ) {
        // Avatar UI falls back to the user's initials when no image is available.
        if (user?.image.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(BrandBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.name?.take(2)?.uppercase() ?: ProfileStrings.FALLBACK_INITIALS,
                    color = BrandBlue,
                    fontSize = ProfileDims.INITIALS_FONT_SIZE,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            AsyncImage(
                model = user?.image,
                contentDescription = ProfileStrings.CD_AVATAR,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        if (isImageUploading) {
            CircularProgressIndicator(
                modifier = Modifier.size(ProfileDims.AVATAR_PROGRESS_SIZE),
                color = BrandBlue,
                strokeWidth = ProfileDims.AVATAR_PROGRESS_STROKE
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(ProfileDims.AVATAR_EDIT_BADGE_SIZE)
                .clip(CircleShape)
                .background(BrandBlue)
                .border(
                    BorderStroke(ProfileDims.AVATAR_EDIT_BADGE_BORDER_WIDTH, OnBrandContent),
                    CircleShape
                )
                .clickable(onClick = onPickImage),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = ProfileStrings.CD_EDIT_AVATAR,
                tint = OnBrandContent,
                modifier = Modifier.size(ProfileDims.AVATAR_EDIT_ICON_SIZE)
            )
        }
    }
}

@Composable
private fun EditProfileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(ProfileDims.EDIT_PROFILE_BUTTON_HEIGHT),
        shape = RoundedCornerShape(ProfileDims.EDIT_PROFILE_BUTTON_CORNER_RADIUS),
        colors = ButtonDefaults.textButtonColors(
            containerColor = BrandBlue.copy(alpha = 0.1f),
            contentColor = BrandBlue
        ),
        border = BorderStroke(ProfileDims.EDIT_PROFILE_BUTTON_BORDER, BrandBlue.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(horizontal = ProfileDims.EDIT_PROFILE_BUTTON_HORIZONTAL_PADDING)
    ) {
        Text(
            text = ProfileStrings.EDIT_PROFILE,
            fontSize = ProfileDims.EDIT_PROFILE_BUTTON_FONT_SIZE,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun VerifiedBadge(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(ProfileDims.VERIFIED_BADGE_CORNER_RADIUS))
            .background(color.copy(alpha = 0.12f))
            .padding(
                horizontal = ProfileDims.VERIFIED_BADGE_HORIZONTAL_PADDING,
                vertical = ProfileDims.VERIFIED_BADGE_VERTICAL_PADDING
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ProfileDims.VERIFIED_BADGE_ICON_TEXT_SPACING)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(ProfileDims.VERIFIED_BADGE_ICON_SIZE)
        )
        Text(
            text = label,
            color = color,
            fontSize = ProfileDims.VERIFIED_BADGE_FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun ListPropertyBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(ProfileDims.LIST_PROPERTY_BANNER_HEIGHT),
        shape = RoundedCornerShape(ProfileDims.LIST_PROPERTY_BANNER_CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = BrandBlue.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = ProfileDims.LIST_PROPERTY_BANNER_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(ProfileDims.CARD_INNER_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(ProfileDims.LIST_PROPERTY_IMAGE_WIDTH)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(ProfileDims.LIST_PROPERTY_BANNER_IMAGE_CORNER_RADIUS))
                    .background(BrandBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = ProfileStrings.CD_LIST_PROPERTY_IMAGE,
                    tint = BrandBlue,
                    modifier = Modifier.size(ProfileDims.LIST_PROPERTY_IMAGE_ICON_SIZE)
                )
            }

            Spacer(modifier = Modifier.width(ProfileDims.BANNER_CONTENT_SPACING))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ProfileStrings.LIST_PROPERTY_TITLE,
                    color = Black,
                    fontSize = ProfileDims.BANNER_TITLE_FONT_SIZE,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(ProfileDims.BANNER_TITLE_SUBTITLE_SPACING))
                Text(
                    text = ProfileStrings.LIST_PROPERTY_SUBTITLE,
                    color = HomeTextSecondary,
                    fontSize = ProfileDims.BANNER_SUBTITLE_FONT_SIZE
                )
            }

            Spacer(modifier = Modifier.width(ProfileDims.BANNER_CONTENT_SPACING))

            Button(
                onClick = onClick,
                modifier = Modifier.height(ProfileDims.LIST_PROPERTY_BUTTON_HEIGHT),
                shape = RoundedCornerShape(ProfileDims.LIST_PROPERTY_BUTTON_CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandCoral,
                    contentColor = OnBrandContent
                ),
                contentPadding = PaddingValues(horizontal = ProfileDims.LIST_PROPERTY_BUTTON_HORIZONTAL_PADDING)
            ) {
                Text(
                    text = ProfileStrings.LIST_PROPERTY_BUTTON,
                    fontSize = ProfileDims.BANNER_BUTTON_FONT_SIZE,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(ProfileDims.BANNER_BUTTON_ICON_SPACING))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = ProfileStrings.CD_LIST_PROPERTY_ARROW,
                    modifier = Modifier.size(ProfileDims.BANNER_ARROW_ICON_SIZE)
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuSection(
    title: String,
    items: List<ProfileMenuItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = Black,
            fontSize = ProfileDims.SECTION_TITLE_FONT_SIZE,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(ProfileDims.SECTION_TITLE_TO_CONTENT))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(ProfileDims.CARD_CORNER_RADIUS),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(defaultElevation = ProfileDims.CARD_ELEVATION)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                items.forEachIndexed { index, item ->
                    ProfileMenuRow(item = item)
                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = ProfileDims.CARD_INNER_PADDING),
                            thickness = ProfileDims.DIVIDER_THICKNESS,
                            color = HomeSearchBarBorder
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(item: ProfileMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(
                horizontal = ProfileDims.CARD_INNER_PADDING,
                vertical = ProfileDims.MENU_ITEM_VERTICAL_PADDING
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ProfileDims.MENU_ITEM_ICON_SIZE)
                .clip(RoundedCornerShape(ProfileDims.CARD_INNER_ICON_CORNER_RADIUS))
                .background(HomeCategoryUnselected),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.contentDescription,
                tint = BrandBlue,
                modifier = Modifier.size(ProfileDims.MENU_ITEM_ICON_INNER_SIZE)
            )
        }

        Spacer(modifier = Modifier.width(ProfileDims.MENU_ITEM_ICON_TEXT_SPACING))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = Black,
                fontSize = ProfileDims.MENU_ITEM_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Medium
            )
            item.subtitle?.let { subtitle ->
                Spacer(modifier = Modifier.height(ProfileDims.MENU_ITEM_TEXT_SPACING))
                Text(
                    text = subtitle,
                    color = HomeTextSecondary,
                    fontSize = ProfileDims.MENU_ITEM_SUBTITLE_FONT_SIZE
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = ProfileStrings.CD_ARROW,
            tint = HomeTextSecondary,
            modifier = Modifier.size(ProfileDims.MENU_ITEM_ARROW_SIZE)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    user: User?,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf(user?.name.orEmpty()) }
    var phone by rememberSaveable { mutableStateOf(user?.phone.orEmpty()) }
    var email by rememberSaveable { mutableStateOf(user?.email.orEmpty()) }
    var address by rememberSaveable { mutableStateOf(user?.address.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(ProfileDims.DIALOG_CORNER_RADIUS),
        containerColor = White,
        title = {
            Text(
                text = ProfileStrings.EDIT_PROFILE,
                color = Black,
                fontSize = ProfileDims.DIALOG_TITLE_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(ProfileDims.FIELD_SPACING)
            ) {
                EditProfileField(
                    value = name,
                    onValueChange = { name = it },
                    label = ProfileStrings.LABEL_NAME,
                    icon = Icons.Filled.Person,
                    keyboardType = KeyboardType.Text
                )
                EditProfileField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = ProfileStrings.LABEL_PHONE,
                    icon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone
                )
                EditProfileField(
                    value = email,
                    onValueChange = { email = it },
                    label = ProfileStrings.LABEL_EMAIL,
                    icon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email
                )
                EditProfileField(
                    value = address,
                    onValueChange = { address = it },
                    label = ProfileStrings.LABEL_ADDRESS,
                    icon = Icons.Filled.LocationOn,
                    keyboardType = KeyboardType.Text
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name != user?.name.orEmpty()) onSave(ProfileStrings.FIELD_NAME, name)
                    if (phone != user?.phone.orEmpty()) onSave(ProfileStrings.FIELD_PHONE, phone)
                    if (email != user?.email.orEmpty()) onSave(ProfileStrings.FIELD_EMAIL, email)
                    if (address != user?.address.orEmpty()) onSave(ProfileStrings.FIELD_ADDRESS, address)
                    onDismiss()
                },
                modifier = Modifier.height(ProfileDims.DIALOG_BUTTON_HEIGHT),
                shape = RoundedCornerShape(ProfileDims.DIALOG_BUTTON_CORNER_RADIUS),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = BrandBlue,
                    contentColor = OnBrandContent
                )
            ) {
                Text(
                    text = ProfileStrings.ACTION_SAVE,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = ProfileStrings.ACTION_CANCEL,
                    color = HomeTextSecondary
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = HomeTextSecondary,
                modifier = Modifier.size(ProfileDims.DIALOG_FIELD_ICON_SIZE)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(ProfileDims.DIALOG_FIELD_CORNER_RADIUS),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Black,
            unfocusedTextColor = Black,
            focusedBorderColor = BrandBlue,
            unfocusedBorderColor = HomeSearchBarBorder,
            focusedLabelColor = BrandBlue,
            unfocusedLabelColor = HomeTextSecondary
        )
    )
}

private const val PROFILE_LOGIN_PROMPT_TAG = "ProfileLoginPrompt"
private const val PROFILE_MENU_TAG = "ProfileMenu"

@Composable
private fun ProfileLoginPrompt(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Logged-out profile UI with a direct action to open the login screen.
    Column(
        modifier = modifier.padding(ProfileDims.LOGIN_PROMPT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = BrandBlue,
            modifier = Modifier.size(ProfileDims.EMPTY_ICON_SIZE)
        )
        Spacer(modifier = Modifier.height(ProfileDims.LOGIN_TITLE_TOP_SPACING))
        Text(
            text = ProfileStrings.LOGIN_PROMPT_TITLE,
            color = Black,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(ProfileDims.LOGIN_BUTTON_TOP_SPACING))
        Button(
            onClick = {
                Logger.d(PROFILE_LOGIN_PROMPT_TAG, "Login button clicked from profile screen")
                onLoginClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(ProfileDims.LOGIN_BUTTON_HEIGHT),
            shape = RoundedCornerShape(ProfileDims.LOGIN_BUTTON_CORNER_RADIUS),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandBlue,
                contentColor = OnBrandContent
            )
        ) {
            Text(
                text = ProfileStrings.LOGIN_BUTTON,
                fontSize = ProfileDims.LOGIN_BUTTON_FONT_SIZE,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private const val PROFILE_IMAGE_MAX_DIM = 1024

private fun processImageFromUri(context: Context, uri: Uri): ByteArray? {
    return try {
        val (width, height) = context.contentResolver.openInputStream(uri)?.use { stream ->
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(stream, null, options)
            options.outWidth to options.outHeight
        } ?: return null

        context.contentResolver.openInputStream(uri)?.use { stream ->
            val options = BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(width, height, PROFILE_IMAGE_MAX_DIM)
            }
            BitmapFactory.decodeStream(stream, null, options)
                ?.cropToSquare()
                ?.resize(PROFILE_IMAGE_MAX_DIM)
                ?.toJpegBytes()
        }
    } catch (e: Exception) {
        null
    }
}

private fun createImageUri(context: Context): Uri? {
    return try {
        val file = File(context.cacheDir, CAMERA_FILE_PREFIX + System.currentTimeMillis() + IMAGE_FILE_EXTENSION)
        file.createNewFile()
        FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file)
    } catch (e: Exception) {
        null
    }
}

private fun Bitmap.cropToSquare(): Bitmap {
    val size = minOf(width, height)
    val x = (width - size) / 2
    val y = (height - size) / 2
    return Bitmap.createBitmap(this, x, y, size, size)
}

private fun Bitmap.resize(maxDim: Int): Bitmap {
    if (width <= maxDim && height <= maxDim) return this
    val scale = maxDim.toFloat() / maxOf(width, height)
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}

private fun calculateInSampleSize(width: Int, height: Int, maxDim: Int): Int {
    var inSampleSize = 1
    while (width / inSampleSize >= maxDim || height / inSampleSize >= maxDim) {
        inSampleSize *= 2
    }
    return inSampleSize
}

private const val PROFILE_IMAGE_FILENAME = "profile_image.jpg"
private const val IMAGE_MIME_TYPE = "image/*"
private const val CAMERA_FILE_PREFIX = "profile_camera_"
private const val IMAGE_FILE_EXTENSION = ".jpg"
private const val FILE_PROVIDER_AUTHORITY = "com.realeapp.fileprovider"
private const val MAILTO_SCHEME = "mailto:"

private fun launchEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse(MAILTO_SCHEME)
        putExtra(Intent.EXTRA_EMAIL, arrayOf(ProfileStrings.SUPPORT_EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, ProfileStrings.EMAIL_SUBJECT)
        putExtra(Intent.EXTRA_TEXT, ProfileStrings.EMAIL_BODY)
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(Intent.createChooser(intent, ProfileStrings.EMAIL_CHOOSER_TITLE))
    } else {
        Toast.makeText(context, ProfileStrings.ERROR_NO_EMAIL_APP, Toast.LENGTH_SHORT).show()
    }
}
