package com.sbdor.onlinestoreclaude.features.products.data

import com.sbdor.onlinestoreclaude.features.products.models.Category
import com.sbdor.onlinestoreclaude.features.products.models.Product
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// SHOWCASE: Multiple implementations of the same interface
// ─────────────────────────────────────────────────────────────────────────────
//
// This class is NOT wired into AppModule and NOT used anywhere in the app.
// It exists purely to show what a second implementation looks like and how
// you would register it alongside ProductRepositoryImpl using @Named.
//
// When you have two implementations of the same interface, Hilt needs a way
// to tell them apart. You use @Named (or a custom @Qualifier annotation):
//
//   In AppModule.kt you would add:
//
//     @Binds @Singleton @Named("real")
//     abstract fun bindRealProductRepository(impl: ProductRepositoryImpl): IProductRepository
//
//     @Binds @Singleton @Named("fake")
//     abstract fun bindFakeProductRepository(impl: FakeProductRepositoryImpl): IProductRepository
//
//   Then in the ViewModel you pick which one you want:
//
//     @HiltViewModel
//     class ProductsViewModel @Inject constructor(
//         @Named("fake") private val productRepository: IProductRepository,
//     ) : ViewModel()
//
//   Without @Named, Hilt would throw a compile-time error:
//   "Cannot provide IProductRepository — it has multiple bindings."
//
// ─────────────────────────────────────────────────────────────────────────────

class FakeProductRepositoryImpl @Inject constructor() : IProductRepository {

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
            val matchesQuery = query.isBlank() || product.name.contains(query, ignoreCase = true)
            val matchesCategory = category == Category.ALL || product.category == category
            matchesQuery && matchesCategory
        }
    }
}
