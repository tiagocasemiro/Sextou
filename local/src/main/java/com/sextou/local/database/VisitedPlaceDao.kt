package com.sextou.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitedPlaceDao {
    @Query("SELECT placeId FROM visited_places ORDER BY selectedAt DESC")
    fun observeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: VisitedPlaceEntity)

    @Query("DELETE FROM visited_places WHERE placeId = :placeId")
    suspend fun delete(placeId: String)
}
