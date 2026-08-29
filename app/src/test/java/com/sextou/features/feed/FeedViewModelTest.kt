package com.sextou.features.feed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedViewModelTest {
    @Test
    fun queryFiltersPlacesByNameAndCategory() {
        val viewModel = FeedViewModel()

        viewModel.onQueryChanged("espetinhos")

        assertEquals(
            listOf("espetaria-do-tonho"),
            viewModel.uiState.value.places.map(FeedPlaceUiModel::id),
        )
    }

    @Test
    fun blankQueryRestoresAllPlaces() {
        val viewModel = FeedViewModel()

        viewModel.onQueryChanged("bar")
        viewModel.onQueryChanged("   ")

        assertEquals(feedPlaces().size, viewModel.uiState.value.places.size)
    }

    @Test
    fun favoriteAndVisitedActionsToggleIndependently() {
        val viewModel = FeedViewModel()

        viewModel.onFavoriteClicked("ao-ponto")
        viewModel.onVisitedClicked("ao-ponto")

        assertTrue("ao-ponto" in viewModel.uiState.value.favoritePlaceIds)
        assertTrue("ao-ponto" in viewModel.uiState.value.visitedPlaceIds)

        viewModel.onFavoriteClicked("ao-ponto")
        viewModel.onVisitedClicked("ao-ponto")

        assertTrue(viewModel.uiState.value.favoritePlaceIds.isEmpty())
        assertTrue(viewModel.uiState.value.visitedPlaceIds.isEmpty())
    }

    @Test
    fun tabSelectionIsReflectedInUiState() {
        val viewModel = FeedViewModel()

        viewModel.onTabSelected(FeedTab.FAVORITES)

        assertEquals(FeedTab.FAVORITES, viewModel.uiState.value.selectedTab)
    }
}
