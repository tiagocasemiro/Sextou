package com.sextou.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AutomaticRefreshEntity::class,
        FavoriteEntity::class,
        VisitedPlaceEntity::class,
        IgnoredPlaceEntity::class,
        PlaceEntity::class,
        PlaceTypeEntity::class,
        PlacePhotoEntity::class,
        PlacePhotoAuthorEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class SextouDatabase : RoomDatabase() {
    abstract fun automaticRefreshDao(): AutomaticRefreshDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun visitedPlaceDao(): VisitedPlaceDao

    abstract fun ignoredPlaceDao(): IgnoredPlaceDao

    abstract fun placesDao(): PlacesDao
}
