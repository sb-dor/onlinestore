package com.sbdor.onlinestoreclaude.features.products.data

import com.sbdor.onlinestoreclaude.features.products.models.Category
import com.sbdor.onlinestoreclaude.features.products.models.Product
import javax.inject.Inject

// @Inject constructor = tells Hilt how to create this class automatically.
// No need to manually pass dependencies — Hilt will find and inject them.
class ProductRepositoryImpl @Inject constructor() : IProductRepository {

    // Fake in-memory data. In a real app this would come from a Retrofit API call or Room database.
    private val fakeProducts = listOf(
        Product(1, "Laptop Pro", "Powerful laptop for developers with 16GB RAM and fast SSD", 999.0, Category.ELECTRONICS, 4.5f, 120),
        Product(2, "Smartphone X", "Latest flagship phone with stunning camera and long battery", 699.0, Category.ELECTRONICS, 4.3f, 85),
        Product(3, "Wireless Headphones", "Noise cancelling headphones with 30h battery life", 199.0, Category.ELECTRONICS, 4.7f, 230),
        Product(4, "Classic T-Shirt", "Comfortable 100% cotton t-shirt available in many colors", 29.0, Category.CLOTHING, 4.1f, 450),
        Product(5, "Slim Jeans", "Modern slim fit jeans, durable and stylish", 59.0, Category.CLOTHING, 4.2f, 310),
        Product(6, "Running Shoes", "Lightweight shoes built for speed and comfort", 119.0, Category.CLOTHING, 4.6f, 180),
        Product(7, "Kotlin in Action", "The definitive guide to Kotlin programming language", 39.0, Category.BOOKS, 4.8f, 95),
        Product(8, "Jetpack Compose Book", "Learn modern Android UI development with Compose", 45.0, Category.BOOKS, 4.7f, 72),
        Product(9, "Coffee Maker", "Brew the perfect cup every morning with precision temperature", 89.0, Category.HOME, 4.4f, 160),
        Product(10, "Desk Lamp", "LED adjustable desk lamp with multiple brightness levels", 49.0, Category.HOME, 4.3f, 205),
    )

    override suspend fun getProducts(): List<Product> = fakeProducts

    override suspend fun getProductById(id: Int): Product? = fakeProducts.find { it.id == id }

    override suspend fun searchProducts(query: String, category: Category): List<Product> {
        return fakeProducts.filter { product ->
            val matchesQuery = query.isBlank() ||
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)
            val matchesCategory = category == Category.ALL || product.category == category
            matchesQuery && matchesCategory
        }
    }
}
