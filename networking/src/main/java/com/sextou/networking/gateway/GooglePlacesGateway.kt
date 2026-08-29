package com.sextou.networking.gateway

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceRankPreference
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.networking.response.PlaceDetailsResponse
import com.sextou.networking.response.PlacePhotoResponse
import com.sextou.networking.response.PlaceSummaryResponse
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.tasks.await

class GooglePlacesGateway(
    context: Context,
    apiKey: String,
) : PlacesGateway {
    private val applicationContext = context.applicationContext
    private val client: PlacesClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        require(apiKey.isNotBlank()) {
            "Defina PLACES_API_KEY no gradle.properties local ou via -PPLACES_API_KEY."
        }
        Places.addInternalUsageAttributionId(USAGE_ATTRIBUTION_ID)
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        }
        Places.createClient(applicationContext)
    }
    private val photoMetadataCache = ConcurrentHashMap<String, List<PhotoMetadata>>()

    override suspend fun searchNearby(request: NearbySearchRequest): List<PlaceSummaryResponse> {
        request.validate()
        val bounds = CircularBounds.newInstance(
            LatLng(request.center.latitude, request.center.longitude),
            request.radiusMeters,
        )
        val sdkRequest = SearchNearbyRequest.builder(bounds, searchFields(request.includePhotos))
            .setIncludedTypes(request.includedTypes.toList())
            .setExcludedTypes(request.excludedTypes.toList())
            .setIncludedPrimaryTypes(request.includedPrimaryTypes.toList())
            .setExcludedPrimaryTypes(request.excludedPrimaryTypes.toList())
            .setMaxResultCount(request.maxResults)
            .setRankPreference(request.rankPreference.toSdk())
            .apply { request.regionCode?.let(::setRegionCode) }
            .build()

        val places = client.searchNearby(sdkRequest).await().places
        if (request.includePhotos) {
            cachePhotoMetadata(places)
        }
        return places.map(::PlaceSummaryResponse)
    }

    override suspend fun searchByText(request: PlaceTextSearchRequest): List<PlaceSummaryResponse> {
        request.validate()
        val locationBiasCenter = request.locationBiasCenter
        val locationBiasRadiusMeters = request.locationBiasRadiusMeters
        val sdkRequest = SearchByTextRequest.builder(request.query, searchFields(request.includePhotos))
            .setMaxResultCount(request.maxResults)
            .setOpenNow(request.openNow)
            .setStrictTypeFiltering(request.strictTypeFiltering)
            .apply {
                request.includedType?.let(::setIncludedType)
                request.minRating?.let(::setMinRating)
                request.regionCode?.let(::setRegionCode)
                if (locationBiasCenter != null && locationBiasRadiusMeters != null) {
                    setLocationBias(
                        CircularBounds.newInstance(
                            LatLng(
                                locationBiasCenter.latitude,
                                locationBiasCenter.longitude,
                            ),
                            locationBiasRadiusMeters,
                        ),
                    )
                }
            }
            .build()
        val places = client.searchByText(sdkRequest).await().places
        if (request.includePhotos) {
            cachePhotoMetadata(places)
        }
        return places.map(::PlaceSummaryResponse)
    }

    override suspend fun getDetails(request: PlaceDetailsRequest): PlaceDetailsResponse {
        require(request.placeId.isNotBlank()) { "placeId não pode ser vazio." }
        val sdkRequest = FetchPlaceRequest.builder(request.placeId, detailFields)
            .apply { request.regionCode?.let(::setRegionCode) }
            .build()
        val place = client.fetchPlace(sdkRequest).await().place
        photoMetadataCache[request.placeId] = place.photoMetadatas.orEmpty()
        return PlaceDetailsResponse(place)
    }

    override suspend fun getPhoto(request: PlacePhotoRequest): PlacePhotoResponse {
        request.validate()
        val metadata = metadataFor(request)
        val sdkRequest = FetchResolvedPhotoUriRequest.builder(metadata)
            .apply {
                request.maxWidth?.let(::setMaxWidth)
                request.maxHeight?.let(::setMaxHeight)
            }
            .build()
        val uri = requireNotNull(client.fetchResolvedPhotoUri(sdkRequest).await().uri) {
            "O Google Places não retornou uma URI para a foto solicitada."
        }
        return PlacePhotoResponse(
            uri = uri.toString(),
            attributionHtml = metadata.attributions,
            authors = metadata.authorAttributions?.asList().orEmpty().map {
                com.sextou.domain.places.model.PlaceAuthor(it.name, it.uri, it.photoUri)
            },
        )
    }

    private suspend fun metadataFor(request: PlacePhotoRequest): PhotoMetadata {
        val reference = request.reference
        val cached = photoMetadataCache[reference.placeId]
        val allMetadata = cached ?: client.fetchPlace(
            FetchPlaceRequest.newInstance(reference.placeId, listOf(Place.Field.PHOTO_METADATAS)),
        ).await().place.photoMetadatas.orEmpty().also {
            photoMetadataCache[reference.placeId] = it
        }
        return allMetadata.getOrNull(reference.index)
            ?: throw IndexOutOfBoundsException("A foto ${reference.index} não existe para o lugar informado.")
    }

    private fun cachePhotoMetadata(places: List<Place>) {
        places.forEach { place ->
            place.id?.let { placeId ->
                photoMetadataCache[placeId] = place.photoMetadatas.orEmpty()
            }
        }
    }

    private fun NearbySearchRequest.validate() {
        require(radiusMeters > 0.0 && radiusMeters <= 50_000.0) {
            "radiusMeters deve estar entre 0 e 50.000 metros."
        }
        require(maxResults in 1..20) { "maxResults deve estar entre 1 e 20." }
        require(includedTypes.intersect(excludedTypes).isEmpty()) {
            "Um tipo não pode ser incluído e excluído ao mesmo tempo."
        }
        require(includedPrimaryTypes.intersect(excludedPrimaryTypes).isEmpty()) {
            "Um tipo primário não pode ser incluído e excluído ao mesmo tempo."
        }
    }

    private fun PlacePhotoRequest.validate() {
        require(reference.placeId.isNotBlank()) { "placeId da foto não pode ser vazio." }
        require(reference.index >= 0) { "O índice da foto não pode ser negativo." }
        maxWidth?.let { require(it > 0) { "maxWidth deve ser positivo." } }
        maxHeight?.let { require(it > 0) { "maxHeight deve ser positivo." } }
    }

    private fun PlaceTextSearchRequest.validate() {
        require(query.isNotBlank()) { "query não pode ser vazia." }
        require(maxResults in 1..20) { "maxResults deve estar entre 1 e 20." }
        require((locationBiasCenter == null) == (locationBiasRadiusMeters == null)) {
            "Centro e raio do location bias devem ser informados juntos."
        }
        locationBiasRadiusMeters?.let {
            require(it > 0.0 && it <= 50_000.0) {
                "locationBiasRadiusMeters deve estar entre 0 e 50.000 metros."
            }
        }
        minRating?.let {
            require(it in 0.0..5.0 && (it * 2.0) % 1.0 == 0.0) {
                "minRating deve estar entre 0 e 5 em incrementos de 0,5."
            }
        }
    }

    private companion object {
        const val USAGE_ATTRIBUTION_ID = "gmp_git_agentskills_v1"

        val searchFields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.PRIMARY_TYPE,
            Place.Field.PRIMARY_TYPE_DISPLAY_NAME,
            Place.Field.TYPES,
            Place.Field.BUSINESS_STATUS,
            Place.Field.RATING,
            Place.Field.USER_RATING_COUNT,
            Place.Field.PRICE_LEVEL,
            Place.Field.GOOGLE_MAPS_URI,
        )

        val searchFieldsWithPhotos = searchFields + Place.Field.PHOTO_METADATAS

        fun searchFields(includePhotos: Boolean) =
            if (includePhotos) searchFieldsWithPhotos else searchFields

        val detailFields = listOf(
            Place.Field.ID,
            Place.Field.RESOURCE_NAME,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.SHORT_FORMATTED_ADDRESS,
            Place.Field.ADR_FORMAT_ADDRESS,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.POSTAL_ADDRESS,
            Place.Field.ADDRESS_DESCRIPTOR,
            Place.Field.LOCATION,
            Place.Field.VIEWPORT,
            Place.Field.PLUS_CODE,
            Place.Field.BUSINESS_STATUS,
            Place.Field.PRIMARY_TYPE,
            Place.Field.PRIMARY_TYPE_DISPLAY_NAME,
            Place.Field.TYPES,
            Place.Field.INTERNATIONAL_PHONE_NUMBER,
            Place.Field.NATIONAL_PHONE_NUMBER,
            Place.Field.WEBSITE_URI,
            Place.Field.GOOGLE_MAPS_URI,
            Place.Field.GOOGLE_MAPS_LINKS,
            Place.Field.ICON_MASK_URL,
            Place.Field.ICON_BACKGROUND_COLOR,
            Place.Field.UTC_OFFSET,
            Place.Field.TIME_ZONE,
            Place.Field.OPENING_HOURS,
            Place.Field.CURRENT_OPENING_HOURS,
            Place.Field.SECONDARY_OPENING_HOURS,
            Place.Field.CURRENT_SECONDARY_OPENING_HOURS,
            Place.Field.PRICE_LEVEL,
            Place.Field.PRICE_RANGE,
            Place.Field.RATING,
            Place.Field.USER_RATING_COUNT,
            Place.Field.ACCESSIBILITY_OPTIONS,
            Place.Field.PARKING_OPTIONS,
            Place.Field.PAYMENT_OPTIONS,
            Place.Field.CURBSIDE_PICKUP,
            Place.Field.DELIVERY,
            Place.Field.DINE_IN,
            Place.Field.TAKEOUT,
            Place.Field.RESERVABLE,
            Place.Field.OUTDOOR_SEATING,
            Place.Field.LIVE_MUSIC,
            Place.Field.ALLOWS_DOGS,
            Place.Field.RESTROOM,
            Place.Field.GOOD_FOR_CHILDREN,
            Place.Field.GOOD_FOR_GROUPS,
            Place.Field.GOOD_FOR_WATCHING_SPORTS,
            Place.Field.MENU_FOR_CHILDREN,
            Place.Field.SERVES_BEER,
            Place.Field.SERVES_WINE,
            Place.Field.SERVES_COCKTAILS,
            Place.Field.SERVES_COFFEE,
            Place.Field.SERVES_BREAKFAST,
            Place.Field.SERVES_BRUNCH,
            Place.Field.SERVES_LUNCH,
            Place.Field.SERVES_DINNER,
            Place.Field.SERVES_DESSERT,
            Place.Field.SERVES_VEGETARIAN_FOOD,
            Place.Field.EDITORIAL_SUMMARY,
            Place.Field.GENERATIVE_SUMMARY,
            Place.Field.NEIGHBORHOOD_SUMMARY,
            Place.Field.REVIEW_SUMMARY,
            Place.Field.REVIEWS,
            Place.Field.PHOTO_METADATAS,
            Place.Field.CONTAINING_PLACES,
            Place.Field.SUB_DESTINATIONS,
        )
    }
}

private fun PlaceRankPreference.toSdk() = when (this) {
    PlaceRankPreference.DISTANCE -> SearchNearbyRequest.RankPreference.DISTANCE
    PlaceRankPreference.POPULARITY -> SearchNearbyRequest.RankPreference.POPULARITY
}
