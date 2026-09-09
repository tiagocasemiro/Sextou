package com.sextou.local.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "place_photo_authors",
    primaryKeys = ["placeId", "photoIndex", "authorIndex"],
    foreignKeys = [
        ForeignKey(
            entity = PlacePhotoEntity::class,
            parentColumns = ["placeId", "photoIndex"],
            childColumns = ["placeId", "photoIndex"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["placeId", "photoIndex"])],
)
data class PlacePhotoAuthorEntity(
    val placeId: String,
    val photoIndex: Int,
    val authorIndex: Int,
    val name: String,
    val profileUri: String?,
    val photoUri: String?,
)
