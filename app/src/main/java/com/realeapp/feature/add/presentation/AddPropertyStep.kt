package com.realeapp.feature.add.presentation

enum class AddPropertyStep(val shortLabel: String) {
    BASIC_DETAILS(AddStrings.SECTION_BASIC_DETAILS),
    PROPERTY_DETAILS(AddStrings.SECTION_PROPERTY_DETAILS),
    PHOTOS_MEDIA(AddStrings.STEP_LABEL_PHOTOS),
    PRICING(AddStrings.STEP_LABEL_PRICING),
    REVIEW_PUBLISH(AddStrings.STEP_LABEL_REVIEW);

    val index: Int get() = ordinal + 1
    val isFirst: Boolean get() = this == BASIC_DETAILS
    val isLast: Boolean get() = this == REVIEW_PUBLISH

    companion object {
        val all = values().toList()

        fun fromIndex(index: Int): AddPropertyStep {
            return all.getOrElse(index - 1) { BASIC_DETAILS }
        }
    }
}
