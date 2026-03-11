package com.sbdor.onlinestoreclaude.navigation

// Sealed class holds all navigation routes in one place.
// Similar to how Flutter uses named routes or GoRouter paths.
sealed class AppRoute(val route: String) {
    object Products : AppRoute("products")
    object Cart : AppRoute("cart")
    object Order : AppRoute("order")

    object Favorites : AppRoute(route = "favorites")
    object OrderHistory : AppRoute(route = "order_history")


    // productId is an optional query param with -1 meaning "no product" (create mode).
    // IntType does not support nullable, so -1 is used as a sentinel value.
    // Create:  createRoute()  → "product_creation" → productId = -1
    // Edit:    createRoute(5) → "product_creation?productId=5" → productId = 5
    object ProductCreation : AppRoute(route = "product_creation?productId={productId}") {
        fun createRoute(productId: Int = -1) =
            if (productId != -1) "product_creation?productId=$productId"
            else "product_creation"
    }

    // ProductDetail uses a path parameter: product_detail/{productId}
    object ProductDetail : AppRoute("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
}
