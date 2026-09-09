package com.sextou.local.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "place_photos",
    primaryKeys = ["placeId", "photoIndex"],
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["placeId"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["placeId"])],
)
data class PlacePhotoEntity(
    val placeId: String,
    val photoIndex: Int,
    val width: Int,
    val height: Int,
    val attributionHtml: String?,
    val googleMapsUri: String?,
    val flagContentUri: String?,
)
