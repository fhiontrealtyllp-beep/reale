package com.fhiont.feature.search.presentation

import com.fhiont.feature.search.domain.model.Property
import com.fhiont.feature.search.domain.model.PropertyType
import com.fhiont.feature.search.domain.model.RentBuy
import com.fhiont.feature.search.domain.model.ResidentialCommercial

/**
 * Top-level home category chips used to locally filter featured properties and ads.
 */
enum class HomeCategory {
    BUY,
    RENT,
    NEW_PROJECTS,
    COMMERCIAL;

    private val newProjectPropertyTypes = setOf(
        PropertyType.APARTMENT,
        PropertyType.VILLA,
        PropertyType.INDEPENDENT_HOUSE
    )

    fun matches(property: Property): Boolean = when (this) {
        BUY -> property.rentBuy == RentBuy.BUY &&
            property.residentialCommercial == ResidentialCommercial.RESIDENTIAL
        RENT -> property.rentBuy == RentBuy.RENT &&
            property.residentialCommercial == ResidentialCommercial.RESIDENTIAL
        NEW_PROJECTS -> property.rentBuy == RentBuy.BUY &&
            property.residentialCommercial == ResidentialCommercial.RESIDENTIAL &&
            property.propertyType in newProjectPropertyTypes
        COMMERCIAL -> property.residentialCommercial == ResidentialCommercial.COMMERCIAL
    }
}
