package com.realeapp.feature.search.presentation

enum class SortBy(val label: String) {
    RELEVANCE(PropertiesStrings.SORT_RELEVANCE),
    PRICE_LOW_HIGH(PropertiesStrings.SORT_PRICE_LOW_HIGH),
    PRICE_HIGH_LOW(PropertiesStrings.SORT_PRICE_HIGH_LOW),
    NEWEST(PropertiesStrings.SORT_NEWEST)
}
