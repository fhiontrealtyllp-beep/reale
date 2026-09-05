package com.realeapp.feature.add.presentation

enum class AddPropertyStep(
    val title: String,
    val subtitle: String
) {
    BASIC_DETAILS("1. Basic Details", "Property type, purpose, location"),
    PROPERTY_DETAILS("2. Property Details", "Add key details about your property"),
    PHOTOS_MEDIA("3. Photos & Media", "Showcase your property with high-quality photos"),
    PRICING("4. Pricing", "Set the price and availability details"),
    REVIEW_PUBLISH("5. Review & Publish", "Please check all details before publishing");

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
