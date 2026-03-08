package com.sbdor.onlinestoreclaude.features.favorites.data

import com.sbdor.onlinestoreclaude.features.favorites.models.Favorite

interface IFavoritesRepository {
    suspend fun favorites(): List<Favorite>

    suspend fun addToFavorite(favorite: Favorite)

    suspend fun removeFavorite(favorite: Favorite)
}