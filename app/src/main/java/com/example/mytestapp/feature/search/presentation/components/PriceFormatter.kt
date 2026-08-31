package com.example.mytestapp.feature.search.presentation.components

import java.text.NumberFormat
import java.util.Locale

fun formatIndianPrice(price: Double, isRent: Boolean = false): String {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))
    val suffix = if (isRent) " / month" else ""

    val value = when {
        price >= 1_00_00_000 -> "%.2f Cr".format(price / 1_00_00_000)
        price >= 1_00_000 -> "%.2f L".format(price / 1_00_000)
        else -> formatter.format(price)
    }
    return "₹ $value$suffix"
}
