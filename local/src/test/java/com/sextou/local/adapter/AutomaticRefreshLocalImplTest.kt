package com.sextou.local.adapter

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sextou.domain.Failure
import com.sextou.domain.Success
import com.sextou.local.database.AutomaticRefreshDao
import com.sextou.local.database.AutomaticRefreshEntity
import kotlinx.coroutines.CancellationException
import com.sextou.local.database.SextouDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AutomaticRefreshLocalImplTest {
    private lateinit var database: SextouDatabase
    private lateinit var repository: AutomaticRefreshLocalImpl

    @Before
    fun beforeEach() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(), SextouDatabase::class.java,
        ).build()
        repository = AutomaticRefreshLocalImpl(database.automaticRefreshDao())
    }

    @After
    fun afterEach() { database.close() }

    @Test
    fun `absent marker maps to zero`() = runTest {
        assertEquals(Success(0L), repository.lastSuccessDay())
    }

    @Test
    fun `replaces the previous civil day`() = runTest {
        assertEquals(Success(Unit), repository.recordSuccessDay(20260911L))
        assertEquals(Success(Unit), repository.recordSuccessDay(20260912L))
        assertEquals(Success(20260912L), repository.lastSuccessDay())
    }

    @Test(expected = CancellationException::class)
    fun `propagates Room cancellation after database closes`() = runTest {
        repository.recordSuccessDay(20260911L)
        database.close()
        repository.lastSuccessDay()
        Unit
    }

    @Test
    fun `maps storage exceptions to domain failures`() = runTest {
        val failed = AutomaticRefreshLocalImpl(object : AutomaticRefreshDao {
            override suspend fun lastSuccessDay(): Long? = throw java.io.IOException("Read failure")
            override suspend fun recordSuccess(entity: AutomaticRefreshEntity) { throw java.io.IOException("Write failure") }
        })
        assertTrue(failed.lastSuccessDay() is Failure)
        assertTrue(failed.recordSuccessDay(20260911L) is Failure)
    }
}
