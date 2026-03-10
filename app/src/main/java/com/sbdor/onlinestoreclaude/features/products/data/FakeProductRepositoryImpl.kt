package com.sbdor.onlinestoreclaude.features.products.data

import com.sbdor.onlinestoreclaude.features.products.models.Category
import com.sbdor.onlinestoreclaude.features.products.models.Product

// ─────────────────────────────────────────────────────────────────────────────
// SHOWCASE: Multiple implementations of the same interface
// ─────────────────────────────────────────────────────────────────────────────
//
// This class is NOT wired into AppContainer and NOT used anywhere in the app.
// It exists purely to show what a second implementation looks like.
//
// To switch to fake data, change AppContainer's productRepository line to:
//   val productRepository: IProductRepository = FakeProductRepositoryImpl()
//
// ─────────────────────────────────────────────────────────────────────────────

class FakeProductRepositoryImpl : IProductRepository {

    // Returns a minimal hardcoded list — useful during early development or UI testing
    // when you do not want to hit a real API.
    private val fakeProducts = listOf(
        Product(1, "Fake Laptop", "A placeholder laptop for UI testing", 0.0, Category.ELECTRONICS),
        Product(2, "Fake T-Shirt", "A placeholder shirt for UI testing", 0.0, Category.CLOTHING),
    )

    override suspend fun getProducts(): List<Product> = fakeProducts

    override suspend fun getProductById(id: Int): Product? = fakeProducts.find { it.id == id }

    override suspend fun searchProducts(query: String, category: Category): List<Product> {
        return fakeProducts.filter { product ->
            val matchesQuery =
                query.isBlank() || product.name?.contains(query, ignoreCase = true) ?: false
            val matchesCategory = category == Category.ALL || product.category == category
            matchesQuery && matchesCategory
        }
    }

    override suspend fun saveProduct(product: Product): Boolean {
        TODO("Not yet implemented")
    }
}
