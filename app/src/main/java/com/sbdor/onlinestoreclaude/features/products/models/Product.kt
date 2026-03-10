package com.sbdor.onlinestoreclaude.features.products.models

// data class in Kotlin = immutable model with auto-generated equals, hashCode, toString, copy.
// Equivalent to @immutable class with copyWith in Flutter/Dart.
data class Product(
    val id: Int,
    val name: String?= null,
    val description: String?= null,
    val price: Double?= null,
    val category: Category?= null,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
)
