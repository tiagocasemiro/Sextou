package com.sextou.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visited_places")
data class VisitedPlaceEntity(
    @PrimaryKey val placeId: String,
    val selectedAt: Long,
)
