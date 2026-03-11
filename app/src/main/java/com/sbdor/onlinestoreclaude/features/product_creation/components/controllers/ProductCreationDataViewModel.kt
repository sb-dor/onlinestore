package com.sbdor.onlinestoreclaude.features.product_creation.components.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.core.viewModelFactory
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductCreationDataViewModel(private val productRepository: IProductRepository) :
    ViewModel() {

    companion object {
        fun factory(productRepository: IProductRepository) = viewModelFactory {
            ProductCreationDataViewModel(productRepository)
        }
    }

    val _state = MutableStateFlow<Product?>(value = null)
    val state = _state.asStateFlow()

    fun load(productId: Int?) {
        viewModelScope.launch {
            if (productId != null) {
                val fetchedProduct = productRepository.getProductById(id = productId)
                _state.value = fetchedProduct ?: Product(id = -2142)
            } else {
                _state.value = Product(id = -2142)
            }
        }
    }
}