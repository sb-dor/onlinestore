package com.sbdor.onlinestoreclaude.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sbdor.onlinestoreclaude.features.cart.components.CartScreen
import com.sbdor.onlinestoreclaude.features.favorites.components.FavoritesScreen
import com.sbdor.onlinestoreclaude.features.order.components.OrderScreen
import com.sbdor.onlinestoreclaude.features.order_history.components.OrderHistoryScreen
import com.sbdor.onlinestoreclaude.features.product_detail.components.ProductDetailScreen
import com.sbdor.onlinestoreclaude.features.products.components.ProductsScreen

// Defines each bottom navigation tab: its route, icon, and label.
// Keeping this as a data class rather than a sealed class because tabs are
// uniform -- they only differ in data, not in behavior.
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
)

// The three bottom nav tabs. Other routes (ProductDetail, Order, Favorites)
// are push navigation -- they appear on top of the current tab without
// replacing it in the bottom bar.
val bottomNavItems = listOf(
    BottomNavItem(AppRoute.Products.route, Icons.Default.Home, "Products"),
    BottomNavItem(AppRoute.Cart.route, Icons.Default.ShoppingCart, "Cart"),
    BottomNavItem(AppRoute.OrderHistory.route, Icons.Default.List, "History"),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // currentBackStackEntryAsState() = reactive stream of the current back stack entry.
    // Every time navigation happens, this recomposes -- allowing us to highlight
    // the correct tab and show/hide the bottom bar.
    // Dart/Flutter equivalent: GoRouter.of(context).location or watching route changes.
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show the bottom bar on the three root tab screens.
    // Push screens (ProductDetail, Order, Favorites) get a full-screen look with a back button.
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            // hierarchy check handles nested nav graphs correctly.
                            // For flat navigation like ours, it is equivalent to:
                            // selected = currentDestination?.route == item.route
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    // popUpTo the graph's start destination and SAVE the
                                    // current tab's back stack state before leaving.
                                    // This means if you are on ProductDetail (inside Products tab)
                                    // and tap Cart, the Products back stack is saved.
                                    // Tapping Products again restores it exactly.
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid creating multiple copies of the same tab on the stack
                                    // when tapping the already-selected tab.
                                    launchSingleTop = true
                                    // Restore the previously saved state of this tab.
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        // paddingValues includes the bottom navigation bar height.
        // Applying it here ensures every screen's content stops above the nav bar.
        NavHost(
            navController = navController,
            startDestination = AppRoute.Products.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(AppRoute.Products.route) {
                ProductsScreen(
                    onProductClick = { productId ->
                        navController.navigate(AppRoute.ProductDetail.createRoute(productId))
                    },
                    onFavoritesClick = {
                        navController.navigate(AppRoute.Favorites.route)
                    },
                )
            }

            composable(
                route = AppRoute.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = { navController.popBackStack() },
                    onCartClick = {
                        navController.navigate(AppRoute.Cart.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }

            composable(AppRoute.Cart.route) {
                CartScreen(
                    onBackClick = { navController.popBackStack() },
                    onCheckoutClick = { navController.navigate(AppRoute.Order.route) },
                )
            }

            composable(AppRoute.Favorites.route) {
                FavoritesScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable(AppRoute.OrderHistory.route) {
                OrderHistoryScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable(AppRoute.Order.route) {
                OrderScreen(
                    onBackClick = { navController.popBackStack() },
                    onContinueShoppingClick = {
                        navController.navigate(AppRoute.Products.route) {
                            popUpTo(AppRoute.Products.route) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
