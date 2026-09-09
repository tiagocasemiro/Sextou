package com.sextou.local.database

import androidx.room.Embedded
import androidx.room.Relation

data class PlaceWithRelations(
    @Embedded val place: PlaceEntity,
    @Relation(
        parentColumn = "placeId",
        entityColumn = "placeId",
        entity = PlaceTypeEntity::class,
    )
    val types: List<PlaceTypeEntity>,
    @Relation(
        parentColumn = "placeId",
        entityColumn = "placeId",
        entity = PlacePhotoEntity::class,
    )
    val photos: List<PlacePhotoWithAuthors>,
)

data class PlacePhotoWithAuthors(
    @Embedded val photo: PlacePhotoEntity,
    @Relation(
        parentColumn = "placeId",
        entityColumn = "placeId",
        entity = PlacePhotoAuthorEntity::class,
    )
    val authors: List<PlacePhotoAuthorEntity>,
)
