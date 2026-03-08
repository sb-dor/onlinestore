package com.sbdor.onlinestoreclaude.features.product_detail.controller

import com.sbdor.onlinestoreclaude.features.products.models.Product

sealed class ProductDetailState {
    object Initial : ProductDetailState()
    object Loading : ProductDetailState()
    data class Error(val message: String) : ProductDetailState()
    data class Completed(
        val product: Product,
        val addedToCart: Boolean = false,
        var addedToFavorites: Boolean = false
    ) : ProductDetailState()
}
