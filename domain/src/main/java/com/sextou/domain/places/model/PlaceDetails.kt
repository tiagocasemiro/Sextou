package com.sextou.domain.places.model

data class PlaceDetails(
    val id: String,
    val resourceName: String?,
    val displayName: String?,
    val displayNameLanguageCode: String?,
    val formattedAddress: String?,
    val shortFormattedAddress: String?,
    val adrFormattedAddress: String?,
    val addressComponents: List<AddressComponent>,
    val postalAddress: PostalAddress?,
    val location: GeoPoint?,
    val viewport: GeoBounds?,
    val plusCode: PlacePlusCode?,
    val businessStatus: BusinessStatus,
    val primaryType: String?,
    val primaryTypeDisplayName: String?,
    val types: List<String>,
    val internationalPhoneNumber: String?,
    val nationalPhoneNumber: String?,
    val websiteUri: String?,
    val googleMapsUri: String?,
    val googleMapsLinks: GoogleMapsLinks?,
    val iconMaskUrl: String?,
    val iconBackgroundColor: Int?,
    val utcOffsetMinutes: Int?,
    val timeZoneId: String?,
    val openingHours: PlaceOpeningHours?,
    val currentOpeningHours: PlaceOpeningHours?,
    val secondaryOpeningHours: List<PlaceOpeningHours>,
    val currentSecondaryOpeningHours: List<PlaceOpeningHours>,
    val priceLevel: Int?,
    val priceRange: PriceRange?,
    val rating: Double?,
    val userRatingCount: Int?,
    val accessibility: Accessibility?,
    val parking: Parking?,
    val payment: Payment?,
    val amenities: PlaceAmenities,
    val editorialSummary: LocalizedText?,
    val generativeSummary: AiSummary?,
    val neighborhoodSummary: NeighborhoodSummary?,
    val reviewSummary: ReviewSummary?,
    val reviews: List<PlaceReview>,
    val photos: List<PlacePhotoReference>,
    val addressDescriptor: AddressDescriptor?,
    val containingPlaces: List<ContainingPlace>,
    val subDestinations: List<SubDestination>,
    val attributions: List<String>,
    val providerAttribution: String,
)

data class PlacePlusCode(val compoundCode: String?, val globalCode: String?)

data class GoogleMapsLinks(
    val directionsUri: String?,
    val placeUri: String?,
    val writeReviewUri: String?,
    val reviewsUri: String?,
    val photosUri: String?,
)

data class LocalizedText(val text: String, val languageCode: String?)

data class AiSummary(
    val overview: String?,
    val languageCode: String?,
    val disclosureText: String?,
    val disclosureLanguageCode: String?,
    val flagContentUri: String?,
)

data class ContentBlock(
    val content: String?,
    val languageCode: String?,
    val referencedPlaceIds: List<String>,
    val referencedPlaceResourceNames: List<String>,
)

data class NeighborhoodSummary(
    val overview: ContentBlock?,
    val description: ContentBlock?,
    val disclosureText: String?,
    val disclosureLanguageCode: String?,
    val flagContentUri: String?,
)

data class ReviewSummary(
    val text: String?,
    val languageCode: String?,
    val disclosureText: String?,
    val disclosureLanguageCode: String?,
    val reviewsUri: String?,
    val flagContentUri: String?,
)

data class PlaceOpeningHours(
    val type: String?,
    val weekdayText: List<String>,
    val periods: List<OpeningPeriod>,
    val specialDays: List<SpecialDay>,
)

data class OpeningPeriod(val open: WeekTime?, val close: WeekTime?)

data class WeekTime(
    val dayOfWeek: String,
    val hour: Int,
    val minute: Int,
    val date: LocalDateValue?,
    val truncated: Boolean,
)

data class LocalDateValue(val year: Int, val month: Int, val day: Int)

data class SpecialDay(val date: LocalDateValue, val exceptionalHours: Boolean)

data class Accessibility(
    val wheelchairAccessibleParking: PlaceAttribute,
    val wheelchairAccessibleEntrance: PlaceAttribute,
    val wheelchairAccessibleRestroom: PlaceAttribute,
    val wheelchairAccessibleSeating: PlaceAttribute,
)

data class Parking(
    val freeLot: PlaceAttribute,
    val paidLot: PlaceAttribute,
    val freeStreet: PlaceAttribute,
    val paidStreet: PlaceAttribute,
    val valet: PlaceAttribute,
    val freeGarage: PlaceAttribute,
    val paidGarage: PlaceAttribute,
)

data class Payment(
    val acceptsCreditCards: PlaceAttribute,
    val acceptsDebitCards: PlaceAttribute,
    val cashOnly: PlaceAttribute,
    val acceptsNfc: PlaceAttribute,
)

data class PlaceAmenities(
    val curbsidePickup: PlaceAttribute,
    val delivery: PlaceAttribute,
    val dineIn: PlaceAttribute,
    val takeout: PlaceAttribute,
    val reservable: PlaceAttribute,
    val outdoorSeating: PlaceAttribute,
    val liveMusic: PlaceAttribute,
    val allowsDogs: PlaceAttribute,
    val restroom: PlaceAttribute,
    val goodForChildren: PlaceAttribute,
    val goodForGroups: PlaceAttribute,
    val goodForWatchingSports: PlaceAttribute,
    val menuForChildren: PlaceAttribute,
    val servesBeer: PlaceAttribute,
    val servesWine: PlaceAttribute,
    val servesCocktails: PlaceAttribute,
    val servesCoffee: PlaceAttribute,
    val servesBreakfast: PlaceAttribute,
    val servesBrunch: PlaceAttribute,
    val servesLunch: PlaceAttribute,
    val servesDinner: PlaceAttribute,
    val servesDessert: PlaceAttribute,
    val servesVegetarianFood: PlaceAttribute,
)

data class PlaceReview(
    val rating: Double?,
    val text: LocalizedText?,
    val originalText: LocalizedText?,
    val relativePublishTimeDescription: String?,
    val publishTime: String?,
    val visitDate: LocalDateValue?,
    val author: PlaceAuthor?,
    val googleMapsUri: String?,
    val flagContentUri: String?,
    val attribution: String?,
)

data class PlacePhotoReference(
    val placeId: String,
    val index: Int,
    val width: Int,
    val height: Int,
    val attributionHtml: String?,
    val authors: List<PlaceAuthor>,
    val googleMapsUri: String?,
    val flagContentUri: String?,
)

data class PlacePhoto(
    val uri: String,
    val attributionHtml: String?,
    val authors: List<PlaceAuthor>,
    val providerAttribution: String,
)

data class AddressDescriptor(
    val landmarks: List<Landmark>,
    val areas: List<Area>,
)

data class Landmark(
    val id: String?,
    val resourceName: String?,
    val displayName: LocalizedText?,
    val types: List<String>,
    val spatialRelationship: String?,
    val straightLineDistanceMeters: Double?,
    val travelDistanceMeters: Double?,
)

data class Area(
    val id: String?,
    val resourceName: String?,
    val displayName: LocalizedText?,
    val containment: String?,
)

data class ContainingPlace(val id: String, val resourceName: String)

data class SubDestination(val id: String, val name: String)
