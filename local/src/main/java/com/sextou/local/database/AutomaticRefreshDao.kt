package com.sextou.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AutomaticRefreshDao {
    @Query("SELECT successDay FROM automatic_refresh WHERE id = 1")
    suspend fun lastSuccessDay(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordSuccess(entity: AutomaticRefreshEntity)
}
