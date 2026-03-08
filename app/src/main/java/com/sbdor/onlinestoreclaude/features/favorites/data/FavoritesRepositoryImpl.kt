package com.sbdor.onlinestoreclaude.features.favorites.data

import com.sbdor.onlinestoreclaude.features.favorites.models.Favorite
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor() : IFavoritesRepository {
    private val _cartItems = mutableListOf<Favorite>()

    override suspend fun favorites(): List<Favorite> {
        delay(3000)
        return _cartItems.toList()
    }

    override suspend fun addToFavorite(favorite: Favorite) {
        val findFavorite = _cartItems.find { it.product.id == favorite.product.id }
        if (findFavorite != null) return;
        _cartItems.add(favorite)
    }

    override suspend fun removeFavorite(favorite: Favorite) {
        _cartItems.removeAll { it.product.id == favorite.product.id }
    }
}