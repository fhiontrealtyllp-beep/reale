package com.realeapp.feature.search.domain.model

object LocationNormalizer {

    fun normalizeCity(value: String?): String? {
        return value?.trim()?.takeIf { it.isNotBlank() }?.lowercase()
    }

    fun normalizeLocality(value: String?): String? {
        return value?.trim()?.takeIf { it.isNotBlank() }?.lowercase()
    }

    fun normalizeLocalities(value: String?): List<String> {
        return value
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.map { it.lowercase() }
            ?: emptyList()
    }

    fun normalizeLocalities(values: List<String>): List<String> {
        return values.map { it.trim() }.filter { it.isNotBlank() }.map { it.lowercase() }
    }

    fun normalizePincode(value: String?): String? {
        return value?.trim()?.takeIf { it.isNotBlank() }
    }
}
