package com.sextou.features.feed

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedCategoryFilterTest {
    @Test
    fun noCategorySelectionKeepsAPlaceVisible() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(),
                selectedOptions = emptySet(),
            ),
        )
    }

    @Test
    fun karaokeCategoryMatchesTheKaraokePlaceType() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("  KARAOKE  ")),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_KARAOKE),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("bar")),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_KARAOKE),
            ),
        )
    }

    @Test
    fun kidsCategoryRequiresPositiveChildFriendlyAttribute() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(goodForChildren = true),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_KIDS),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(goodForChildren = false),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_KIDS),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(goodForChildren = null),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_KIDS),
            ),
        )
    }

    @Test
    fun streetFoodCategoryMatchesSupportedFoodPlaceTypes() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("meal_takeaway")),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_STREET_FOOD),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("restaurant")),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_STREET_FOOD),
            ),
        )
    }

    @Test
    fun wineStoreCategoryMatchesWinePlaceTypes() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("winery")),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_WINE_STORE),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("liquor_store")),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_WINE_STORE),
            ),
        )
    }

    @Test
    fun liveMusicCategoryMatchesStructuredAttributeOrPlaceType() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(liveMusic = true),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_LIVE_MUSIC),
            ),
        )
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("live_music_venue")),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_LIVE_MUSIC),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(liveMusic = false),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_LIVE_MUSIC),
            ),
        )
    }

    @Test
    fun twentyFourHourCategoryRequiresPositiveScheduleSignal() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(isOpen24Hours = true),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_24_HOURS),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(isOpen24Hours = false),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_24_HOURS),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(isOpen24Hours = null),
                selectedOptions = setOf(FeedFilterOption.CATEGORY_24_HOURS),
            ),
        )
    }

    @Test
    fun multipleCategoriesUseUnionSemantics() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("winery")),
                selectedOptions = setOf(
                    FeedFilterOption.CATEGORY_KARAOKE,
                    FeedFilterOption.CATEGORY_WINE_STORE,
                ),
            ),
        )
        assertFalse(
            FeedCategoryFilter.matches(
                place = place(placeTypes = setOf("bar")),
                selectedOptions = setOf(
                    FeedFilterOption.CATEGORY_KARAOKE,
                    FeedFilterOption.CATEGORY_WINE_STORE,
                ),
            ),
        )
    }

    @Test
    fun nonCategoryOptionsDoNotHideAPlace() {
        assertTrue(
            FeedCategoryFilter.matches(
                place = place(),
                selectedOptions = setOf(FeedFilterOption.PRICE_LOW),
            ),
        )
    }
}

private fun place(
    placeTypes: Set<String> = emptySet(),
    goodForChildren: Boolean? = null,
    liveMusic: Boolean? = null,
    isOpen24Hours: Boolean? = null,
) = FeedPlaceUiModel(
    id = "place-1",
    placeTypes = placeTypes,
    goodForChildren = goodForChildren,
    liveMusic = liveMusic,
    isOpen24Hours = isOpen24Hours,
)
