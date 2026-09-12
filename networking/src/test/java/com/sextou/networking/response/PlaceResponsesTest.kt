package com.sextou.networking.response

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.DayOfWeek
import com.google.android.libraries.places.api.model.LocalTime
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Period
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.OpeningHours
import com.google.android.libraries.places.api.model.TimeOfWeek
import com.sextou.domain.places.model.PlaceAttribute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceResponsesTest {
    @Test
    fun summaryMapsOtherFilterSignalsFromPlacesFields() {
        val place = Place.builder()
            .setId("place-1")
            .setCurrentOpeningHours(
                OpeningHours.builder()
                    .zza(true)
                    .build(),
            )
            .setLiveMusic(Place.BooleanPlaceAttributeValue.TRUE)
            .setGoodForChildren(Place.BooleanPlaceAttributeValue.TRUE)
            .build()

        val summary = PlaceSummaryResponse(place).mapToDomain()

        assertTrue(summary.isOpen == true)
        assertEquals(PlaceAttribute.YES, summary.liveMusic)
        assertEquals(PlaceAttribute.YES, summary.goodForChildren)
    }

    @Test
    fun summaryMapsAnAlwaysOpenRegularSchedule() {
        val place = Place.builder()
            .setId("place-1")
            .setOpeningHours(
                OpeningHours.builder()
                    .setPeriods(
                        listOf(
                            Period.builder()
                                .setOpen(
                                    TimeOfWeek.newInstance(
                                        DayOfWeek.SUNDAY,
                                        LocalTime.newInstance(0, 0),
                                    ),
                                )
                                .build(),
                        ),
                    )
                    .build(),
            )
            .build()

        val summary = PlaceSummaryResponse(place).mapToDomain()

        assertTrue(summary.isOpen24Hours == true)
    }

    @Test
    fun summaryMapsTheFirstPartyPhotoMetadataToAPlacePhotoReference() {
        val place = Place.builder()
            .setId("place-1")
            .setDisplayName("Place 1")
            .setLocation(LatLng(-22.9, -43.2))
            .setPhotoMetadatas(
                listOf(
                    PhotoMetadata.builder("photo-1")
                        .setWidth(1_200)
                        .setHeight(800)
                        .setAttributions("Photo attribution")
                        .build(),
                ),
            )
            .build()

        val summary = PlaceSummaryResponse(place).mapToDomain()

        assertEquals(1, summary.photos.size)
        assertEquals("place-1", summary.photos.single().placeId)
        assertEquals(0, summary.photos.single().index)
        assertEquals(1_200, summary.photos.single().width)
        assertEquals(800, summary.photos.single().height)
        assertEquals("Photo attribution", summary.photos.single().attributionHtml)
    }
}
