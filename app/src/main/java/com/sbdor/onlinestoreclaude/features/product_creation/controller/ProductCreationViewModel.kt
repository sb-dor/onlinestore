package com.sbdor.onlinestoreclaude.features.product_creation.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.core.viewModelFactory
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.models.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductCreationViewModel(private val productRepository: IProductRepository) : ViewModel() {


    companion object {
        fun factory(productRepository: IProductRepository) = viewModelFactory {
            ProductCreationViewModel(productRepository)
        }
    }

    val _state = MutableStateFlow<ProductCreationState>(value = ProductCreationState.Initial)
    val state = _state.asStateFlow()


    fun create(product: Product) {
        viewModelScope.launch {
            _state.value = ProductCreationState.InProgress
            val saveValue = productRepository.saveProduct(product)
            _state.value = if (saveValue) {
                ProductCreationState.Completed(product)
            } else {
                ProductCreationState.Error("Failed to save product")
            }

            delay(100)
            _state.value = ProductCreationState.Initial
        }
    }

}