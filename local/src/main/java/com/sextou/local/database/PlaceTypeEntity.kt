package com.sextou.local.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "place_types",
    primaryKeys = ["placeId", "type"],
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
data class PlaceTypeEntity(
    val placeId: String,
    val type: String,
)
