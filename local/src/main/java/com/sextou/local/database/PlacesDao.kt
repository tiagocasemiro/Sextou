package com.sextou.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlacesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertPlaces(places: List<PlaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertTypes(types: List<PlaceTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertPhotos(photos: List<PlacePhotoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertPhotoAuthors(authors: List<PlacePhotoAuthorEntity>)

    @Query("DELETE FROM place_types WHERE placeId IN (:placeIds)")
    protected abstract suspend fun deleteTypes(placeIds: List<String>)

    @Query("DELETE FROM place_photos WHERE placeId IN (:placeIds)")
    protected abstract suspend fun deletePhotos(placeIds: List<String>)

    @Query("DELETE FROM place_photo_authors WHERE placeId IN (:placeIds)")
    protected abstract suspend fun deletePhotoAuthors(placeIds: List<String>)

    @Transaction
    open suspend fun saveAll(
        places: List<PlaceEntity>,
        types: List<PlaceTypeEntity>,
        photos: List<PlacePhotoEntity>,
        photoAuthors: List<PlacePhotoAuthorEntity>,
    ) {
        if (places.isEmpty()) return

        val placeIds = places.map(PlaceEntity::placeId).distinct()
        deletePhotoAuthors(placeIds)
        deletePhotos(placeIds)
        deleteTypes(placeIds)
        upsertPlaces(places)
        upsertTypes(types)
        upsertPhotos(photos)
        upsertPhotoAuthors(photoAuthors)
    }

    @Query("SELECT placeId FROM places WHERE placeId IN (:placeIds)")
    protected abstract suspend fun findExistingPlaceIds(placeIds: List<String>): List<String>

    @Transaction
    open suspend fun saveMissing(
        places: List<PlaceEntity>,
        types: List<PlaceTypeEntity>,
        photos: List<PlacePhotoEntity>,
        photoAuthors: List<PlacePhotoAuthorEntity>,
    ) {
        if (places.isEmpty()) return

        val existingPlaceIds = findExistingPlaceIds(
            places.map(PlaceEntity::placeId).distinct(),
        ).toSet()
        val missingPlaces = places.filterNot { place ->
            place.placeId in existingPlaceIds
        }
        if (missingPlaces.isEmpty()) return

        val missingPlaceIds = missingPlaces.map(PlaceEntity::placeId).toSet()
        upsertPlaces(missingPlaces)
        upsertTypes(types.filter { type -> type.placeId in missingPlaceIds })
        upsertPhotos(photos.filter { photo -> photo.placeId in missingPlaceIds })
        upsertPhotoAuthors(
            photoAuthors.filter { author -> author.placeId in missingPlaceIds },
        )
    }

    @Query("SELECT * FROM places WHERE placeId = :placeId")
    abstract suspend fun findPlace(placeId: String): PlaceEntity?

    @Query("SELECT * FROM place_types WHERE placeId = :placeId ORDER BY type")
    abstract suspend fun findTypes(placeId: String): List<PlaceTypeEntity>

    @Query("SELECT * FROM place_photos WHERE placeId = :placeId ORDER BY photoIndex")
    abstract suspend fun findPhotos(placeId: String): List<PlacePhotoEntity>

    @Query(
        "SELECT * FROM place_photo_authors " +
            "WHERE placeId = :placeId AND photoIndex = :photoIndex " +
            "ORDER BY authorIndex",
    )
    abstract suspend fun findPhotoAuthors(placeId: String, photoIndex: Int): List<PlacePhotoAuthorEntity>

    @Transaction
    @Query("SELECT * FROM places ORDER BY placeId")
    abstract fun observeAll(): Flow<List<PlaceWithRelations>>
}
