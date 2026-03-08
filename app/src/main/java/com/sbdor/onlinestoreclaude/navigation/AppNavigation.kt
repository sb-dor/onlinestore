package com.sbdor.onlinestoreclaude.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sbdor.onlinestoreclaude.features.cart.components.CartScreen
import com.sbdor.onlinestoreclaude.features.favorites.components.FavoritesScreen
import com.sbdor.onlinestoreclaude.features.order.components.OrderScreen
import com.sbdor.onlinestoreclaude.features.product_detail.components.ProductDetailScreen
import com.sbdor.onlinestoreclaude.features.products.components.ProductsScreen

// AppNavigation is the single place that wires all screens together.
// NavHost = the container that knows which screen to show.
// rememberNavController = the controller that handles back stack and navigation.
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Products.route,
    ) {
        composable(AppRoute.Products.route) {
            ProductsScreen(
                onProductClick = { productId ->
                    navController.navigate(AppRoute.ProductDetail.createRoute(productId))
                },
                onFavoritesClick = {
                    navController.navigate(AppRoute.Favorites.route)
                },
                onCartClick = {
                    navController.navigate(AppRoute.Cart.route)
                },
            )
        }

        composable(
            route = AppRoute.ProductDetail.route,
            // Declare the type of the argument so Navigation can parse it from the URL
            arguments = listOf(navArgument("productId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(AppRoute.Cart.route) },
            )
        }

        composable(AppRoute.Cart.route) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckoutClick = { navController.navigate(AppRoute.Order.route) },
            )
        }


        composable(AppRoute.Favorites.route) {
            FavoritesScreen(onBackClick = {
                navController.popBackStack()
            })
        }

        composable(AppRoute.Order.route) {
            OrderScreen(
                onBackClick = { navController.popBackStack() },
                onContinueShoppingClick = {
                    // Clear the entire back stack so the user can't go back to the order
                    navController.navigate(AppRoute.Products.route) {
                        popUpTo(AppRoute.Products.route) { inclusive = true }
                    }
                },
            )
        }
    }
}
