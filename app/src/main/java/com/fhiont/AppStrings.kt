package com.fhiont

/**
 * App-wide string constants shared across all features.
 *
 * This is the single source of truth for brand copy such as the app name,
 * so it is not duplicated in individual feature string objects.
 */
internal object AppStrings {
    const val APP_NAME = "Fhiont"

    private const val APP_NAME_SPLIT_INDEX = 4

    /** First (blue) part of the logo, derived from [APP_NAME]. */
    val APP_NAME_FIRST = APP_NAME.take(APP_NAME_SPLIT_INDEX)

    /** Second (accent) part of the logo, derived from [APP_NAME]. */
    val APP_NAME_ACCENT = APP_NAME.drop(APP_NAME_SPLIT_INDEX)

    const val PHONE_MAX_LENGTH = 10

    const val EXIT_DIALOG_TITLE = "Exit"
    const val EXIT_DIALOG_MESSAGE = "Do you want to exit the app?"
    const val EXIT_CONFIRM = "Exit"
    const val DIALOG_CANCEL = "Cancel"
}
