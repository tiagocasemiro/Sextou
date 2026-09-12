package com.sextou.domain.places.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceOpeningHoursTest {
    @Test
    fun midnightOpeningWithoutClosingMeansOpenTwentyFourHours() {
        assertTrue(
            openingHours(
                OpeningPeriod(
                    open = WeekTime("SUNDAY", hour = 0, minute = 0, date = null, truncated = false),
                    close = null,
                ),
            ).isOpen24Hours,
        )
    }

    @Test
    fun aRegularClosingTimeDoesNotMeanOpenTwentyFourHours() {
        assertFalse(
            openingHours(
                OpeningPeriod(
                    open = WeekTime("SUNDAY", hour = 0, minute = 0, date = null, truncated = false),
                    close = WeekTime("SUNDAY", hour = 23, minute = 59, date = null, truncated = false),
                ),
            ).isOpen24Hours,
        )
    }
}

private fun openingHours(period: OpeningPeriod) = PlaceOpeningHours(
    type = null,
    weekdayText = emptyList(),
    periods = listOf(period),
    specialDays = emptyList(),
)
