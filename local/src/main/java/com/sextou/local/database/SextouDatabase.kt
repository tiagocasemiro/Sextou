package com.sextou.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        VisitedPlaceEntity::class,
        IgnoredPlaceEntity::class,
        PlaceEntity::class,
        PlaceTypeEntity::class,
        PlacePhotoEntity::class,
        PlacePhotoAuthorEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class SextouDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    abstract fun visitedPlaceDao(): VisitedPlaceDao

    abstract fun ignoredPlaceDao(): IgnoredPlaceDao

    abstract fun placesDao(): PlacesDao
}
