package com.sbdor.onlinestoreclaude.features.products.data

import com.sbdor.onlinestoreclaude.features.products.models.Category
import com.sbdor.onlinestoreclaude.features.products.models.Product

// Interface = abstract contract. Same pattern as abstract interface class in Dart.
// This allows swapping implementations (fake data, real API, etc.) without touching ViewModels.
interface IProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(id: Int): Product?
    suspend fun searchProducts(query: String, category: Category): List<Product>
}
