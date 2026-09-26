package com.fhiont.core.notification

internal object PushNotificationConstants {
    const val CHANNEL_ID = "enquiries"
    const val CHANNEL_NAME = "Property enquiries"
    const val CHANNEL_DESCRIPTION = "Notifications when someone enquires about your property"
    const val DEFAULT_TITLE = "New property enquiry"
    const val DEFAULT_MESSAGE = "Someone is interested in your property"
    const val EXTRA_PROPERTY_ID = "propertyId"
    const val EXTRA_ENQUIRY_ID = "enquiryId"
    const val PLATFORM_ANDROID = "android"
    const val NOTIFICATION_ID_BASE = 10_000
}
