package com.sextou.features.details

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceDetailsContactTest {
    @Test
    fun phoneMakesContactAvailable() {
        assertTrue(placeDetails(phone = "+55 21 99999-9999").hasContactInformation())
    }

    @Test
    fun websiteMakesContactAvailable() {
        assertTrue(placeDetails(website = "https://example.com").hasContactInformation())
    }

    @Test
    fun blankContactsAreUnavailable() {
        assertFalse(placeDetails(phone = " ", website = "\n").hasContactInformation())
    }
}

private fun placeDetails(
    phone: String? = null,
    website: String? = null,
) = PlaceDetailsUiModel(
    name = "Estabelecimento",
    address = null,
    phone = phone,
    website = website,
    summary = null,
    hours = emptyList(),
    rating = null,
    ratingsCount = null,
    providerAttribution = "Google Maps",
)
