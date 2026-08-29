package com.sextou.features.feed

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sextou.R

enum class FeedTab {
    MAP,
    FEED,
    FAVORITES,
}

enum class FeedPlaceStatus {
    OPEN,
    CLOSED,
}

data class FeedPlaceUiModel(
    val id: String,
    @param:StringRes val categoryResId: Int = 0,
    @param:StringRes val nameResId: Int = 0,
    @param:StringRes val highlightResId: Int = 0,
    @param:StringRes val distanceResId: Int = 0,
    val categoryText: String? = null,
    val nameText: String? = null,
    val highlightText: String? = null,
    val distanceMeters: Double? = null,
    val providerAttribution: String? = null,
    @param:StringRes val hoursResId: Int = 0,
    val hoursText: String? = null,
    val rating: Float? = null,
    val ratingsCount: Int? = null,
    val priceLevel: Int? = null,
    @param:StringRes val priceDescriptionResId: Int = R.string.feed_price_description,
    val status: FeedPlaceStatus? = FeedPlaceStatus.OPEN,
    @param:DrawableRes val imageResId: Int? = null,
    @param:StringRes val placeholderEmojiResId: Int? = null,
    val searchableText: String = "",
)

data class FeedUiState(
    val query: String = "",
    val places: List<FeedPlaceUiModel> = emptyList(),
    val favoritePlaceIds: Set<String> = emptySet(),
    val visitedPlaceIds: Set<String> = emptySet(),
    val selectedTab: FeedTab = FeedTab.FEED,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isStale: Boolean = false,
    @param:StringRes val errorMessageResId: Int? = null,
    @param:StringRes val actionErrorMessageResId: Int? = null,
    val isFilterDialogVisible: Boolean = false,
    val openOnly: Boolean = false,
    val providerAttribution: String? = null,
) {
    val visiblePlaces: List<FeedPlaceUiModel>
        get() = places
            .filter { selectedTab != FeedTab.FAVORITES || it.id in favoritePlaceIds }
            .filter { !openOnly || it.status != FeedPlaceStatus.CLOSED }

    companion object
}

fun FeedUiState.Companion.preview(): FeedUiState = FeedUiState(places = feedPlaces())

internal fun feedPlaces(): List<FeedPlaceUiModel> = listOf(
    FeedPlaceUiModel(
        id = "ao-ponto",
        categoryResId = R.string.feed_place_ao_ponto_category,
        nameResId = R.string.feed_place_ao_ponto_name,
        highlightResId = R.string.feed_place_ao_ponto_highlight,
        distanceResId = R.string.feed_place_ao_ponto_distance,
        hoursResId = R.string.feed_hours,
        rating = 4.6f,
        ratingsCount = 156,
        priceLevel = 2,
        imageResId = R.drawable.feed_ao_ponto,
        searchableText = "ao ponto bar restaurante",
    ),
    FeedPlaceUiModel(
        id = "bar-do-ninho",
        categoryResId = R.string.feed_place_ninho_category,
        nameResId = R.string.feed_place_ninho_name,
        highlightResId = R.string.feed_place_ninho_highlight,
        distanceResId = R.string.feed_place_ninho_distance,
        hoursResId = R.string.feed_hours,
        rating = 4.8f,
        ratingsCount = 193,
        priceLevel = 1,
        imageResId = R.drawable.feed_bar_do_ninho,
        searchableText = "bar do ninho boteco raiz",
    ),
    FeedPlaceUiModel(
        id = "bar-do-wanderley",
        categoryResId = R.string.feed_place_wanderley_category,
        nameResId = R.string.feed_place_wanderley_name,
        highlightResId = R.string.feed_place_wanderley_highlight,
        distanceResId = R.string.feed_place_wanderley_distance,
        hoursResId = R.string.feed_hours,
        rating = 4.9f,
        ratingsCount = 428,
        priceLevel = 1,
        imageResId = R.drawable.feed_bar_do_wanderley,
        searchableText = "bar do wanderley boteco raiz",
    ),
    FeedPlaceUiModel(
        id = "bar-do-jamil",
        categoryResId = R.string.feed_place_jamil_category,
        nameResId = R.string.feed_place_jamil_name,
        highlightResId = R.string.feed_place_jamil_highlight,
        distanceResId = R.string.feed_place_jamil_distance,
        hoursResId = R.string.feed_hours,
        rating = 4.8f,
        ratingsCount = 312,
        priceLevel = 1,
        placeholderEmojiResId = R.string.feed_emoji_beer,
        searchableText = "bar do jamil rei do litrao boteco raiz",
    ),
    FeedPlaceUiModel(
        id = "espetaria-do-tonho",
        categoryResId = R.string.feed_place_tonho_category,
        nameResId = R.string.feed_place_tonho_name,
        highlightResId = R.string.feed_place_tonho_highlight,
        distanceResId = R.string.feed_place_tonho_distance,
        hoursResId = R.string.feed_hours,
        rating = 4.6f,
        ratingsCount = 187,
        priceLevel = 1,
        placeholderEmojiResId = R.string.feed_emoji_skewer,
        searchableText = "espetaria do tonho espetinhos podroes",
    ),
    FeedPlaceUiModel(
        id = "karaoke-da-geralda",
        categoryResId = R.string.feed_place_geralda_category,
        nameResId = R.string.feed_place_geralda_name,
        highlightResId = R.string.feed_place_geralda_highlight,
        distanceResId = R.string.feed_place_geralda_distance,
        hoursResId = R.string.feed_hours,
        rating = 4.5f,
        ratingsCount = 98,
        priceLevel = 2,
        placeholderEmojiResId = R.string.feed_emoji_microphone,
        searchableText = "karaoke da geralda karaoke bar",
    ),
    FeedPlaceUiModel(
        id = "adega-sao-jorge",
        categoryResId = R.string.feed_place_adega_category,
        nameResId = R.string.feed_place_adega_name,
        highlightResId = R.string.feed_place_adega_highlight,
        distanceResId = R.string.feed_place_adega_distance,
        hoursResId = R.string.feed_hours,
        rating = 4.7f,
        ratingsCount = 241,
        priceLevel = 1,
        status = FeedPlaceStatus.CLOSED,
        placeholderEmojiResId = R.string.feed_emoji_wine,
        searchableText = "adega sao jorge adega petiscos",
    ),
)
