package com.realeapp.feature.add.presentation

/**
 * Centralized UI strings for the Add Property feature.
 * Never hardcode string literals in feature code — add them here.
 */
internal object AddStrings {

    // Step titles & subtitles
    const val STEP1_TITLE = "1. Basic Details"
    const val STEP1_SUBTITLE = "Property type, purpose, location"
    const val STEP2_TITLE = "2. Property Details"
    const val STEP2_SUBTITLE = "Add key details about your property"
    const val STEP3_TITLE = "3. Photos & Media"
    const val STEP3_SUBTITLE = "Showcase your property with high-quality photos"
    const val STEP4_TITLE = "4. Pricing"
    const val STEP4_SUBTITLE = "Set the price and availability details"
    const val STEP5_TITLE = "5. Review & Publish"
    const val STEP5_SUBTITLE = "Please check all details before publishing"

    // Navigation
    const val ACTION_BACK = "Back"
    const val ACTION_CONTINUE = "Continue"
    const val ACTION_REVIEW = "Review"
    const val ACTION_OK = "OK"
    const val ACTION_CANCEL = "Cancel"

    // Shared field labels
    const val LABEL_PROPERTY_TITLE = "Property Title"
    const val LABEL_TITLE = "Title"
    const val LABEL_PROPERTY_TYPE = "Property Type"
    const val LABEL_LISTING_TYPE = "Listing Type"
    const val LABEL_LOCATION = "Location"
    const val LABEL_CITY = "City"
    const val LABEL_LOCALITY = "Locality"
    const val LABEL_PINCODE = "Pincode"
    const val LABEL_ADDRESS = "Address"
    const val LABEL_DESCRIPTION = "Description"
    const val LABEL_SHORT_DESCRIPTION = "Short Description"
    const val LABEL_PRICE = "Price"
    const val LABEL_BEDROOMS = "Bedrooms"
    const val LABEL_BATHROOMS = "Bathrooms"
    const val LABEL_FURNISHING = "Furnishing"
    const val LABEL_FACING = "Facing"
    const val LABEL_AGE = "Age"
    const val LABEL_PROPERTY_AGE = "Property Age"
    const val LABEL_AMENITIES = "Amenities"
    const val LABEL_CARPET_AREA = "Carpet Area"
    const val LABEL_BUILT_UP = "Built-up"
    const val LABEL_SUPER_BUILT_UP = "Super Built-up"
    const val LABEL_BUILT_UP_AREA = "Built-up Area"
    const val LABEL_BUILT_UP_AREA_SQFT = "Built-up Area (sq ft)"
    const val LABEL_PLOT_AREA_SQFT = "Plot Area (sq ft)"
    const val LABEL_AGENT_PHONE = "Agent Phone"
    const val LABEL_FLOOR_NO = "Floor No."
    const val LABEL_TOTAL_FLOORS = "Total Floors"
    const val LABEL_CONFIGURATION = "Configuration"
    const val LABEL_STATUS = "Status"
    const val LABEL_PHOTOS = "Photos"

    // Shared section headers
    const val SECTION_BASIC_DETAILS = "Basic Details"
    const val SECTION_PROPERTY_DETAILS = "Property Details"
    const val SECTION_CONFIGURATION = "Configuration"
    const val SECTION_PROPERTY_FEATURES = "Property Features"
    const val SECTION_ADDITIONAL_FEATURES = "Additional Features"
    const val SECTION_PROPERTY_PHOTOS = "Property Photos"
    const val SECTION_PRICE_DETAILS = "Price Details"
    const val SECTION_AVAILABILITY = "Availability"

    // Shared input affordances
    const val REQUIRED_MARKER = " *"
    const val ENTER_PREFIX = "Enter "
    const val SELECT_PREFIX = "Select "
    const val BULLET_PREFIX = "• "
    const val PLACEHOLDER_DASH = "-"

    // Step 1 — Basic details
    const val LOCATION_FIELD_PLACEHOLDER = "Enter location, locality or landmark"
    const val ACTION_USE_MY_LOCATION = "Use my Location"
    const val ACTION_PICK_ON_MAP = "Pick on Map"
    const val ACTION_CHANGE_LOCATION_ON_MAP = "Change Location on Map"
    const val ACTION_CHANGE_LOCATION = "Change Location"
    const val LISTING_FOR_SALE = "For Sale"
    const val LISTING_FOR_RENT = "For Rent"
    const val DESCRIPTION_COUNTER_SUFFIX = "/50"

    // Step 2 — Property details
    const val FLOOR_GROUND = "Ground"
    const val FLOOR_TEN_PLUS = "10+"
    const val FLOORS_TWENTY_PLUS = "20+"
    const val LABEL_PROPERTY_VIDEO_OPTIONAL = "Property Video (Optional)"
    const val VIDEO_MIME_FILTER = "video/*"
    const val UPLOAD_VIDEO_TITLE = "Upload Video"
    const val UPLOAD_VIDEO_HINT = "MP4, MOV (Max 100 MB)"
    const val VIDEO_SELECTED = "Video selected"
    const val CD_REMOVE_VIDEO = "Remove video"
    const val CD_DECREASE_PREFIX = "Decrease "
    const val CD_INCREASE_PREFIX = "Increase "

    // Step 2 — amenity feature labels
    const val AMENITY_PARKING = "Parking"
    const val AMENITY_SWIMMING_POOL = "Swimming Pool"
    const val AMENITY_GARDEN = "Garden"
    const val AMENITY_POWER_BACKUP = "Power Backup"
    const val AMENITY_SECURITY = "Security"
    const val AMENITY_LIFT = "Lift"
    const val AMENITY_CLUBHOUSE = "Clubhouse"
    const val AMENITY_PET_FRIENDLY = "Pet Friendly"

    // Step 3 — Photos & media
    const val UPLOAD_PHOTOS_TITLE = "Upload Photos"
    const val UPLOAD_PHOTOS_DRAG_HINT = "Drag & drop or tap to upload"
    const val UPLOAD_PHOTOS_FORMAT_HINT = "JPG, PNG (Max 10 MB each)"
    const val ACTION_SELECT_PHOTOS = "Select Photos"
    const val UPLOADING_PHOTOS = "Uploading photos..."
    const val PHOTOS_VISIBILITY_HINT = "Add at least 5 photos for better visibility"
    const val ACTION_ADD_MORE = "Add More"
    const val CD_ADD_MORE_PHOTOS = "Add more photos"
    const val BADGE_COVER = "Cover"
    const val CD_REMOVE_PREFIX = "Remove "
    const val PHOTO_LABEL_PREFIX = "Photo "
    const val TIPS_TITLE = "Tips for great photos"
    const val TIP_WELL_LIT = "Use clear, well-lit photos"
    const val TIP_KEY_AREAS = "Show all key areas (bedrooms, kitchen, etc.)"
    const val TIP_EXTERIOR = "Include exterior, amenities and surrounding views"

    val PHOTO_SUGGESTIONS = listOf(
        "Main Exterior",
        "Living Room",
        "Bedroom",
        "Kitchen",
        "Bathroom",
        "Balcony / View",
        "Amenities"
    )

    val PHOTO_TIPS = listOf(TIP_WELL_LIT, TIP_KEY_AREAS, TIP_EXTERIOR)

    // Step 4 — Pricing
    const val PRICE_MODE_TOTAL = "Total Price"
    const val PRICE_MODE_PER_SQFT = "Price per sq ft"
    const val PRICE_PLACEHOLDER = "e.g. 1,85,00,000"
    const val RUPEE_SYMBOL = "₹"
    const val LABEL_NEGOTIABLE = "Negotiable"
    const val LABEL_ADDITIONAL_COSTS = "Additional Costs (Optional)"
    const val LABEL_PROPERTY_STATUS = "Property Status"
    const val LABEL_POSSESSION_DATE = "Possession Date"
    const val PLACEHOLDER_SELECT_DATE = "Select Date"
    const val CD_PICK_DATE = "Pick date"
    const val CALC_TOTAL_SUFFIX = " total (calculated)"
    const val CALC_PER_SQFT_SUFFIX = " per sq ft (calculated)"
    const val PER_SQFT_SUFFIX = " per sq ft"
    const val DATE_FORMAT_POSSESSION = "dd MMM yyyy"

    val PRICE_MODE_OPTIONS = listOf(PRICE_MODE_TOTAL, PRICE_MODE_PER_SQFT)

    val ADDITIONAL_COST_OPTIONS = listOf(
        "Maintenance",
        "Govt. Taxes",
        "Parking Charges",
        "Club Membership",
        "Other"
    )

    val PROPERTY_STATUS_OPTIONS = listOf(
        "Ready to Move",
        "Under Construction",
        "New Launch"
    )

    // Step 5 — Review & publish
    const val UNTITLED_PROPERTY = "Untitled Property"
    const val CD_COVER_PHOTO = "Cover photo"
    const val ACTION_EDIT = "Edit"
    const val ACTION_SHOW_MORE = "Show More"
    const val ACTION_SHOW_LESS = "Show Less"
    const val ACTION_PUBLISH_LISTING = "Publish Listing"
    const val CONFIRM_ACCURACY_PREFIX =
        "I confirm that the information provided is accurate and I agree to the "
    const val TERMS_AND_CONDITIONS = "Terms & Conditions"
    const val BEDS_BATHS_SEPARATOR = " Beds • "
    const val BATHS_SUFFIX = " Baths"
    const val BEDS_SUFFIX = " Beds"
    const val SQ_FT_SUFFIX = " sq ft"
    const val PHOTOS_SUFFIX = " Photos"
    const val VIDEO_SUFFIX = ", 1 Video"
    const val CRORE_SUFFIX = " Cr"
    const val LAKH_SUFFIX = " L"
    const val RUPEE_PREFIX = "₹ "

    // Image picking / upload
    const val DIALOG_IMAGE_SOURCE_TITLE = "Choose Image Source"
    const val IMAGE_SOURCE_CAMERA = "Camera"
    const val IMAGE_SOURCE_GALLERY = "Gallery"
    const val CD_CAMERA = "Camera"
    const val CD_GALLERY = "Gallery"
    const val CD_ADD_IMAGES = "Add images"
    const val ACTION_ADD_PHOTOS = "Add Photos"
    const val CD_PROPERTY_IMAGE = "Property image"
    const val CD_REMOVE_IMAGE = "Remove image"
    const val IMAGE_MIME_FILTER = "image/*"
    const val IMAGE_MIME_DEFAULT = "image/jpeg"
    const val IMAGE_EXT_DEFAULT = "jpg"
    const val IMAGE_FILENAME_PREFIX = "property_image_"
    const val IMAGE_FILENAME_EXT = ".jpg"

    // My Listings screen
    const val TITLE_ADD_PROPERTY = "Add Property"
    const val TITLE_MY_LISTINGS = "My Listings"
    const val SUBTITLE_MANAGE_PROPERTIES = "Manage your properties"
    const val ACTION_ADD_PROPERTY = "Add Property"
    const val CD_BACK = "Back"
    const val CD_ADD_PROPERTY = "Add property"
    const val TAB_ALL = "All"
    const val TAB_ACTIVE = "Active"
    const val TAB_INACTIVE = "Inactive"
    const val TAB_DRAFTS = "Drafts"
    const val SEARCH_LISTINGS_PLACEHOLDER = "Search by property name, location or type"
    const val CD_REFRESH_FILTERS = "Refresh filters"
    const val EMPTY_LISTINGS = "No properties found"
    const val CD_MORE_OPTIONS = "More options"
    const val ACTION_VIEW_DETAILS = "View Details"
    const val ACTION_REACTIVATE = "Reactivate"
    const val STATUS_ACTIVE = "Active"
    const val STATUS_INACTIVE = "Inactive"
    const val STATUS_DRAFT = "Draft"

    val LISTING_TABS = listOf(TAB_ALL, TAB_ACTIVE, TAB_INACTIVE, TAB_DRAFTS)

    // Success screen
    const val SUCCESS_TITLE = "Your Property\nis Live!"
    const val SUCCESS_MESSAGE =
        "Congratulations! Your property has been successfully listed on Reale."
    const val ACTION_VIEW_LISTING = "View Listing"
    const val ACTION_ADD_ANOTHER_PROPERTY = "Add Another Property"

    // Location picker
    const val LOCATION_PERMISSION_REQUIRED = "Location permission is required"
    const val PICK_LOCATION_TITLE = "Pick Location"
    const val CD_CLOSE = "Close"
    const val SELECTED_LOCATION_FALLBACK = "Selected location"
    const val ACTION_CONFIRM_LOCATION = "Confirm Location"
    const val CD_SELECTED_LOCATION = "Selected location"
    const val CD_MY_LOCATION = "My location"
    const val MAPS_KEY_MISSING_TITLE = "Google Maps API key not set."
    const val MAPS_KEY_MISSING_BODY =
        "Set MAPS_API_KEY in AndroidManifest to enable the map picker. You can still enter coordinates manually below."
    const val LABEL_LATITUDE = "Latitude"
    const val LABEL_LONGITUDE = "Longitude"
    const val ACTION_CONFIRM_COORDINATES = "Confirm Coordinates"
    const val ERROR_CURRENT_LOCATION = "Unable to get current location"
    const val MAPS_API_KEY_METADATA = "com.google.android.geo.API_KEY"
    const val MAPS_API_KEY_PLACEHOLDER = "YOUR_API_KEY"

    // Validation & messages
    const val ERR_TITLE_REQUIRED = "Property title is required"
    const val ERR_PROPERTY_TYPE_REQUIRED = "Property type is required"
    const val ERR_LISTING_TYPE_REQUIRED = "Listing type is required"
    const val ERR_CITY_REQUIRED = "City is required"
    const val ERR_LOCALITY_REQUIRED = "Locality is required"
    const val ERR_PRICE_REQUIRED = "Price is required"
    const val ERR_PRICE_INVALID = "Price must be a valid number"
    const val ERR_RENT_BUY_REQUIRED = "Rent/Buy is required"
    const val ERR_RESIDENTIAL_COMMERCIAL_REQUIRED = "Residential/Commercial is required"
    const val ERR_LATITUDE_INVALID = "Latitude must be a valid number"
    const val ERR_LONGITUDE_INVALID = "Longitude must be a valid number"
    const val ERR_LOGIN_REQUIRED = "Please log in to add a property"
    const val MSG_PROPERTY_ADDED = "Property added successfully"
    const val ACTION_SUBMIT_PROPERTY = "Submit Property"
    const val LOGIN_PROMPT_TITLE = "Please Login to Add a Property"

    // Locale
    const val LOCALE_LANGUAGE = "en"
    const val LOCALE_COUNTRY = "IN"

    // Backend values
    const val STATUS_LIVE = "live"
    const val STATUS_VALUE_ACTIVE = "active"
    const val STATUS_VALUE_DRAFT = "draft"
    const val TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val TIMEZONE_UTC = "UTC"
}
