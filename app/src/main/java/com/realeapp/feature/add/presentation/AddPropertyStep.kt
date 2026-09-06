package com.realeapp.feature.add.presentation

enum class AddPropertyStep(
    val title: String,
    val subtitle: String,
    val shortLabel: String
) {
    BASIC_DETAILS(AddStrings.STEP1_TITLE, AddStrings.STEP1_SUBTITLE, AddStrings.SECTION_BASIC_DETAILS),
    PROPERTY_DETAILS(AddStrings.STEP2_TITLE, AddStrings.STEP2_SUBTITLE, AddStrings.SECTION_PROPERTY_DETAILS),
    PHOTOS_MEDIA(AddStrings.STEP3_TITLE, AddStrings.STEP3_SUBTITLE, AddStrings.STEP_LABEL_PHOTOS),
    PRICING(AddStrings.STEP4_TITLE, AddStrings.STEP4_SUBTITLE, AddStrings.STEP_LABEL_PRICING),
    REVIEW_PUBLISH(AddStrings.STEP5_TITLE, AddStrings.STEP5_SUBTITLE, AddStrings.STEP_LABEL_REVIEW);

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
