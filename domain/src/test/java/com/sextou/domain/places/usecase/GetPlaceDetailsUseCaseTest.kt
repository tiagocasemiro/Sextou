package com.sextou.domain.places.usecase

import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlacesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPlaceDetailsUseCaseTest {
    private lateinit var repository: DetailsRecordingPlacesRepository

    @Before
    fun setUp() {
        repository = DetailsRecordingPlacesRepository()
    }

    @Test
    fun blankPlaceIdReturnsFailureWithoutCallingRemoteRepository() = kotlinx.coroutines.test.runTest {
        val result = GetPlaceDetailsUseCase(repository).invoke(" ")

        assertTrue(result is Failure)
        assertEquals(null, repository.lastDetailsRequest)
    }

    @Test
    fun validPlaceIdUsesBrazilRegionAndReturnsRemoteResult() = kotlinx.coroutines.test.runTest {
        val details = samplePlaceDetails()
        repository.detailsResult = Success(details)

        val result = GetPlaceDetailsUseCase(repository).invoke("place-1")

        assertEquals(Success(details), result)
        assertEquals(PlaceDetailsRequest("place-1", "BR"), repository.lastDetailsRequest)
    }
}

private class DetailsRecordingPlacesRepository : PlacesRepository.Remote {
    var lastDetailsRequest: PlaceDetailsRequest? = null
    var detailsResult: Result<PlaceDetails> = Failure(null)

    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> {
        lastDetailsRequest = request
        return detailsResult
    }

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> =
        Failure(null)
}

private fun samplePlaceDetails() = PlaceDetails(
    id = "place-1",
    resourceName = null,
    displayName = "Place 1",
    displayNameLanguageCode = "pt-BR",
    formattedAddress = null,
    shortFormattedAddress = null,
    adrFormattedAddress = null,
    addressComponents = emptyList(),
    postalAddress = null,
    location = GeoPoint(0.0, 0.0),
    viewport = null,
    plusCode = null,
    businessStatus = BusinessStatus.OPERATIONAL,
    primaryType = null,
    primaryTypeDisplayName = null,
    types = emptyList(),
    internationalPhoneNumber = null,
    nationalPhoneNumber = null,
    websiteUri = null,
    googleMapsUri = null,
    googleMapsLinks = null,
    iconMaskUrl = null,
    iconBackgroundColor = null,
    utcOffsetMinutes = null,
    timeZoneId = null,
    openingHours = null,
    currentOpeningHours = null,
    secondaryOpeningHours = emptyList(),
    currentSecondaryOpeningHours = emptyList(),
    priceLevel = null,
    priceRange = null,
    rating = null,
    userRatingCount = null,
    accessibility = null,
    parking = null,
    payment = null,
    amenities = com.sextou.domain.places.model.PlaceAmenities(
        curbsidePickup = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        delivery = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        dineIn = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        takeout = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        reservable = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        outdoorSeating = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        liveMusic = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        allowsDogs = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        restroom = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        goodForChildren = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        goodForGroups = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        goodForWatchingSports = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        menuForChildren = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesBeer = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesWine = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesCocktails = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesCoffee = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesBreakfast = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesBrunch = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesLunch = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesDinner = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesDessert = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
        servesVegetarianFood = com.sextou.domain.places.model.PlaceAttribute.UNKNOWN,
    ),
    editorialSummary = null,
    generativeSummary = null,
    neighborhoodSummary = null,
    reviewSummary = null,
    reviews = emptyList(),
    photos = emptyList(),
    addressDescriptor = null,
    containingPlaces = emptyList(),
    subDestinations = emptyList(),
    attributions = emptyList(),
    providerAttribution = "Google Maps",
)
