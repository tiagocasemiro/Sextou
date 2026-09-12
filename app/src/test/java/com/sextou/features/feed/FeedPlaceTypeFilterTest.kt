package com.sextou.features.feed

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedPlaceTypeFilterTest {
    @Test
    fun noPlaceTypeSelectionKeepsAPlaceVisible() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("restaurant"),
                selectedOptions = emptySet(),
            ),
        )
    }

    @Test
    fun botecoMatchesTheBarPlaceType() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("bar"),
                selectedOptions = setOf(FeedFilterOption.TYPE_BOTECO),
            ),
        )
    }

    @Test
    fun skewerMatchesARelevantFoodPlaceType() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("barbecue_restaurant"),
                selectedOptions = setOf(FeedFilterOption.TYPE_SKEWER),
            ),
        )
    }

    @Test
    fun wineStoreMatchesTheWinePlaceType() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("wine_bar"),
                selectedOptions = setOf(FeedFilterOption.TYPE_WINE_STORE),
            ),
        )
    }

    @Test
    fun karaokeMatchesTheKaraokePlaceType() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("karaoke"),
                selectedOptions = setOf(FeedFilterOption.TYPE_KARAOKE),
            ),
        )
    }

    @Test
    fun foodTrailerMatchesMealTakeaway() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("meal_takeaway"),
                selectedOptions = setOf(FeedFilterOption.TYPE_FOOD_TRAILER),
            ),
        )
    }

    @Test
    fun multiplePlaceTypesUseOrSemantics() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("karaoke"),
                selectedOptions = setOf(
                    FeedFilterOption.TYPE_BOTECO,
                    FeedFilterOption.TYPE_KARAOKE,
                ),
            ),
        )
    }

    @Test
    fun unrelatedPlaceTypeIsNotIncluded() {
        assertFalse(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("restaurant"),
                selectedOptions = setOf(FeedFilterOption.TYPE_BOTECO),
            ),
        )
    }

    @Test
    fun matchingIsCaseAndWhitespaceInsensitive() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("  BAR  "),
                selectedOptions = setOf(FeedFilterOption.TYPE_BOTECO),
            ),
        )
    }

    @Test
    fun nonPlaceFilterOptionsDoNotHidePlaces() {
        assertTrue(
            FeedPlaceTypeFilter.matches(
                placeTypes = setOf("restaurant"),
                selectedOptions = setOf(FeedFilterOption.PRICE_LOW),
            ),
        )
    }
}
