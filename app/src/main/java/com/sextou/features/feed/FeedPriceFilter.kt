package com.sextou.features.feed

/** Maps the Feed's price cards to the price levels returned by Places. */
internal object FeedPriceFilter {
    private val priceLevelsByOption = mapOf(
        FeedFilterOption.PRICE_LOW to setOf(0, 1),
        FeedFilterOption.PRICE_MEDIUM to setOf(2),
        FeedFilterOption.PRICE_HIGH to setOf(3, 4),
    )

    fun matches(
        priceLevel: Int?,
        selectedOptions: Set<FeedFilterOption>,
    ): Boolean {
        val selectedPriceLevels = selectedOptions
            .asSequence()
            .flatMap { priceLevelsByOption[it].orEmpty().asSequence() }
            .toSet()

        if (selectedPriceLevels.isEmpty()) return true

        return priceLevel in selectedPriceLevels
    }
}
