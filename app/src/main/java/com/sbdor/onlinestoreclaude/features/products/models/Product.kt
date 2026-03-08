package com.sbdor.onlinestoreclaude.features.products.models

// data class in Kotlin = immutable model with auto-generated equals, hashCode, toString, copy.
// Equivalent to @immutable class with copyWith in Flutter/Dart.
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: Category,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
)
