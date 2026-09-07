package com.realeapp

/**
 * App-wide string constants shared across all features.
 *
 * This is the single source of truth for brand copy such as the app name,
 * so it is not duplicated in individual feature string objects.
 */
internal object AppStrings {
    const val APP_NAME = "HomeFinder"

    private const val APP_NAME_SPLIT_INDEX = 4

    /** First (blue) part of the logo, derived from [APP_NAME]. */
    val APP_NAME_FIRST = APP_NAME.take(APP_NAME_SPLIT_INDEX)

    /** Second (accent) part of the logo, derived from [APP_NAME]. */
    val APP_NAME_ACCENT = APP_NAME.drop(APP_NAME_SPLIT_INDEX)
}
