package com.sbdor.onlinestoreclaude.di

import com.sbdor.onlinestoreclaude.features.cart.data.CartRepositoryImpl
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.order.data.IOrderRepository
import com.sbdor.onlinestoreclaude.features.order.data.OrderRepositoryImpl
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.data.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// @Module tells Hilt this class provides dependencies.
// @InstallIn(SingletonComponent::class) = these live for the entire app lifetime (like Singleton in Flutter's DI).
// @Binds = "when someone asks for IProductRepository, give them ProductRepositoryImpl".
// This is the interface-based DI pattern from the architecture guide.
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): IProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): ICartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): IOrderRepository
}
