package com.sbdor.onlinestoreclaude.core

import android.content.Context
import com.sbdor.onlinestoreclaude.features.cart.data.CartRepositoryImpl
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.favorites.data.FavoritesRepositoryImpl
import com.sbdor.onlinestoreclaude.features.favorites.data.IFavoritesRepository
import com.sbdor.onlinestoreclaude.features.order.data.IOrderRepository
import com.sbdor.onlinestoreclaude.features.order.data.OrderRepositoryImpl
import com.sbdor.onlinestoreclaude.features.order_history.data.IOrderHistoryRepository
import com.sbdor.onlinestoreclaude.features.order_history.data.OrderHistoryImpl
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.data.ProductRepositoryImpl

class AppContainer(context: Context) {
    val sharedPreferencesManager = SharedPreferencesManager(context)
    val cartRepository: ICartRepository = CartRepositoryImpl()
    val favoritesRepository: IFavoritesRepository = FavoritesRepositoryImpl()
    val productRepository: IProductRepository = ProductRepositoryImpl()
    val orderHistoryRepository: IOrderHistoryRepository = OrderHistoryImpl(sharedPreferencesManager)
    val orderRepository: IOrderRepository = OrderRepositoryImpl(sharedPreferencesManager)
}
