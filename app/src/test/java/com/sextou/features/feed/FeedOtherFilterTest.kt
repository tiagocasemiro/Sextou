package com.sextou.features.feed

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedOtherFilterTest {
    @Test
    fun noOtherFilterSelectionKeepsAPlaceVisible() {
        assertTrue(
            FeedOtherFilter.matches(
                place = place(),
                selectedOptions = emptySet(),
            ),
        )
    }

    @Test
    fun openNowRequiresAnExplicitOpenValue() {
        assertTrue(
            FeedOtherFilter.matches(
                place = place(isOpen = true),
                selectedOptions = setOf(FeedFilterOption.OPEN_NOW),
            ),
        )
        assertFalse(
            FeedOtherFilter.matches(
                place = place(isOpen = false),
                selectedOptions = setOf(FeedFilterOption.OPEN_NOW),
            ),
        )
        assertFalse(
            FeedOtherFilter.matches(
                place = place(isOpen = null),
                selectedOptions = setOf(FeedFilterOption.OPEN_NOW),
            ),
        )
    }

    @Test
    fun kidsSpaceRequiresAPositiveStructuredAttribute() {
        assertTrue(
            FeedOtherFilter.matches(
                place = place(goodForChildren = true),
                selectedOptions = setOf(FeedFilterOption.KIDS_SPACE),
            ),
        )
        assertFalse(
            FeedOtherFilter.matches(
                place = place(goodForChildren = false),
                selectedOptions = setOf(FeedFilterOption.KIDS_SPACE),
            ),
        )
        assertFalse(
            FeedOtherFilter.matches(
                place = place(goodForChildren = null),
                selectedOptions = setOf(FeedFilterOption.KIDS_SPACE),
            ),
        )
    }

    @Test
    fun liveMusicRequiresAPositiveStructuredAttribute() {
        assertTrue(
            FeedOtherFilter.matches(
                place = place(liveMusic = true),
                selectedOptions = setOf(FeedFilterOption.LIVE_MUSIC),
            ),
        )
        assertFalse(
            FeedOtherFilter.matches(
                place = place(liveMusic = false),
                selectedOptions = setOf(FeedFilterOption.LIVE_MUSIC),
            ),
        )
        assertFalse(
            FeedOtherFilter.matches(
                place = place(liveMusic = null),
                selectedOptions = setOf(FeedFilterOption.LIVE_MUSIC),
            ),
        )
    }

    @Test
    fun multipleOtherFiltersUseIntersection() {
        assertTrue(
            FeedOtherFilter.matches(
                place = place(isOpen = true, goodForChildren = true, liveMusic = true),
                selectedOptions = setOf(
                    FeedFilterOption.OPEN_NOW,
                    FeedFilterOption.KIDS_SPACE,
                    FeedFilterOption.LIVE_MUSIC,
                ),
            ),
        )
        assertFalse(
            FeedOtherFilter.matches(
                place = place(isOpen = true, goodForChildren = true, liveMusic = false),
                selectedOptions = setOf(
                    FeedFilterOption.OPEN_NOW,
                    FeedFilterOption.KIDS_SPACE,
                    FeedFilterOption.LIVE_MUSIC,
                ),
            ),
        )
    }

    @Test
    fun unrelatedFilterOptionsDoNotHideAPlace() {
        assertTrue(
            FeedOtherFilter.matches(
                place = place(),
                selectedOptions = setOf(FeedFilterOption.PRICE_MEDIUM),
            ),
        )
    }
}

private fun place(
    isOpen: Boolean? = null,
    goodForChildren: Boolean? = null,
    liveMusic: Boolean? = null,
) = FeedPlaceUiModel(
    id = "place-1",
    isOpen = isOpen,
    goodForChildren = goodForChildren,
    liveMusic = liveMusic,
)
