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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()

        assertEquals(listOf("favorite-1"), database!!.favoriteDao().observeIds().first())
        assertEquals(null, database!!.placesDao().findPlace("place-1"))
        assertEquals(emptyList<PlaceTypeEntity>(), database!!.placesDao().findTypes("place-1"))
        assertEquals(emptyList<String>(), database!!.ignoredPlaceDao().observeIds().first())
    }

    @Test
    fun `version three migration preserves establishments and every selection and persists marker`() = runTest {
        database = Room.databaseBuilder(context, SextouDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
        database!!.openHelper.writableDatabase.apply {
            execSQL("INSERT INTO places (placeId, displayName, businessStatus, providerAttribution) VALUES ('saved', 'Original', 'UNKNOWN', 'Google Maps')")
            execSQL("INSERT INTO visited_places (placeId, selectedAt) VALUES ('visited', 2)")
            execSQL("INSERT INTO ignored_places (placeId, selectedAt) VALUES ('ignored', 3)")
        }
        database!!.close()
        context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null).use {
            it.execSQL("DROP TABLE automatic_refresh")
            it.version = 3
        }
        database = Room.databaseBuilder(context, SextouDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_3_4).build()
        assertEquals("Original", database!!.placesDao().findPlace("saved")?.displayName)
        assertEquals(listOf("favorite-1"), database!!.favoriteDao().observeIds().first())
        assertEquals(listOf("visited"), database!!.visitedPlaceDao().observeIds().first())
        assertEquals(listOf("ignored"), database!!.ignoredPlaceDao().observeIds().first())
        assertEquals(null, database!!.automaticRefreshDao().lastSuccessDay())
        database!!.automaticRefreshDao().recordSuccess(AutomaticRefreshEntity(successDay = 20260911L))
        database!!.close()
        database = Room.databaseBuilder(context, SextouDatabase::class.java, DATABASE_NAME).build()
        assertEquals(20260911L, database!!.automaticRefreshDao().lastSuccessDay())
    }

    private companion object {
        const val DATABASE_NAME = "sextou-migration-test.db"
    }
}
