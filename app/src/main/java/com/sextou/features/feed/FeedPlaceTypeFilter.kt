package com.sextou.features.feed

import java.util.Locale

/** Maps the Feed's place-type chips to the official place types returned by Places. */
internal object FeedPlaceTypeFilter {
    private val placeTypesByOption = mapOf(
        FeedFilterOption.TYPE_BOTECO to setOf("bar"),
        FeedFilterOption.TYPE_SKEWER to setOf(
            "barbecue_restaurant",
            "fast_food_restaurant",
            "hamburger_restaurant",
            "hot_dog_restaurant",
            "hot_dog_stand",
            "meal_takeaway",
            "sandwich_shop",
            "snack_bar",
            "steak_house",
        ),
        FeedFilterOption.TYPE_WINE_STORE to setOf("wine_bar", "winery"),
        FeedFilterOption.TYPE_KARAOKE to setOf("karaoke"),
        FeedFilterOption.TYPE_FOOD_TRAILER to setOf(
            "fast_food_restaurant",
            "hot_dog_stand",
            "meal_takeaway",
            "sandwich_shop",
            "snack_bar",
        ),
    )

    fun matches(
        placeTypes: Set<String>,
        selectedOptions: Set<FeedFilterOption>,
    ): Boolean {
        val selectedPlaceTypes = selectedOptions
            .asSequence()
            .flatMap { placeTypesByOption[it].orEmpty().asSequence() }
            .toSet()

        if (selectedPlaceTypes.isEmpty()) return true

        return placeTypes.any { placeType ->
            placeType.trim().lowercase(Locale.ROOT) in selectedPlaceTypes
        }
    }
}
