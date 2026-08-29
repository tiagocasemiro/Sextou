package com.sextou.networking.response

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceResponsesTest {
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
