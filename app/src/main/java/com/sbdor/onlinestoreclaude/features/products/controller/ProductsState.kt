package com.sbdor.onlinestoreclaude.features.products.controller

import com.sbdor.onlinestoreclaude.features.products.models.Category
import com.sbdor.onlinestoreclaude.features.products.models.Product

// Sealed class = Kotlin's equivalent of Dart's sealed/freezed class.
// It restricts all possible states to a known set — the compiler enforces exhaustive when() checks.
// Compare with: @freezed sealed class ProductsState with _$ProductsState { ... }
sealed class ProductsState {
    object Initial : ProductsState()
    object Loading : ProductsState()
    data class Error(val message: String) : ProductsState()
    data class Completed(
        val products: List<Product>,
        val searchQuery: String = "",
        val selectedCategory: Category = Category.ALL,
    ) : ProductsState()
}
