package com.sbdor.onlinestoreclaude.features.favorites.controller

import com.sbdor.onlinestoreclaude.features.favorites.models.Favorite

sealed class FavoritesState {
    object Initial : FavoritesState()

    object InProgress : FavoritesState()

    object Error : FavoritesState()

    data class Completed(
        val favorites: List<Favorite>,
    ) : FavoritesState()
}