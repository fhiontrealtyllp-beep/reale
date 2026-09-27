package com.fhiont.core.notification

internal object PushNotificationConstants {
    const val CHANNEL_ID = "enquiries_v2"
    const val OLD_CHANNEL_ID = "enquiries"
    const val CHANNEL_NAME = "Property enquiries"
    const val CHANNEL_DESCRIPTION = "Notifications when someone enquires about your property"
    const val DEFAULT_TITLE = "New property enquiry"
    const val DEFAULT_MESSAGE = "Someone is interested in your property"
    const val EXTRA_PROPERTY_ID = "propertyId"
    const val EXTRA_ENQUIRY_ID = "enquiryId"
    const val EXTRA_PROPERTY_TITLE = "propertyTitle"
    const val EXTRA_PROPERTY_LOCATION = "propertyLocation"
    const val EXTRA_PROPERTY_IMAGE = "propertyImage"
    const val EXTRA_AGENT_PHONE = "agentPhone"
    const val EXTRA_MESSAGE = "message"
    const val EXTRA_USER_ID = "userId"
    const val EXTRA_USER_NAME = "userName"
    const val EXTRA_STATUS = "status"
    const val EXTRA_CREATED_AT = "createdAt"
    const val PLATFORM_ANDROID = "android"
    const val NOTIFICATION_ID_BASE = 10_000
}
