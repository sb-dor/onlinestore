package com.sbdor.onlinestoreclaude.features.product_detail.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.favorites.data.IFavoritesRepository
import com.sbdor.onlinestoreclaude.features.favorites.models.Favorite
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

// This ViewModel gets both IProductRepository and ICartRepository injected.
// ICartRepository has only one binding in AppModule so no @Named needed there.
// IProductRepository has two bindings (@Named("real") and @Named("fake")) so @Named is required.
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    @Named("real") private val productRepository: IProductRepository,
    private val cartRepository: ICartRepository,
    private val favoritesRepository: IFavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProductDetailState>(ProductDetailState.Initial)
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

    // Channel for one-shot snackbar events.
    // Unlike StateFlow, Channel does not replay the last value — each event is consumed once.
    // This is the correct tool for UI events like snackbars, toasts, and navigation triggers.
    private val _snackbarEvent = Channel<String>(Channel.BUFFERED)
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    fun load(productId: Int) {
        viewModelScope.launch {
            _state.value = ProductDetailState.Loading
            try {
                val product = productRepository.getProductById(productId)
                _state.value = if (product != null) {
                    ProductDetailState.Completed(product = product)
                } else {
                    ProductDetailState.Error("Product not found")
                }
            } catch (e: Exception) {
                _state.value = ProductDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addToCart(product: Product) {
        cartRepository.addToCart(product)
        // Smart cast: after checking `is Completed`, Kotlin knows the type — no casting needed.
        val current = _state.value
        if (current is ProductDetailState.Completed) {
            _state.value = current.copy(addedToCart = true)
            viewModelScope.launch {
                _snackbarEvent.send("${product.name} added to cart")
            }
        }
    }

    fun addToFavorites(product: Product) {
        viewModelScope.launch {
            favoritesRepository.addToFavorite(Favorite(product = product))
            val current = _state.value;
            if (current is ProductDetailState.Completed) {
                _state.value = current.copy(addedToFavorites = true)
            }
        }
    }
}
