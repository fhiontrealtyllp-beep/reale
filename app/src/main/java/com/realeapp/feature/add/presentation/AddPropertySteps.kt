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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realeapp.ui.theme.MainBackground

@Composable
fun AddPropertySteps(
    uiState: AddUiState,
    viewModel: AddViewModel,
    modifier: Modifier = Modifier
) {
    val form = uiState.form
    var showLocationPicker by remember { mutableStateOf(false) }

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
        containerColor = MainBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepHeader(step = uiState.currentStep)

            StepIndicator(
                steps = AddPropertyStep.all,
                currentStep = uiState.currentStep,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                    onPickOnMap = { showLocationPicker = true },
                    onUseMyLocation = { showLocationPicker = true }
                )
                AddPropertyStep.PROPERTY_DETAILS -> AddPropertyStep2Screen(
                    form = form,
                    onBedroomCountChanged = viewModel::onBedroomCountChanged,
                    onBathroomsChanged = viewModel::onBathroomsChanged,
                    onFurnishingChanged = viewModel::onFurnishingChanged,
                    onAgeChanged = viewModel::onAgeChanged,
                    onFloorNoChanged = viewModel::onFloorNoChanged,
                    onTotalFloorsChanged = viewModel::onTotalFloorsChanged,
                    onFacingChanged = viewModel::onFacingChanged,
                    onAmenitiesChanged = viewModel::onAmenitiesChanged,
                    onBuiltUpAreaChanged = viewModel::onBuiltUpAreaChanged,
                    onPlotAreaChanged = viewModel::onPlotAreaChanged,
                    onVideoChanged = viewModel::onVideoChanged
                )
                AddPropertyStep.PHOTOS_MEDIA -> AddPropertyStep3Screen(
                    images = form.images,
                    isUploadingImage = uiState.isUploadingImage,
                    imageUploadError = uiState.imageUploadError,
                    onUploadImages = viewModel::uploadImages,
                    onRemoveImage = viewModel::removeImageUrl
                )
                AddPropertyStep.PRICING -> AddPropertyStep4Screen(
                    form = form,
                    onPriceChanged = viewModel::onPriceChanged,
                    onPriceModeChanged = viewModel::onPriceModeChanged,
                    onNegotiableChanged = viewModel::onNegotiableChanged,
                    onAdditionalCostsChanged = viewModel::onAdditionalCostsChanged,
                    onPropertyStatusChanged = viewModel::onPropertyStatusChanged,
                    onPossessionDateChanged = viewModel::onPossessionDateChanged
                )
                AddPropertyStep.REVIEW_PUBLISH -> AddPropertyStep5Screen(
                    form = form,
                    onSubmit = viewModel::submit,
                    onBack = viewModel::previousStep,
                    onEdit = { viewModel.goToStep(AddPropertyStep.PHOTOS_MEDIA) },
                    isSubmitting = uiState.isSubmitting
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ValidationErrorList(errors = uiState.fieldErrors)

            if (uiState.currentStep != AddPropertyStep.REVIEW_PUBLISH) {
                StepNavigationButtons(
                    onPrevious = viewModel::previousStep,
                    onNext = viewModel::nextStep,
                    nextLabel = if (uiState.currentStep == AddPropertyStep.PRICING) "Review" else "Continue",
                    showPrevious = !uiState.currentStep.isFirst
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
