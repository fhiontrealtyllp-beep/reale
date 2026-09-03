package com.realeapp.feature.profile.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView.Guidelines
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.add.presentation.ImageSourceDialog
import com.realeapp.feature.add.presentation.toJpegBytes
import com.realeapp.feature.profile.di.ProfileModule
import com.realeapp.feature.profile.presentation.ProfileViewModel
import com.realeapp.ui.theme.Accent
import com.realeapp.ui.theme.AppBackground
import com.realeapp.ui.theme.CardBackground
import com.realeapp.ui.theme.ItemCardBackground
import com.realeapp.ui.theme.TextPrimary
import com.realeapp.ui.theme.TextSecondary
import com.realeapp.util.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = ProfileModule.viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                viewModel.uploadImage(bytes, "profile_image.jpg")
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
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CardBackground,
                    contentColor = TextPrimary
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
                        color = Accent
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
                    updatingField = uiState.updatingField,
                    onPickImage = { showImageSourceDialog = true },
                    onLogout = viewModel::logout,
                    onWriteToUs = { launchEmail(context) },
                    onUpdateField = { field, value -> viewModel.updateProfileField(field, value) },
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
                        imagePicker.launch("image/*")
                    },
                    onDismiss = { showImageSourceDialog = false }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User?,
    isImageUploading: Boolean,
    updatingField: String?,
    onPickImage: () -> Unit,
    onLogout: () -> Unit,
    onWriteToUs: () -> Unit,
    onUpdateField: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        // Profile header card: title, edit action, avatar, name and email.
        ProfileHeader(
            user = user,
            isLoading = isImageUploading,
            onPickImage = onPickImage,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Section label for account details.
            Text(
                text = "Account Details",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            user?.let {
                // Name info item.
                ProfileInfoItem(
                    label = "Name",
                    value = it.name,
                    icon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    isSaving = updatingField == "name",
                    onSave = { value -> onUpdateField("name", value) }
                )

                // Email info item.
                ProfileInfoItem(
                    label = "Email",
                    value = it.email,
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    isSaving = updatingField == "email",
                    onSave = { value -> onUpdateField("email", value) }
                )

                // Phone info item.
                ProfileInfoItem(
                    label = "Phone",
                    value = it.phone,
                    icon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    isSaving = updatingField == "phone",
                    onSave = { value -> onUpdateField("phone", value) }
                )

                // Address info item.
                ProfileInfoItem(
                    label = "Address",
                    value = it.address,
                    icon = Icons.Default.LocationOn,
                    keyboardType = KeyboardType.Text,
                    isMultiline = true,
                    isSaving = updatingField == "address",
                    onSave = { value -> onUpdateField("address", value) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Write to us action item.
            ProfileInfoItem(
                label = "Write to us",
                value = null,
                icon = Icons.AutoMirrored.Filled.Send,
                onClick = onWriteToUs
            )

            // Logout action item.
            ProfileInfoItem(
                label = "Logout",
                value = null,
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = onLogout
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User?,
    isLoading: Boolean,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header row with screen title.
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Profile",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Avatar with accent border. Tap to change profile picture.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(92.dp)
                    .clickable { onPickImage() }
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(AppBackground)
                        .border(BorderStroke(2.dp, Accent), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Avatar UI falls back to the user's initials when no image is available.
                    if (user?.image.isNullOrBlank()) {
                        Text(
                            text = user?.name?.take(2)?.uppercase() ?: "?",
                            color = Accent,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        AsyncImage(
                            model = user?.image,
                            contentDescription = "Profile picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Accent,
                        strokeWidth = 3.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.name ?: "Guest",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user?.email ?: "",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableProfileField(
    label: String,
    value: String,
    icon: ImageVector,
    onSave: ((String) -> Unit)?,
    keyboardType: KeyboardType,
    isMultiline: Boolean = false
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(value) }

    LaunchedEffect(value) {
        if (!isEditing) {
            text = value
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp
            )

            // Field value UI switches between editor and read-only text.
            if (isEditing) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = !isMultiline,
                    minLines = if (isMultiline) 2 else 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = if (isMultiline) ImeAction.Default else ImeAction.Done
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Accent,
                        unfocusedBorderColor = TextSecondary
                    )
                )
            } else {
                Text(
                    text = value.ifBlank { "N/A" },
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Edit action UI is omitted for read-only fields such as email.
        if (onSave != null) {
            if (isEditing) {
                Row {
                    IconButton(
                        onClick = {
                            onSave(text)
                            isEditing = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save",
                            tint = Accent
                        )
                    }
                    IconButton(
                        onClick = {
                            text = value
                            isEditing = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = TextPrimary
                        )
                    }
                }
            } else {
                IconButton(onClick = { isEditing = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String?,
    icon: ImageVector,
    showArrow: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isMultiline: Boolean = false,
    isSaving: Boolean = false,
    onClick: (() -> Unit)? = null,
    onSave: ((String) -> Unit)? = null
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(value.orEmpty()) }

    LaunchedEffect(value) {
        if (!isEditing) {
            text = value.orEmpty()
        }
    }

    LaunchedEffect(isSaving) {
        if (!isSaving) {
            text = value.orEmpty()
            if (isEditing) {
                isEditing = false
            }
        }
    }

    val isEditable = onSave != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(
                enabled = (!isSaving && !isEditing && isEditable) || (onClick != null && !isSaving),
                onClick = {
                    when {
                        isEditable && !isEditing -> isEditing = true
                        onClick != null -> onClick()
                    }
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ItemCardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { if (!isSaving) text = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(label, color = TextSecondary) },
                        singleLine = !isMultiline,
                        minLines = if (isMultiline) 2 else 1,
                        readOnly = isSaving,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (!isSaving) {
                                    onSave?.invoke(text)
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = TextSecondary,
                            focusedLabelColor = TextSecondary,
                            unfocusedLabelColor = TextSecondary
                        )
                    )
                } else {
                    Text(
                        text = label,
                        color = if (value == null) TextPrimary else TextSecondary,
                        fontSize = if (value == null) 16.sp else 12.sp,
                        fontWeight = if (value == null) FontWeight.Medium else FontWeight.Normal
                    )

                    if (value != null) {
                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = value.ifBlank { "N/A" },
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (isEditing) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Accent,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = { onSave?.invoke(text) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save",
                            tint = Accent
                        )
                    }
                }
            } else if (showArrow) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = null,
                    tint = TextPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


private const val PROFILE_LOGIN_PROMPT_TAG = "ProfileLoginPrompt"

@Composable
private fun ProfileLoginPrompt(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Logged-out profile UI with a direct action to open the login screen.
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please Login to View your profile",
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = {
                Logger.d(PROFILE_LOGIN_PROMPT_TAG, "Login button clicked from profile screen")
                onLoginClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = Accent,
                contentColor = AppBackground
            )
        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LinearProgressPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Accent)
    )
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
        val file = File(context.cacheDir, "profile_camera_${System.currentTimeMillis()}.jpg")
        file.createNewFile()
        FileProvider.getUriForFile(context, "com.realeapp.fileprovider", file)
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

private fun launchEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("realeapp25@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Support Request")
        putExtra(Intent.EXTRA_TEXT, "Hi team,")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}
