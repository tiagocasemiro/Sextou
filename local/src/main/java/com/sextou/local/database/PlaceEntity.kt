package com.sextou.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val placeId: String,
    val displayName: String?,
    val formattedAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val primaryType: String?,
    val primaryTypeDisplayName: String?,
    val businessStatus: String,
    val rating: Double?,
    val userRatingCount: Int?,
    val priceLevel: Int?,
    val googleMapsUri: String?,
    val providerAttribution: String,
    val liveMusic: String = "NOT_AVAILABLE",
    val goodForChildren: String = "NOT_AVAILABLE",
)
