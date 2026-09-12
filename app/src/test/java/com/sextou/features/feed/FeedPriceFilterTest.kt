package com.sextou.features.feed

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedPriceFilterTest {
    @Test
    fun noPriceSelectionKeepsAPlaceWithoutPriceVisible() {
        assertTrue(
            FeedPriceFilter.matches(
                priceLevel = null,
                selectedOptions = emptySet(),
            ),
        )
    }

    @Test
    fun lowPriceMatchesTheFreeLevel() {
        assertTrue(
            FeedPriceFilter.matches(
                priceLevel = 0,
                selectedOptions = setOf(FeedFilterOption.PRICE_LOW),
            ),
        )
    }

    @Test
    fun lowPriceMatchesTheInexpensiveLevel() {
        assertTrue(
            FeedPriceFilter.matches(
                priceLevel = 1,
                selectedOptions = setOf(FeedFilterOption.PRICE_LOW),
            ),
        )
    }

    @Test
    fun mediumPriceMatchesTheModerateLevel() {
        assertTrue(
            FeedPriceFilter.matches(
                priceLevel = 2,
                selectedOptions = setOf(FeedFilterOption.PRICE_MEDIUM),
            ),
        )
    }

    @Test
    fun highPriceMatchesTheExpensiveLevel() {
        assertTrue(
            FeedPriceFilter.matches(
                priceLevel = 3,
                selectedOptions = setOf(FeedFilterOption.PRICE_HIGH),
            ),
        )
    }

    @Test
    fun highPriceMatchesTheVeryExpensiveLevel() {
        assertTrue(
            FeedPriceFilter.matches(
                priceLevel = 4,
                selectedOptions = setOf(FeedFilterOption.PRICE_HIGH),
            ),
        )
    }

    @Test
    fun multiplePriceOptionsUseOrSemantics() {
        assertTrue(
            FeedPriceFilter.matches(
                priceLevel = 4,
                selectedOptions = setOf(
                    FeedFilterOption.PRICE_LOW,
                    FeedFilterOption.PRICE_HIGH,
                ),
            ),
        )
    }

    @Test
    fun aPriceOutsideTheSelectedRangeIsNotIncluded() {
        assertFalse(
            FeedPriceFilter.matches(
                priceLevel = 1,
                selectedOptions = setOf(FeedFilterOption.PRICE_MEDIUM),
            ),
        )
    }

    @Test
    fun anUnknownPriceIsNotIncludedWhenAPriceIsSelected() {
        assertFalse(
            FeedPriceFilter.matches(
                priceLevel = null,
                selectedOptions = setOf(FeedFilterOption.PRICE_LOW),
            ),
        )
    }

    @Test
    fun nonPriceFilterOptionsDoNotHidePlaces() {
        assertTrue(
            FeedPriceFilter.matches(
                priceLevel = null,
                selectedOptions = setOf(FeedFilterOption.TYPE_BOTECO),
            ),
        )
    }
}
