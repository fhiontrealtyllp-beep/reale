package com.fhiont.feature.add.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.fhiont.feature.add.domain.model.PropertyForm
import com.fhiont.feature.search.domain.model.ListingCategory
import com.fhiont.ui.preview.PreviewData
import com.fhiont.AppStrings
import com.fhiont.ui.theme.FhiontTheme

@Composable
internal fun AddPropertyStep4Screen(
    form: PropertyForm,
    onPriceChanged: (String) -> Unit,
    onAgentPhoneChanged: (String) -> Unit,
    onListingCategoryChanged: (ListingCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AddDims.SECTION_SPACING)
    ) {
        SectionHeader(AddStrings.SECTION_PRICE_DETAILS)
        FormTextField(
            value = form.price,
            onValueChange = onPriceChanged,
            label = AddStrings.LABEL_PRICE,
            isRequired = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        SectionHeader(AddStrings.SECTION_CONTACT_DETAILS)
        FormTextField(
            value = form.agentPhone,
            onValueChange = { onAgentPhoneChanged(it.filter(Char::isDigit).take(AppStrings.PHONE_MAX_LENGTH)) },
            label = AddStrings.LABEL_AGENT_PHONE,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        FormDropdown(
            label = AddStrings.LABEL_LISTING_CATEGORY,
            options = ListingCategory.entries,
            selected = form.listingCategory,
            optionLabel = { it.label },
            onSelected = onListingCategoryChanged,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddPropertyStep4ScreenPreview() {
    FhiontTheme {
        AddPropertyStep4Screen(
            form = PreviewData.samplePropertyForm,
            onPriceChanged = {},
            onAgentPhoneChanged = {},
            onListingCategoryChanged = {}
        )
    }
}
