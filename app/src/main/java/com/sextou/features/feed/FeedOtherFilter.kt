package com.sextou.features.feed

/** Applies the structured attributes used by the Feed's other filters. */
internal object FeedOtherFilter {
    fun matches(
        place: FeedPlaceUiModel,
        selectedOptions: Set<FeedFilterOption>,
    ): Boolean {
        if (FeedFilterOption.OPEN_NOW in selectedOptions && place.isOpen != true) {
            return false
        }
        if (FeedFilterOption.KIDS_SPACE in selectedOptions && place.goodForChildren != true) {
            return false
        }
        if (FeedFilterOption.LIVE_MUSIC in selectedOptions && place.liveMusic != true) {
            return false
        }
        return true
    }
}
