package com.sbdor.onlinestoreclaude.navigation

// Sealed class holds all navigation routes in one place.
// Similar to how Flutter uses named routes or GoRouter paths.
sealed class AppRoute(val route: String) {
    object Products : AppRoute("products")
    object Cart : AppRoute("cart")
    object Order : AppRoute("order")

    object Favorites : AppRoute(route = "favorites")

    // ProductDetail uses a path parameter: product_detail/{productId}
    object ProductDetail : AppRoute("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
}
