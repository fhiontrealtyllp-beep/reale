package com.realeapp.feature.add.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realeapp.ui.theme.AppBackground

/**
 * Host for the multi-step add-property form. Shows the step indicator, the
 * current step's screen, and the bottom navigation bar with validation
 * errors plus Previous/Continue buttons.
 */
@Composable
fun AddPropertySteps(
    uiState: AddUiState,
    viewModel: AddViewModel,
    modifier: Modifier = Modifier
) {
    val form = uiState.form
    var showLocationPicker by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    val imageLaunchers = rememberImageLaunchers(viewModel::uploadImages)
    val scrollState = rememberScrollState()

    // Reset scroll to top whenever the step changes so the first field is visible.
    LaunchedEffect(uiState.currentStep) {
        scrollState.scrollTo(0)
    }

    // Camera/gallery picker dialog for the photos step.
    if (showImageSourceDialog) {
        ImageSourceDialog(
            onCamera = {
                showImageSourceDialog = false
                imageLaunchers.camera()
            },
            onGallery = {
                showImageSourceDialog = false
                imageLaunchers.gallery()
            },
            onDismiss = { showImageSourceDialog = false }
        )
    }

    // Map picker dialog for choosing the property location.
    if (showLocationPicker) {
        com.realeapp.feature.add.presentation.LocationPickerDialog(
            initialLat = form.latitude,
            initialLng = form.longitude,
            onDismiss = { showLocationPicker = false },
            onConfirm = { lat, lng, city, locality, pincode, address ->
                viewModel.onLocationPicked(lat, lng, city, locality, pincode, address)
                showLocationPicker = false
            }
        )
    }

    Scaffold(
        modifier = modifier.imePadding(),
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = AppBackground,
        // Bottom bar: validation errors + Previous/Continue navigation.
        // Hidden on the review step, which has its own Publish button.
        bottomBar = {
            if (uiState.currentStep != AddPropertyStep.REVIEW_PUBLISH) {
                Surface(color = AppBackground) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = AddDims.SCREEN_PADDING,
                                vertical = AddDims.SCREEN_PADDING
                            ),
                        verticalArrangement = Arrangement.spacedBy(AddDims.FIELD_LABEL_SPACING)
                    ) {
                        // Red validation error texts for the current step.
                        ValidationErrorList(errors = uiState.fieldErrors)

                        // Previous/Continue buttons (Continue becomes Review on the pricing step).
                        StepNavigationButtons(
                            onPrevious = viewModel::previousStep,
                            onNext = viewModel::nextStep,
                            nextLabel = if (uiState.currentStep == AddPropertyStep.PRICING) AddStrings.ACTION_REVIEW else AddStrings.ACTION_CONTINUE,
                            showPrevious = !uiState.currentStep.isFirst
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = AddDims.SCREEN_PADDING, vertical = AddDims.SCREEN_PADDING)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(AddDims.SECTION_SPACING)
        ) {
            // Horizontal step indicator; tapping a step validates forward jumps.
            StepIndicator(
                steps = AddPropertyStep.all,
                currentStep = uiState.currentStep,
                onStepClick = viewModel::onStepClicked,
                modifier = Modifier.fillMaxWidth()
            )

            // The active step's form screen.
            when (uiState.currentStep) {
                // Step 1: title, listing type, property type, location fields.
                AddPropertyStep.BASIC_DETAILS -> AddPropertyStep1Screen(
                    form = form,
                    onRentBuyChanged = viewModel::onRentBuyChanged,
                    onPropertyTypeChanged = viewModel::onPropertyTypeChanged,
                    onTitleChanged = viewModel::onTitleChanged,
                    onDescriptionChanged = viewModel::onDescriptionChanged,
                    onCityChanged = viewModel::onCityChanged,
                    onLocalityChanged = viewModel::onLocalityChanged,
                    onPincodeChanged = viewModel::onPincodeChanged,
                    onAddressChanged = viewModel::onAddressChanged,
                    onUseMyLocation = { showLocationPicker = true }
                )
                // Step 2: bedrooms, bathrooms, furnishing, areas, amenities.
                AddPropertyStep.PROPERTY_DETAILS -> AddPropertyStep2Screen(
                    form = form,
                    onBedroomCountChanged = viewModel::onBedroomCountChanged,
                    onBathroomsChanged = viewModel::onBathroomsChanged,
                    onFurnishingChanged = viewModel::onFurnishingChanged,
                    onAgeChanged = viewModel::onAgeChanged,
                    onFacingChanged = viewModel::onFacingChanged,
                    onAmenitiesChanged = viewModel::onAmenitiesChanged,
                    onCarpetAreaChanged = viewModel::onCarpetAreaChanged,
                    onBuiltUpAreaChanged = viewModel::onBuiltUpAreaChanged,
                    onSuperBuiltUpAreaChanged = viewModel::onSuperBuiltUpAreaChanged
                )
                // Step 3: photo grid with add/remove and upload progress.
                AddPropertyStep.PHOTOS_MEDIA -> AddPropertyStep3Screen(
                    images = form.images,
                    isUploadingImage = uiState.isUploadingImage,
                    uploadError = uiState.imageUploadError,
                    onAddMore = { showImageSourceDialog = true },
                    onRemoveImage = viewModel::removeImageUrl,
                    modifier = Modifier.fillMaxWidth()
                )
                // Step 4: price, listing category, contact phone.
                AddPropertyStep.PRICING -> AddPropertyStep4Screen(
                    form = form,
                    onPriceChanged = viewModel::onPriceChanged,
                    onAgentPhoneChanged = viewModel::onAgentPhoneChanged,
                    onListingCategoryChanged = viewModel::onListingCategoryChanged
                )
                // Step 5: summary review with edit shortcuts and publish action.
                AddPropertyStep.REVIEW_PUBLISH -> AddPropertyStep5Screen(
                    form = form,
                    onSubmit = viewModel::submit,
                    onBack = viewModel::previousStep,
                    onEdit = { viewModel.goToStep(AddPropertyStep.PHOTOS_MEDIA) },
                    isSubmitting = uiState.isSubmitting
                )
            }

            // Bottom spacing so the last field clears the navigation bar.
            Spacer(modifier = Modifier.height(AddDims.SECTION_SPACING))
        }
    }
}
