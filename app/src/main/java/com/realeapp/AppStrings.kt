package com.realeapp

/**
 * App-wide string constants shared across all features.
 *
 * This is the single source of truth for brand copy such as the app name,
 * so it is not duplicated in individual feature string objects.
 */
internal object AppStrings {
    const val APP_NAME = "HomeFinder"

    /** First (blue) part of the logo, derived from [APP_NAME]. */
    val APP_NAME_FIRST = APP_NAME.dropLast(1)

    /** Final (accent) part of the logo, derived from [APP_NAME]. */
    val APP_NAME_ACCENT = APP_NAME.takeLast(1)
}
