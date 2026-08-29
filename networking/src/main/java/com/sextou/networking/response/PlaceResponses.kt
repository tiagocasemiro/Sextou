package com.sextou.networking.response

import com.google.android.libraries.places.api.model.AddressDescriptor as GoogleAddressDescriptor
import com.google.android.libraries.places.api.model.AuthorAttribution
import com.google.android.libraries.places.api.model.ContentBlock as GoogleContentBlock
import com.google.android.libraries.places.api.model.LocalDate
import com.google.android.libraries.places.api.model.Money as GoogleMoney
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PostalAddress as GooglePostalAddress
import com.google.android.libraries.places.api.model.TimeOfWeek
import com.sextou.domain.places.model.Accessibility
import com.sextou.domain.places.model.AddressComponent
import com.sextou.domain.places.model.AddressDescriptor
import com.sextou.domain.places.model.AiSummary
import com.sextou.domain.places.model.Area
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.ContainingPlace
import com.sextou.domain.places.model.ContentBlock
import com.sextou.domain.places.model.GeoBounds
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.GoogleMapsLinks
import com.sextou.domain.places.model.Landmark
import com.sextou.domain.places.model.LocalDateValue
import com.sextou.domain.places.model.LocalizedText
import com.sextou.domain.places.model.Money
import com.sextou.domain.places.model.NeighborhoodSummary
import com.sextou.domain.places.model.OpeningPeriod
import com.sextou.domain.places.model.Parking
import com.sextou.domain.places.model.Payment
import com.sextou.domain.places.model.PlaceAmenities
import com.sextou.domain.places.model.PlaceAttribute
import com.sextou.domain.places.model.PlaceAuthor
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceOpeningHours
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlacePlusCode
import com.sextou.domain.places.model.PlaceReview
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PostalAddress
import com.sextou.domain.places.model.PriceRange
import com.sextou.domain.places.model.ReviewSummary
import com.sextou.domain.places.model.SpecialDay
import com.sextou.domain.places.model.SubDestination
import com.sextou.domain.places.model.WeekTime
import com.sextou.repository.DomainMapperResponse

data class PlaceSummaryResponse(val place: Place) : DomainMapperResponse<PlaceSummary> {
    override fun mapToDomain() = PlaceSummary(
        id = requireNotNull(place.id),
        displayName = place.displayName,
        formattedAddress = place.formattedAddress,
        location = place.location?.let { GeoPoint(it.latitude, it.longitude) },
        primaryType = place.primaryType,
        primaryTypeDisplayName = place.primaryTypeDisplayName,
        types = place.placeTypes.orEmpty(),
        businessStatus = place.businessStatus.toDomain(),
        rating = place.rating,
        userRatingCount = place.userRatingCount,
        priceLevel = place.priceLevel,
        googleMapsUri = place.googleMapsUri?.toString(),
        providerAttribution = PROVIDER_ATTRIBUTION,
        photos = place.toPhotoReferences(),
    )
}

data class PlaceDetailsResponse(val place: Place) : DomainMapperResponse<PlaceDetails> {
    override fun mapToDomain() = PlaceDetails(
        id = requireNotNull(place.id),
        resourceName = place.resourceName,
        displayName = place.displayName,
        displayNameLanguageCode = place.displayNameLanguageCode,
        formattedAddress = place.formattedAddress,
        shortFormattedAddress = place.shortFormattedAddress,
        adrFormattedAddress = place.adrFormatAddress,
        addressComponents = place.addressComponents?.asList().orEmpty().map {
            AddressComponent(it.name, it.shortName, it.types)
        },
        postalAddress = place.postalAddress?.toDomain(),
        location = place.location?.let { GeoPoint(it.latitude, it.longitude) },
        viewport = place.viewport?.let {
            GeoBounds(
                southWest = GeoPoint(it.southwest.latitude, it.southwest.longitude),
                northEast = GeoPoint(it.northeast.latitude, it.northeast.longitude),
            )
        },
        plusCode = place.plusCode?.let { PlacePlusCode(it.compoundCode, it.globalCode) },
        businessStatus = place.businessStatus.toDomain(),
        primaryType = place.primaryType,
        primaryTypeDisplayName = place.primaryTypeDisplayName,
        types = place.placeTypes.orEmpty(),
        internationalPhoneNumber = place.internationalPhoneNumber,
        nationalPhoneNumber = place.nationalPhoneNumber,
        websiteUri = place.websiteUri?.toString(),
        googleMapsUri = place.googleMapsUri?.toString(),
        googleMapsLinks = place.googleMapsLinks?.let {
            GoogleMapsLinks(
                directionsUri = it.directionsUri?.toString(),
                placeUri = it.placeUri?.toString(),
                writeReviewUri = it.writeAReviewUri?.toString(),
                reviewsUri = it.reviewsUri?.toString(),
                photosUri = it.photosUri?.toString(),
            )
        },
        iconMaskUrl = place.iconMaskUrl,
        iconBackgroundColor = place.iconBackgroundColor,
        utcOffsetMinutes = place.utcOffsetMinutes,
        timeZoneId = place.timeZone?.id,
        openingHours = place.openingHours?.toDomain(),
        currentOpeningHours = place.currentOpeningHours?.toDomain(),
        secondaryOpeningHours = place.secondaryOpeningHours.orEmpty().map { it.toDomain() },
        currentSecondaryOpeningHours = place.currentSecondaryOpeningHours.orEmpty().map { it.toDomain() },
        priceLevel = place.priceLevel,
        priceRange = place.priceRange?.let {
            PriceRange(it.startPrice?.toDomain(), it.endPrice?.toDomain())
        },
        rating = place.rating,
        userRatingCount = place.userRatingCount,
        accessibility = place.accessibilityOptions?.let {
            Accessibility(
                wheelchairAccessibleParking = it.wheelchairAccessibleParking.toDomain(),
                wheelchairAccessibleEntrance = it.wheelchairAccessibleEntrance.toDomain(),
                wheelchairAccessibleRestroom = it.wheelchairAccessibleRestroom.toDomain(),
                wheelchairAccessibleSeating = it.wheelchairAccessibleSeating.toDomain(),
            )
        },
        parking = place.parkingOptions?.let {
            Parking(
                freeLot = it.freeParkingLot.toDomain(),
                paidLot = it.paidParkingLot.toDomain(),
                freeStreet = it.freeStreetParking.toDomain(),
                paidStreet = it.paidStreetParking.toDomain(),
                valet = it.valetParking.toDomain(),
                freeGarage = it.freeGarageParking.toDomain(),
                paidGarage = it.paidGarageParking.toDomain(),
            )
        },
        payment = place.paymentOptions?.let {
            Payment(
                acceptsCreditCards = it.acceptsCreditCards.toDomain(),
                acceptsDebitCards = it.acceptsDebitCards.toDomain(),
                cashOnly = it.acceptsCashOnly.toDomain(),
                acceptsNfc = it.acceptsNfc.toDomain(),
            )
        },
        amenities = place.toAmenities(),
        editorialSummary = place.editorialSummary?.let {
            LocalizedText(it, place.editorialSummaryLanguageCode)
        },
        generativeSummary = place.generativeSummary?.let {
            AiSummary(
                overview = it.overview,
                languageCode = it.overviewLanguageCode,
                disclosureText = it.disclosureText,
                disclosureLanguageCode = it.disclosureTextLanguageCode,
                flagContentUri = it.flagContentUri?.toString(),
            )
        },
        neighborhoodSummary = place.neighborhoodSummary?.let {
            NeighborhoodSummary(
                overview = it.overview?.toDomain(),
                description = it.description?.toDomain(),
                disclosureText = it.disclosureText,
                disclosureLanguageCode = it.disclosureTextLanguageCode,
                flagContentUri = it.flagContentUri?.toString(),
            )
        },
        reviewSummary = place.reviewSummary?.let {
            ReviewSummary(
                text = it.text,
                languageCode = it.textLanguageCode,
                disclosureText = it.disclosureText,
                disclosureLanguageCode = it.disclosureTextLanguageCode,
                reviewsUri = it.reviewsUri?.toString(),
                flagContentUri = it.flagContentUri?.toString(),
            )
        },
        reviews = place.reviews.orEmpty().map { review ->
            PlaceReview(
                rating = review.rating,
                text = review.text?.let { LocalizedText(it, review.textLanguageCode) },
                originalText = review.originalText?.let { LocalizedText(it, review.originalTextLanguageCode) },
                relativePublishTimeDescription = review.relativePublishTimeDescription,
                publishTime = review.publishTime,
                visitDate = review.visitDate?.toDomain(),
                author = review.authorAttribution.toDomain(),
                googleMapsUri = review.googleMapsUri?.toString(),
                flagContentUri = review.flagContentUri?.toString(),
                attribution = review.attribution,
            )
        },
        photos = place.toPhotoReferences(),
        addressDescriptor = place.addressDescriptor?.toDomain(),
        containingPlaces = place.containingPlaces.orEmpty().map {
            ContainingPlace(it.id, it.resourceName)
        },
        subDestinations = place.subDestinations.orEmpty().map { SubDestination(it.id, it.name) },
        attributions = place.attributions.orEmpty(),
        providerAttribution = PROVIDER_ATTRIBUTION,
    )
}

data class PlacePhotoResponse(
    val uri: String,
    val attributionHtml: String?,
    val authors: List<PlaceAuthor>,
) : DomainMapperResponse<PlacePhoto> {
    override fun mapToDomain() = PlacePhoto(
        uri = uri,
        attributionHtml = attributionHtml,
        authors = authors,
        providerAttribution = PROVIDER_ATTRIBUTION,
    )
}

private fun Place.toPhotoReferences(): List<PlacePhotoReference> =
    photoMetadatas.orEmpty().mapIndexed { index, photo ->
        PlacePhotoReference(
            placeId = requireNotNull(id),
            index = index,
            width = photo.width,
            height = photo.height,
            attributionHtml = photo.attributions,
            authors = photo.authorAttributions?.asList().orEmpty().map { it.toDomain() },
            googleMapsUri = photo.googleMapsUri?.toString(),
            flagContentUri = photo.flagContentUri?.toString(),
        )
    }

private fun Place.BusinessStatus?.toDomain() = when (this) {
    Place.BusinessStatus.OPERATIONAL -> BusinessStatus.OPERATIONAL
    Place.BusinessStatus.CLOSED_TEMPORARILY -> BusinessStatus.CLOSED_TEMPORARILY
    Place.BusinessStatus.CLOSED_PERMANENTLY -> BusinessStatus.CLOSED_PERMANENTLY
    null -> BusinessStatus.UNKNOWN
}

private fun Place.BooleanPlaceAttributeValue?.toDomain() = when (this) {
    Place.BooleanPlaceAttributeValue.TRUE -> PlaceAttribute.YES
    Place.BooleanPlaceAttributeValue.FALSE -> PlaceAttribute.NO
    Place.BooleanPlaceAttributeValue.UNKNOWN -> PlaceAttribute.UNKNOWN
    null -> PlaceAttribute.NOT_AVAILABLE
}

private fun GoogleMoney.toDomain() = Money(currencyCode, units ?: 0L, nanos ?: 0)

private fun GooglePostalAddress.toDomain() = PostalAddress(
    regionCode = regionCode,
    languageCode = languageCode,
    postalCode = postalCode,
    sortingCode = sortingCode,
    administrativeArea = administrativeArea,
    locality = locality,
    sublocality = sublocality,
    addressLines = addressLines.orEmpty(),
    recipients = recipients.orEmpty(),
    organization = organization,
)

private fun OpeningHours.toDomain() = PlaceOpeningHours(
    type = hoursType?.name,
    weekdayText = weekdayText.orEmpty(),
    periods = periods.orEmpty().map { OpeningPeriod(it.open?.toDomain(), it.close?.toDomain()) },
    specialDays = specialDays.orEmpty().mapNotNull { day ->
        day.date.let { SpecialDay(it.toDomain(), day.isExceptional) }
    },
)

private fun TimeOfWeek.toDomain() = WeekTime(
    dayOfWeek = day.name,
    hour = time.hours,
    minute = time.minutes,
    date = date?.toDomain(),
    truncated = isTruncated,
)

private fun LocalDate.toDomain() = LocalDateValue(year, month, day)

private fun AuthorAttribution.toDomain() = PlaceAuthor(name, uri, photoUri)

private fun GoogleContentBlock.toDomain() = ContentBlock(
    content = content,
    languageCode = contentLanguageCode,
    referencedPlaceIds = referencedPlaceIds.orEmpty(),
    referencedPlaceResourceNames = referencedPlaceResourceNames.orEmpty(),
)

private fun GoogleAddressDescriptor.toDomain() = AddressDescriptor(
    landmarks = landmarks.orEmpty().map {
        Landmark(
            id = it.id,
            resourceName = it.resourceName,
            displayName = it.displayName?.let { name -> LocalizedText(name, it.displayNameLanguageCode) },
            types = it.types.orEmpty(),
            spatialRelationship = it.spatialRelationship?.name,
            straightLineDistanceMeters = it.straightLineDistanceMeters,
            travelDistanceMeters = it.travelDistanceMeters,
        )
    },
    areas = areas.orEmpty().map {
        Area(
            id = it.id,
            resourceName = it.resourceName,
            displayName = it.displayName?.let { name -> LocalizedText(name, it.displayNameLanguageCode) },
            containment = it.containment?.name,
        )
    },
)

private fun Place.toAmenities() = PlaceAmenities(
    curbsidePickup = curbsidePickup.toDomain(),
    delivery = delivery.toDomain(),
    dineIn = dineIn.toDomain(),
    takeout = takeout.toDomain(),
    reservable = reservable.toDomain(),
    outdoorSeating = outdoorSeating.toDomain(),
    liveMusic = liveMusic.toDomain(),
    allowsDogs = allowsDogs.toDomain(),
    restroom = restroom.toDomain(),
    goodForChildren = goodForChildren.toDomain(),
    goodForGroups = goodForGroups.toDomain(),
    goodForWatchingSports = goodForWatchingSports.toDomain(),
    menuForChildren = menuForChildren.toDomain(),
    servesBeer = servesBeer.toDomain(),
    servesWine = servesWine.toDomain(),
    servesCocktails = servesCocktails.toDomain(),
    servesCoffee = servesCoffee.toDomain(),
    servesBreakfast = servesBreakfast.toDomain(),
    servesBrunch = servesBrunch.toDomain(),
    servesLunch = servesLunch.toDomain(),
    servesDinner = servesDinner.toDomain(),
    servesDessert = servesDessert.toDomain(),
    servesVegetarianFood = servesVegetarianFood.toDomain(),
)

private const val PROVIDER_ATTRIBUTION = "Google Maps"
