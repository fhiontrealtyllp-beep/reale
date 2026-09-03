package com.realeapp.feature.profile.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.realeapp.feature.profile.di.ProfileModule
import com.realeapp.feature.profile.presentation.ProfileViewModel
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

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri ->
            val bytes = readBytesFromUri(context, imageUri)
            if (bytes != null) {
                viewModel.uploadImage(bytes, "profile_image.jpg")
            }
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFF141C3D),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1C2755),
                    contentColor = Color(0xFFFBFBFB)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = Color(0xFFFBFBFB),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF141C3D)
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
                // Full-screen loading UI shown while the initial profile is being fetched.
                uiState.isLoading && uiState.user == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFDD60D)
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
                    isLoading = uiState.isLoading,
                    onPickImage = { imagePicker.launch("image/*") },
                    onUpdateField = viewModel::updateProfileField,
                    onLogout = viewModel::logout,
                    onWriteToUs = { launchEmail(context) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User?,
    isLoading: Boolean,
    onPickImage: () -> Unit,
    onUpdateField: (String, String) -> Unit,
    onLogout: () -> Unit,
    onWriteToUs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        // Profile identity UI: avatar, display name, and email address.
        ProfileHeader(
            user = user,
            onPickImage = onPickImage,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile details UI: editable name, phone, and address; email is read-only.
            user?.let {
                EditableProfileField(
                    label = "Name",
                    value = it.name,
                    icon = Icons.Default.Person,
                    onSave = { newValue -> onUpdateField("name", newValue) },
                    keyboardType = KeyboardType.Text
                )

                EditableProfileField(
                    label = "Email",
                    value = it.email,
                    icon = Icons.Default.Email,
                    onSave = null,
                    keyboardType = KeyboardType.Email
                )

                EditableProfileField(
                    label = "Phone",
                    value = it.phone,
                    icon = Icons.Default.Phone,
                    onSave = { newValue -> onUpdateField("phone", newValue) },
                    keyboardType = KeyboardType.Phone
                )

                EditableProfileField(
                    label = "Address",
                    value = it.address,
                    icon = Icons.Default.LocationOn,
                    onSave = { newValue -> onUpdateField("address", newValue) },
                    keyboardType = KeyboardType.Text,
                    isMultiline = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFF8F9FDC).copy(alpha = 0.3f))

            // Profile action UI: contact support and log out of the current session.
            ProfileListItem(
                title = "Write to us",
                icon = Icons.AutoMirrored.Filled.Send,
                onClick = onWriteToUs
            )

            ProfileListItem(
                title = "Logout",
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = onLogout
            )

            // Inline progress UI shown while a profile action is running.
            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressPlaceholder()
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User?,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(200.dp)
            .background(
                Color(0xFF1C2755),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDD60D))
                    .clickable { onPickImage() },
                contentAlignment = Alignment.Center
            ) {
                // Avatar UI falls back to the user's initials when no image is available.
                if (user?.image.isNullOrBlank()) {
                    Text(
                        text = user?.name?.take(2)?.uppercase() ?: "?",
                        color = Color(0xFF141C3D),
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user?.name ?: "Guest",
                color = Color(0xFFFBFBFB),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = user?.email ?: "",
                color = Color(0xFFFBFBFB).copy(alpha = 0.7f),
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
            tint = Color(0xFFFDD60D),
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color(0xFF8F9FDC),
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
                        focusedTextColor = Color(0xFFFBFBFB),
                        unfocusedTextColor = Color(0xFFFBFBFB),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color(0xFFFDD60D),
                        unfocusedBorderColor = Color(0xFF8F9FDC)
                    )
                )
            } else {
                Text(
                    text = value.ifBlank { "N/A" },
                    color = Color(0xFFFBFBFB),
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
                            tint = Color(0xFFFDD60D)
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
                            tint = Color(0xFFFBFBFB)
                        )
                    }
                }
            } else {
                IconButton(onClick = { isEditing = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFFFBFBFB)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileListItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFDD60D),
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )

        Text(
            text = title,
            color = Color(0xFFFBFBFB),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        if (title != "Logout") {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFFBFBFB).copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
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
            tint = Color(0xFFFDD60D),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please Login to View your profile",
            color = Color(0xFFFBFBFB),
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
                containerColor = Color(0xFFFDD60D),
                contentColor = Color(0xFF141C3D)
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
            .background(Color(0xFFFDD60D))
    )
}

private fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
    } catch (e: Exception) {
        null
    }
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
