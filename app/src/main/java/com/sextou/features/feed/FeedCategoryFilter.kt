package com.sextou.features.feed

import java.util.Locale

/** Applies the category chips to the data available in the Feed. */
internal object FeedCategoryFilter {
    private val categoryOptions = setOf(
        FeedFilterOption.CATEGORY_KARAOKE,
        FeedFilterOption.CATEGORY_KIDS,
        FeedFilterOption.CATEGORY_STREET_FOOD,
        FeedFilterOption.CATEGORY_WINE_STORE,
        FeedFilterOption.CATEGORY_LIVE_MUSIC,
        FeedFilterOption.CATEGORY_24_HOURS,
    )

    private val placeTypesByOption = mapOf(
        FeedFilterOption.CATEGORY_KARAOKE to setOf("karaoke"),
        FeedFilterOption.CATEGORY_STREET_FOOD to setOf(
            "barbecue_restaurant",
            "fast_food_restaurant",
            "hamburger_restaurant",
            "hot_dog_restaurant",
            "hot_dog_stand",
            "meal_delivery",
            "meal_takeaway",
            "sandwich_shop",
            "snack_bar",
        ),
        FeedFilterOption.CATEGORY_WINE_STORE to setOf("wine_bar", "winery"),
        FeedFilterOption.CATEGORY_LIVE_MUSIC to setOf("live_music_venue"),
    )

    fun matches(
        place: FeedPlaceUiModel,
        selectedOptions: Set<FeedFilterOption>,
    ): Boolean {
        val selectedCategories = selectedOptions intersect categoryOptions
        if (selectedCategories.isEmpty()) return true

        return selectedCategories.any { option ->
            when (option) {
                FeedFilterOption.CATEGORY_KIDS -> place.goodForChildren == true
                FeedFilterOption.CATEGORY_LIVE_MUSIC -> {
                    place.liveMusic == true || place.hasPlaceType(option)
                }
                FeedFilterOption.CATEGORY_24_HOURS -> place.isOpen24Hours == true
                else -> place.hasPlaceType(option)
            }
        }
    }

    private fun FeedPlaceUiModel.hasPlaceType(option: FeedFilterOption): Boolean {
        val expectedTypes = placeTypesByOption[option].orEmpty()
        return placeTypes.any { type ->
            type.trim().lowercase(Locale.ROOT) in expectedTypes
        }
    }
}
