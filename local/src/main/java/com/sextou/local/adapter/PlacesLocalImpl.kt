package com.sextou.local.adapter

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlaceAuthor
import com.sextou.domain.places.model.PlaceAttribute
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.local.database.PlaceEntity
import com.sextou.local.database.PlacePhotoAuthorEntity
import com.sextou.local.database.PlacePhotoEntity
import com.sextou.local.database.PlaceTypeEntity
import com.sextou.local.database.PlaceWithRelations
import com.sextou.local.database.PlacesDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlacesLocalImpl(
    private val placesDao: PlacesDao,
) : PlacesRepository.Local {
    override fun observeAll(): Flow<List<PlaceSummary>> =
        placesDao.observeAll().map { places -> places.map(PlaceWithRelations::toDomain) }

    override suspend fun saveAll(places: List<PlaceSummary>): Result<Unit> = try {
        val uniquePlaces = places
            .filter { it.id.isNotBlank() }
            .distinctBy(PlaceSummary::id)

        placesDao.saveAll(
            places = uniquePlaces.map(PlaceSummary::toEntity),
            types = uniquePlaces.flatMap(PlaceSummary::toTypeEntities),
            photos = uniquePlaces.flatMap(PlaceSummary::toPhotoEntities),
            photoAuthors = uniquePlaces.flatMap(PlaceSummary::toPhotoAuthorEntities),
        )
        Success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Failure(
            Error(
                message = exception.message?.takeIf(String::isNotBlank)
                    ?: "Não foi possível salvar os estabelecimentos localmente.",
            ),
        )
    }

    override suspend fun saveMissing(places: List<PlaceSummary>): Result<Unit> = try {
        val uniquePlaces = places
            .filter { it.id.isNotBlank() }
            .distinctBy(PlaceSummary::id)

        placesDao.saveMissing(
            places = uniquePlaces.map(PlaceSummary::toEntity),
            types = uniquePlaces.flatMap(PlaceSummary::toTypeEntities),
            photos = uniquePlaces.flatMap(PlaceSummary::toPhotoEntities),
            photoAuthors = uniquePlaces.flatMap(PlaceSummary::toPhotoAuthorEntities),
        )
        Success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Failure(
            Error(
                message = exception.message?.takeIf(String::isNotBlank)
                    ?: "Não foi possível salvar os estabelecimentos localmente.",
            ),
        )
    }
}

private fun PlaceWithRelations.toDomain() = PlaceSummary(
    id = place.placeId,
    displayName = place.displayName,
    formattedAddress = place.formattedAddress,
    location = if (place.latitude != null && place.longitude != null) {
        GeoPoint(place.latitude, place.longitude)
    } else {
        null
    },
    primaryType = place.primaryType,
    primaryTypeDisplayName = place.primaryTypeDisplayName,
    types = types.sortedBy(PlaceTypeEntity::type).map(PlaceTypeEntity::type),
    businessStatus = place.businessStatus.toBusinessStatus(),
    rating = place.rating,
    userRatingCount = place.userRatingCount,
    priceLevel = place.priceLevel,
    googleMapsUri = place.googleMapsUri,
    providerAttribution = place.providerAttribution,
    photos = photos
        .sortedBy { photo -> photo.photo.photoIndex }
        .map { photo -> photo.toDomain(place.placeId) },
    liveMusic = place.liveMusic.toPlaceAttribute(),
    goodForChildren = place.goodForChildren.toPlaceAttribute(),
    isOpen24Hours = place.isOpen24Hours,
)

private fun com.sextou.local.database.PlacePhotoWithAuthors.toDomain(
    placeId: String,
) = PlacePhotoReference(
    placeId = placeId,
    index = photo.photoIndex,
    width = photo.width,
    height = photo.height,
    attributionHtml = photo.attributionHtml,
    authors = authors
        .asSequence()
        .filter { author -> author.photoIndex == photo.photoIndex }
        .sortedBy(PlacePhotoAuthorEntity::authorIndex)
        .map(PlacePhotoAuthorEntity::toDomain)
        .toList(),
    googleMapsUri = photo.googleMapsUri,
    flagContentUri = photo.flagContentUri,
)

private fun PlacePhotoAuthorEntity.toDomain() = PlaceAuthor(
    name = name,
    profileUri = profileUri,
    photoUri = photoUri,
)

private fun String.toBusinessStatus(): BusinessStatus =
    BusinessStatus.values().firstOrNull { status -> status.name == this } ?: BusinessStatus.UNKNOWN

private fun String.toPlaceAttribute(): PlaceAttribute =
    PlaceAttribute.values().firstOrNull { attribute -> attribute.name == this }
        ?: PlaceAttribute.NOT_AVAILABLE

private fun PlaceSummary.toEntity() = PlaceEntity(
    placeId = id,
    displayName = displayName,
    formattedAddress = formattedAddress,
    latitude = location?.latitude,
    longitude = location?.longitude,
    primaryType = primaryType,
    primaryTypeDisplayName = primaryTypeDisplayName,
    businessStatus = businessStatus.name,
    rating = rating,
    userRatingCount = userRatingCount,
    priceLevel = priceLevel,
    googleMapsUri = googleMapsUri,
    providerAttribution = providerAttribution,
    liveMusic = liveMusic.name,
    goodForChildren = goodForChildren.name,
    isOpen24Hours = isOpen24Hours,
)

private fun PlaceSummary.toTypeEntities() = types
    .distinct()
    .map { type ->
        PlaceTypeEntity(
            placeId = id,
            type = type,
        )
    }

private fun PlaceSummary.toPhotoEntities() = photos
    .distinctBy { photo -> photo.index }
    .map { photo ->
        PlacePhotoEntity(
            placeId = id,
            photoIndex = photo.index,
            width = photo.width,
            height = photo.height,
            attributionHtml = photo.attributionHtml,
            googleMapsUri = photo.googleMapsUri,
            flagContentUri = photo.flagContentUri,
        )
    }

private fun PlaceSummary.toPhotoAuthorEntities() = photos
    .distinctBy { photo -> photo.index }
    .flatMap { photo ->
        photo.authors.mapIndexed { authorIndex, author ->
            PlacePhotoAuthorEntity(
                placeId = id,
                photoIndex = photo.index,
                authorIndex = authorIndex,
                name = author.name,
                profileUri = author.profileUri,
                photoUri = author.photoUri,
            )
        }
    }
