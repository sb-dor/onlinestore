package com.sbdor.onlinestoreclaude.di

import com.sbdor.onlinestoreclaude.features.cart.data.CartRepositoryImpl
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.favorites.data.FavoritesRepositoryImpl
import com.sbdor.onlinestoreclaude.features.favorites.data.IFavoritesRepository
import com.sbdor.onlinestoreclaude.features.order.data.IOrderRepository
import com.sbdor.onlinestoreclaude.features.order.data.OrderLocalRepositoryImpl
import com.sbdor.onlinestoreclaude.features.order.data.OrderRepositoryImpl
import com.sbdor.onlinestoreclaude.features.products.data.FakeProductRepositoryImpl
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.data.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

// @Module tells Hilt this class provides dependencies.
// @InstallIn(SingletonComponent::class) = these live for the entire app lifetime.
// @Binds = "when someone asks for IProductRepository, give them ProductRepositoryImpl".
// This is the interface-based DI pattern from the architecture guide.
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    // ─────────────────────────────────────────────────────────────────────────
    // SINGLE IMPLEMENTATION (default, most common case)
    //
    // When there is only one implementation of an interface, no @Named is needed.
    // Hilt knows exactly which class to inject wherever ICartRepository is requested.
    // ─────────────────────────────────────────────────────────────────────────

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): ICartRepository

//    ---------------------------------------------------------------------------
//    WHY these bindings are commented out:
//    ---------------------------------------------------------------------------
//    IOrderRepository is NOT bound here because OrderViewModel uses @AssistedInject.
//    With @AssistedInject, the caller (OrderScreen) passes the IOrderRepository
//    implementation directly via factory.create(...) at runtime — Hilt does not
//    need to know about it in AppModule at all.
//
//    If you were using the @Named approach instead (Option 1 — SavedStateHandle),
//    you would uncomment these and inject via @Named on the ViewModel constructor:
//
//    @Binds @Singleton @Named("Remote")
//    abstract fun bindOrderRemoteRepository(impl: OrderRepositoryImpl): IOrderRepository
//
//    @Binds @Singleton @Named("Local")
//    abstract fun bindOrderLocalRepository(impl: OrderLocalRepositoryImpl): IOrderRepository
//    ---------------------------------------------------------------------------

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): IFavoritesRepository

    // ─────────────────────────────────────────────────────────────────────────
    // MULTIPLE IMPLEMENTATIONS (showcase — @Named qualifier)
    //
    // When two or more classes implement the same interface, Hilt cannot decide
    // which one to inject on its own — it will throw a compile-time error:
    //   "Cannot provide IProductRepository — it has multiple bindings."
    //
    // Fix: tag each binding with @Named("some_label") and use the same tag
    // on the constructor parameter in the ViewModel to pick the right one.
    //
    // Currently, the app uses @Named("real") in ProductsViewModel and
    // ProductDetailViewModel. To switch to fake data, change @Named("real")
    // to @Named("fake") in those ViewModels — no other code changes needed.
    // ─────────────────────────────────────────────────────────────────────────

    // Real implementation — talks to an actual API / database
    @Binds
    @Singleton
    @Named("real")
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): IProductRepository

    // Fake implementation — returns hardcoded data, useful for UI testing / early dev
    @Binds
    @Singleton
    @Named("fake")
    abstract fun bindFakeProductRepository(impl: FakeProductRepositoryImpl): IProductRepository
}
