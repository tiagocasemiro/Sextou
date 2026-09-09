package com.sextou.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IgnoredPlaceDao {
    @Query("SELECT placeId FROM ignored_places ORDER BY selectedAt DESC")
    fun observeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: IgnoredPlaceEntity)

    @Query("DELETE FROM ignored_places WHERE placeId = :placeId")
    suspend fun delete(placeId: String)
}
