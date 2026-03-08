package com.sbdor.onlinestoreclaude.features.product_detail.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.features.cart.data.ICartRepository
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.models.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : ViewModel() {

    private val _state = MutableStateFlow<ProductDetailState>(ProductDetailState.Initial)
    val state: StateFlow<ProductDetailState> = _state.asStateFlow()

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
        }
    }
}
