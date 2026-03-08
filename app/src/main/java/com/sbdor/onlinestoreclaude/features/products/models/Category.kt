package com.sbdor.onlinestoreclaude.features.products.models

// Enum class = a type with a fixed set of values.
// Each entry has a displayName property used in the UI.
enum class Category(val displayName: String) {
    ALL("All"),
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    BOOKS("Books"),
    HOME("Home"),
}
