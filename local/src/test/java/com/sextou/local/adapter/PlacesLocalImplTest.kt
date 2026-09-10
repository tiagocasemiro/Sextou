package com.sextou.local.adapter

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.sextou.domain.Success
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlaceAuthor
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.local.database.PlaceEntity
import com.sextou.local.database.PlacePhotoAuthorEntity
import com.sextou.local.database.PlacePhotoEntity
import com.sextou.local.database.PlaceTypeEntity
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
class PlacesLocalImplTest {
    private lateinit var database: SextouDatabase
    private lateinit var repository: PlacesLocalImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SextouDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = PlacesLocalImpl(database.placesDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `persists place fields and nested values in normalized tables`() = runTest {
        val place = samplePlace()

        assertEquals(Success(Unit), repository.saveAll(listOf(place)))
        assertEquals(
            PlaceEntity(
                placeId = "place-1",
                displayName = "Bar do Bairro",
                formattedAddress = "Rua Principal, 10",
                latitude = -22.9,
                longitude = -43.2,
                primaryType = "bar",
                primaryTypeDisplayName = "Bar",
                businessStatus = "OPERATIONAL",
                rating = 4.6,
                userRatingCount = 120,
                priceLevel = 2,
                googleMapsUri = "https://maps.google.com/?q=place-1",
                providerAttribution = "Google Maps",
            ),
            database.placesDao().findPlace("place-1"),
        )
        assertEquals(
            listOf(
                PlaceTypeEntity("place-1", "bar"),
                PlaceTypeEntity("place-1", "restaurant"),
            ),
            database.placesDao().findTypes("place-1"),
        )
        assertEquals(
            listOf(
                PlacePhotoEntity(
                    placeId = "place-1",
                    photoIndex = 0,
                    width = 1_200,
                    height = 800,
                    attributionHtml = "<a>Foto</a>",
                    googleMapsUri = "https://maps.google.com/photo-1",
                    flagContentUri = "https://maps.google.com/flag-1",
                ),
            ),
            database.placesDao().findPhotos("place-1"),
        )
        assertEquals(
            listOf(
                PlacePhotoAuthorEntity(
                    placeId = "place-1",
                    photoIndex = 0,
                    authorIndex = 0,
                    name = "Autora",
                    profileUri = "https://example.invalid/autora",
                    photoUri = "https://example.invalid/autora.jpg",
                ),
            ),
            database.placesDao().findPhotoAuthors("place-1", photoIndex = 0),
        )
    }

    @Test
    fun `replacing a place also removes stale child collections`() = runTest {
        repository.saveAll(
            listOf(
                samplePlace(
                    types = listOf("bar", "restaurant"),
                    photos = listOf(samplePhoto(index = 0), samplePhoto(index = 1)),
                ),
            ),
        )

        repository.saveAll(
            listOf(
                samplePlace(
                    types = listOf("cafe"),
                    photos = listOf(samplePhoto(index = 0, authors = emptyList())),
                ),
            ),
        )

        assertEquals(listOf(PlaceTypeEntity("place-1", "cafe")), database.placesDao().findTypes("place-1"))
        assertEquals(1, database.placesDao().findPhotos("place-1").size)
        assertEquals(emptyList<PlacePhotoAuthorEntity>(), database.placesDao().findPhotoAuthors("place-1", 0))
        assertEquals(emptyList<PlacePhotoEntity>(), database.placesDao().findPhotos("unknown"))
    }

    @Test
    fun `saving missing places keeps existing data and adds every new place`() = runTest {
        val existingPlace = samplePlace()
        repository.saveAll(listOf(existingPlace))

        val changedExistingPlace = samplePlace(
            types = listOf("cafe"),
            photos = listOf(samplePhoto(authors = emptyList())),
        ).copy(displayName = "Nome atualizado")
        val newPlace = samplePlace(id = "place-2")

        assertEquals(
            Success(Unit),
            repository.saveMissing(listOf(changedExistingPlace, newPlace)),
        )

        assertEquals(
            listOf(existingPlace, newPlace),
            repository.observeAll().first(),
        )
    }

    @Test
    fun `observes every saved place and rebuilds its normalized relationships`() = runTest {
        val expected = samplePlace(
            photos = listOf(
                samplePhoto(),
                samplePhoto(
                    index = 1,
                    authors = listOf(
                        PlaceAuthor(
                            name = "Outro autor",
                            profileUri = "https://example.invalid/outro",
                            photoUri = "https://example.invalid/outro.jpg",
                        ),
                    ),
                ),
            ),
        )

        repository.saveAll(listOf(expected))

        assertEquals(listOf(expected), repository.observeAll().first())
    }
}

private fun samplePlace(
    id: String = "place-1",
    types: List<String> = listOf("bar", "restaurant"),
    photos: List<PlacePhotoReference> = listOf(samplePhoto(placeId = id)),
) = PlaceSummary(
    id = id,
    displayName = "Bar do Bairro",
    formattedAddress = "Rua Principal, 10",
    location = GeoPoint(-22.9, -43.2),
    primaryType = "bar",
    primaryTypeDisplayName = "Bar",
    types = types,
    businessStatus = BusinessStatus.OPERATIONAL,
    rating = 4.6,
    userRatingCount = 120,
    priceLevel = 2,
    googleMapsUri = "https://maps.google.com/?q=place-1",
    providerAttribution = "Google Maps",
    photos = photos,
)

private fun samplePhoto(
    placeId: String = "place-1",
    index: Int = 0,
    authors: List<PlaceAuthor> = listOf(
        PlaceAuthor(
            name = "Autora",
            profileUri = "https://example.invalid/autora",
            photoUri = "https://example.invalid/autora.jpg",
        ),
    ),
) = PlacePhotoReference(
    placeId = placeId,
    index = index,
    width = 1_200,
    height = 800,
    attributionHtml = "<a>Foto</a>",
    authors = authors,
    googleMapsUri = "https://maps.google.com/photo-1",
    flagContentUri = "https://maps.google.com/flag-1",
)
