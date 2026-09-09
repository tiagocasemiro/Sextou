package com.sextou.features.map.components

import com.sextou.designsystem.R as DesignSystemR
import org.junit.Assert.assertEquals
import org.junit.Test

class MapMarkerIconTest {
    @Test
    fun `favorite places use the brand flame marker`() {
        assertEquals(
            DesignSystemR.drawable.ic_sextou_map_marker_bombando,
            mapMarkerIconResource(
                placeId = "favorite-place",
                favoritePlaceIds = setOf("favorite-place"),
                ignoredPlaceIds = emptySet(),
            ),
        )
    }

    @Test
    fun `ignored places use the gray marker`() {
        assertEquals(
            DesignSystemR.drawable.ic_sextou_map_marker_ignorar,
            mapMarkerIconResource(
                placeId = "ignored-place",
                favoritePlaceIds = emptySet(),
                ignoredPlaceIds = setOf("ignored-place"),
            ),
        )
    }

    @Test
    fun `unmarked places keep the listed marker`() {
        assertEquals(
            DesignSystemR.drawable.ic_sextou_map_marker_listados,
            mapMarkerIconResource(
                placeId = "place-1",
                favoritePlaceIds = emptySet(),
                ignoredPlaceIds = emptySet(),
            ),
        )
    }

    @Test
    fun `ignored marker takes precedence when a place has both states`() {
        assertEquals(
            DesignSystemR.drawable.ic_sextou_map_marker_ignorar,
            mapMarkerIconResource(
                placeId = "place-1",
                favoritePlaceIds = setOf("place-1"),
                ignoredPlaceIds = setOf("place-1"),
            ),
        )
    }
}
