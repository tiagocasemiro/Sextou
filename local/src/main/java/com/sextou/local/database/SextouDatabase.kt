package com.sextou.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        VisitedPlaceEntity::class,
        PlaceEntity::class,
        PlaceTypeEntity::class,
        PlacePhotoEntity::class,
        PlacePhotoAuthorEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class SextouDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    abstract fun visitedPlaceDao(): VisitedPlaceDao

    abstract fun placesDao(): PlacesDao
}
