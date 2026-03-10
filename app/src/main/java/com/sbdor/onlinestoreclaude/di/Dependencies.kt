package com.sbdor.onlinestoreclaude.di

import android.content.Context
import com.sbdor.onlinestoreclaude.core.SharedPreferencesManager
import com.sbdor.onlinestoreclaude.features.cart.data.CartRepositoryImpl
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.favorites.data.FavoritesRepositoryImpl
import com.sbdor.onlinestoreclaude.features.favorites.data.IFavoritesRepository
import com.sbdor.onlinestoreclaude.features.order.data.IOrderRepository
import com.sbdor.onlinestoreclaude.features.order.data.OrderRepositoryImpl
import com.sbdor.onlinestoreclaude.features.order_history.data.IOrderHistoryRepository
import com.sbdor.onlinestoreclaude.features.order_history.data.OrderHistoryImpl
import com.sbdor.onlinestoreclaude.features.order_history_deletion.data.IOrderHistoryDeletionRepository
import com.sbdor.onlinestoreclaude.features.order_history_deletion.data.OrderHistoryDeletionRepository
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.data.ProductRepositoryImpl

// ---------------------------------------------------------------------------
// Dependencies — manual dependency injection container (replaces Hilt's @Module)
// ---------------------------------------------------------------------------
// This class is the single place where all dependencies are created and wired.
// Instead of Hilt reading @Inject annotations across dozens of files and generating
// code behind the scenes, here everything is explicit and readable.
//
// HILT EQUIVALENT:
//   @Module @InstallIn(SingletonComponent::class)
//   abstract class AppModule {
//       @Binds @Singleton abstract fun bindCart(impl: CartRepositoryImpl): ICartRepository
//       @Binds @Singleton abstract fun bindFavorites(impl: FavoritesRepositoryImpl): IFavoritesRepository
//       ...
//   }
//
// With manual DI: just declare the properties here — no annotations, no code generation.
// ---------------------------------------------------------------------------
class Dependencies(context: Context) {

    // `by lazy` = the object is NOT created immediately when Dependencies is constructed.
    // It is created the FIRST time the property is accessed, then cached forever.
    //
    // Use `by lazy` when:
    //   - initialisation is expensive and may not always be needed
    //   - the dependency depends on another lazy property (avoids ordering issues)
    //
    // HILT EQUIVALENT: @Singleton already implies lazy construction — Hilt creates
    // the object the first time it is needed, not on app start.
    val sharedPreferencesManager: SharedPreferencesManager by lazy {
        SharedPreferencesManager(context)
    }

    // `by lazy` here demonstrates the pattern — CartRepositoryImpl() is cheap to create,
    // so eager initialisation would also be fine. Both approaches are shown intentionally.
    val cartRepository: ICartRepository by lazy {
        CartRepositoryImpl()
    }

    // Eager initialisation — object is created immediately when Dependencies is constructed.
    // Fine for cheap objects. The type annotation uses the INTERFACE (IFavoritesRepository),
    // not the concrete class — this is the key: swap the right-hand side to change the
    // implementation app-wide without touching any other file.
    val favoritesRepository: IFavoritesRepository = FavoritesRepositoryImpl()
    val productRepository: IProductRepository = ProductRepositoryImpl()

    // OrderHistoryImpl needs SharedPreferencesManager, so we pass it explicitly.
    // HILT EQUIVALENT: Hilt would have injected SharedPreferencesManager automatically
    // because it is @Singleton — here we pass it by hand.
    val orderHistoryRepository: IOrderHistoryRepository = OrderHistoryImpl(sharedPreferencesManager)
    val orderHistoryDeletionRepository: IOrderHistoryDeletionRepository = OrderHistoryDeletionRepository(sharedPreferencesManager)
    val orderRepository: IOrderRepository = OrderRepositoryImpl(sharedPreferencesManager)
}
