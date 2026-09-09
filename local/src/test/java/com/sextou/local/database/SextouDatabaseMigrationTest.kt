package com.sextou.local.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SextouDatabaseMigrationTest {
    private lateinit var context: Context
    private var database: SextouDatabase? = null

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(DATABASE_NAME)

        context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null).use { legacyDatabase ->
            legacyDatabase.execSQL(
                "CREATE TABLE `favorite_places` " +
                    "(`placeId` TEXT NOT NULL, `selectedAt` INTEGER NOT NULL, PRIMARY KEY(`placeId`))",
            )
            legacyDatabase.execSQL(
                "CREATE TABLE `visited_places` " +
                    "(`placeId` TEXT NOT NULL, `selectedAt` INTEGER NOT NULL, PRIMARY KEY(`placeId`))",
            )
            legacyDatabase.execSQL(
                "INSERT INTO `favorite_places` (`placeId`, `selectedAt`) VALUES ('favorite-1', 1)",
            )
            legacyDatabase.version = 1
        }
    }

    @After
    fun tearDown() {
        database?.close()
        context.deleteDatabase(DATABASE_NAME)
    }

    @Test
    fun `migrates version one while preserving existing data and creating place tables`() = runTest {
        database = Room.databaseBuilder(context, SextouDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

        assertEquals(listOf("favorite-1"), database!!.favoriteDao().observeIds().first())
        assertEquals(null, database!!.placesDao().findPlace("place-1"))
        assertEquals(emptyList<PlaceTypeEntity>(), database!!.placesDao().findTypes("place-1"))
        assertEquals(emptyList<String>(), database!!.ignoredPlaceDao().observeIds().first())
    }

    private companion object {
        const val DATABASE_NAME = "sextou-migration-test.db"
    }
}
