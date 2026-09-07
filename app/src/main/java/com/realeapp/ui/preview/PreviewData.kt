package com.realeapp.ui.preview

import com.realeapp.feature.add.domain.model.PropertyForm
import com.realeapp.feature.auth.domain.model.User
import com.realeapp.feature.search.domain.model.Age
import com.realeapp.feature.search.domain.model.Amenity
import com.realeapp.feature.search.domain.model.BedroomType
import com.realeapp.feature.search.domain.model.Facing
import com.realeapp.feature.search.domain.model.Furnishing
import com.realeapp.feature.search.domain.model.ListingCategory
import com.realeapp.feature.search.domain.model.NearbyPlace
import com.realeapp.feature.search.domain.model.NearbyPlaceType
import com.realeapp.feature.search.domain.model.Property
import com.realeapp.feature.search.domain.model.PropertyType
import com.realeapp.feature.search.domain.model.RentBuy
import com.realeapp.feature.search.domain.model.ResidentialCommercial

object PreviewData {

    val sampleProperty = Property(
        id = "preview-property-1",
        documentId = "preview-property-1",
        userId = "preview-user-1",
        title = "Luxury 3 BHK Apartment",
        description = "Spacious and well ventilated apartment with modern fittings, " +
            "24/7 security, covered parking and close to schools and hospitals.",
        price = 12_500_000.0,
        city = "Goa",
        locality = "Panjim",
        pincode = "403001",
        address = "123 Main Road, Near Miramar Beach",
        latitude = 15.4909,
        longitude = 73.8278,
        images = listOf(
            "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800&q=80",
            "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&q=80"
        ),
        isLiked = true,
        rating = 4.5,
        agentPhone = "+91 98765 43210",
        status = "live",
        listingCategory = ListingCategory.FEATURED,
        createdAt = "2025-01-15T10:00:00.000Z",
        rentBuy = RentBuy.BUY,
        residentialCommercial = ResidentialCommercial.RESIDENTIAL,
        propertyType = PropertyType.APARTMENT,
        bedroomType = BedroomType.THREE_BHK,
        bathrooms = 3,
        furnishing = Furnishing.FULLY_FURNISHED,
        facing = Facing.NORTH,
        age = Age.ONE_TO_FIVE,
        amenities = listOf(
            Amenity.PARKING,
            Amenity.LIFT,
            Amenity.POWER_BACKUP,
            Amenity.GYMNASIUM,
            Amenity.CCTV,
            Amenity.GATED_COMMUNITY
        ),
        nearbyPlaces = listOf(
            NearbyPlace("St. Mary's School", 1.2, NearbyPlaceType.SCHOOL),
            NearbyPlace("Goa Medical College", 2.5, NearbyPlaceType.HOSPITAL),
            NearbyPlace("Miramar Shopping", 0.8, NearbyPlaceType.SHOPPING)
        ),
        carpetArea = 1_200.0,
        builtUpArea = 1_500.0,
        superBuiltUpArea = 1_800.0
    )

    val sampleProperties = listOf(
        sampleProperty,
        sampleProperty.copy(
            id = "preview-property-2",
            documentId = "preview-property-2",
            title = "2 BHK Sea View Apartment",
            price = 9_500_000.0,
            locality = "Miramar",
            bedroomType = BedroomType.TWO_BHK,
            bathrooms = 2,
            isLiked = false
        ),
        sampleProperty.copy(
            id = "preview-property-3",
            documentId = "preview-property-3",
            title = "4 BHK Independent Villa",
            price = 22_000_000.0,
            locality = "Porvorim",
            propertyType = PropertyType.VILLA,
            bedroomType = BedroomType.FOUR_BHK,
            bathrooms = 4,
            isLiked = false
        )
    )

    val sampleUser = User(
        id = "preview-user-1",
        name = "John Doe",
        email = "john.doe@example.com",
        phone = "+91 98765 43210",
        status = "active",
        city = "Goa",
        location = "Panjim",
        address = "123 Main Road",
        password = "",
        sessionId = "preview-session",
        image = null
    )

    val samplePropertyForm = PropertyForm(
        rentBuy = RentBuy.BUY,
        residentialCommercial = ResidentialCommercial.RESIDENTIAL,
        propertyType = PropertyType.APARTMENT,
        bedroomType = BedroomType.THREE_BHK,
        title = "Luxury 3 BHK Apartment",
        description = "Spacious and well ventilated apartment with modern fittings.",
        price = "12500000",
        city = "Goa",
        locality = "Panjim",
        pincode = "403001",
        address = "123 Main Road, Near Miramar Beach",
        latitude = "15.4909",
        longitude = "73.8278",
        furnishing = Furnishing.FULLY_FURNISHED,
        facing = Facing.NORTH,
        age = Age.ONE_TO_FIVE,
        amenities = listOf(Amenity.PARKING, Amenity.LIFT, Amenity.CCTV),
        builtUpArea = "1500",
        plotArea = "",
        bathrooms = 3,
        floorNo = "3",
        totalFloors = "5",
        additionalCosts = "Maintenance: ₹5,000/month",
        propertyStatus = "Ready to Move",
        possessionDate = "31 Dec 2025",
        agentPhone = "+91 98765 43210",
        images = listOf(
            "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800&q=80"
        )
    )
}
