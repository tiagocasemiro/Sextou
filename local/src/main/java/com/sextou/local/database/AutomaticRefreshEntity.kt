package com.sextou.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automatic_refresh")
data class AutomaticRefreshEntity(
    @PrimaryKey val id: Int = 1,
    val successDay: Long,
)
