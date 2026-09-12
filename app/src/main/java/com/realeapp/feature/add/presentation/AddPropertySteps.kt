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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realeapp.ui.theme.AppBackground

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
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = AppBackground,
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
                        ValidationErrorList(errors = uiState.fieldErrors)

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AddDims.SECTION_SPACING)
        ) {
            StepIndicator(
                steps = AddPropertyStep.all,
                currentStep = uiState.currentStep,
                modifier = Modifier.fillMaxWidth()
            )

            when (uiState.currentStep) {
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
                AddPropertyStep.PHOTOS_MEDIA -> AddPropertyStep3Screen(
                    images = form.images,
                    isUploadingImage = uiState.isUploadingImage,
                    uploadError = uiState.imageUploadError,
                    onAddMore = { showImageSourceDialog = true },
                    onRemoveImage = viewModel::removeImageUrl,
                    modifier = Modifier.fillMaxWidth()
                )
                AddPropertyStep.PRICING -> AddPropertyStep4Screen(
                    form = form,
                    onPriceChanged = viewModel::onPriceChanged,
                    onAgentPhoneChanged = viewModel::onAgentPhoneChanged,
                    onListingCategoryChanged = viewModel::onListingCategoryChanged
                )
                AddPropertyStep.REVIEW_PUBLISH -> AddPropertyStep5Screen(
                    form = form,
                    onSubmit = viewModel::submit,
                    onBack = viewModel::previousStep,
                    onEdit = { viewModel.goToStep(AddPropertyStep.PHOTOS_MEDIA) },
                    isSubmitting = uiState.isSubmitting
                )
            }

            Spacer(modifier = Modifier.height(AddDims.SECTION_SPACING))
        }
    }
}
