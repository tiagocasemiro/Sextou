package com.sextou.features.feed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FeedViewModel : ViewModel() {
    private val allPlaces = feedPlaces()
    private val mutableUiState = MutableStateFlow(FeedUiState(places = allPlaces))

    val uiState: StateFlow<FeedUiState> = mutableUiState.asStateFlow()

    fun onQueryChanged(query: String) {
        val normalizedQuery = query.trim().lowercase()
        mutableUiState.update {
            it.copy(
                query = query,
                places = if (normalizedQuery.isEmpty()) {
                    allPlaces
                } else {
                    allPlaces.filter { place ->
                        place.searchableText.contains(normalizedQuery)
                    }
                },
            )
        }
    }

    fun onFavoriteClicked(placeId: String) {
        mutableUiState.update { state ->
            state.copy(
                favoritePlaceIds = state.favoritePlaceIds.toggle(placeId),
            )
        }
    }

    fun onVisitedClicked(placeId: String) {
        mutableUiState.update { state ->
            state.copy(
                visitedPlaceIds = state.visitedPlaceIds.toggle(placeId),
            )
        }
    }

    fun onTabSelected(tab: FeedTab) {
        mutableUiState.update { it.copy(selectedTab = tab) }
    }

    private fun Set<String>.toggle(value: String): Set<String> = if (contains(value)) {
        this - value
    } else {
        this + value
    }
}
