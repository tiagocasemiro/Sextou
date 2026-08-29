package com.sextou.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_places")
data class FavoriteEntity(
    @PrimaryKey val placeId: String,
    val selectedAt: Long,
)
