package com.sextou.local.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sextou.domain.places.model.PlaceStatus
import com.sextou.local.database.SextouDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalRepositoryTest {
    private lateinit var database: SextouDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SextouDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun favoriteIsPersistedAndCanBeRemoved() = runTest {
        val repository = FavoriteLocalRepository(database)

        repository.setSelected("place-1", selected = true)
        assertEquals(setOf("place-1"), repository.observeIds().first())

        repository.setSelected("place-1", selected = false)

        assertEquals(emptySet<String>(), repository.observeIds().first())
    }

    @Test
    fun visitedPlaceSelectionIsIdempotent() = runTest {
        val repository = VisitedPlaceLocalRepository(database)

        repository.setSelected("place-2", selected = true)
        repository.setSelected("place-2", selected = true)

        assertEquals(setOf("place-2"), repository.observeIds().first())
    }

    @Test
    fun ignoredPlaceSelectionIsPersistedAndCanBeRemoved() = runTest {
        val repository = IgnoredPlaceLocalRepository(database)

        repository.setSelected("place-3", selected = true)
        assertEquals(setOf("place-3"), repository.observeIds().first())

        repository.setSelected("place-3", selected = false)

        assertEquals(emptySet<String>(), repository.observeIds().first())
    }

    @Test
    fun placeStatusIsExclusiveAcrossFavoriteVisitedAndIgnoredTables() = runTest {
        val statusRepository = PlaceStatusLocalRepository(database)

        statusRepository.setStatus("place-4", PlaceStatus.FAVORITE)
        assertEquals(listOf("place-4"), database.favoriteDao().observeIds().first())
        assertEquals(emptyList<String>(), database.visitedPlaceDao().observeIds().first())
        assertEquals(emptyList<String>(), database.ignoredPlaceDao().observeIds().first())

        statusRepository.setStatus("place-4", PlaceStatus.VISITED)
        assertEquals(emptyList<String>(), database.favoriteDao().observeIds().first())
        assertEquals(listOf("place-4"), database.visitedPlaceDao().observeIds().first())
        assertEquals(emptyList<String>(), database.ignoredPlaceDao().observeIds().first())

        statusRepository.setStatus("place-4", PlaceStatus.IGNORED)
        assertEquals(emptyList<String>(), database.favoriteDao().observeIds().first())
        assertEquals(emptyList<String>(), database.visitedPlaceDao().observeIds().first())
        assertEquals(listOf("place-4"), database.ignoredDaoIds())

        statusRepository.setStatus("place-4", null)
        assertEquals(emptyList<String>(), database.favoriteDao().observeIds().first())
        assertEquals(emptyList<String>(), database.visitedPlaceDao().observeIds().first())
        assertEquals(emptyList<String>(), database.ignoredDaoIds())
    }

    private suspend fun SextouDatabase.ignoredDaoIds(): List<String> = ignoredPlaceDao()
        .observeIds()
        .first()
}
