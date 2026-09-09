package com.sextou.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ignored_places")
data class IgnoredPlaceEntity(
    @PrimaryKey val placeId: String,
    val selectedAt: Long,
)
