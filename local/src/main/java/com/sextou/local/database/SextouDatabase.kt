package com.sextou.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteEntity::class, VisitedPlaceEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SextouDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    abstract fun visitedPlaceDao(): VisitedPlaceDao
}
