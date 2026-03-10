package com.sbdor.onlinestoreclaude.features.product_creation.components.controllers

import androidx.lifecycle.ViewModel
import com.sbdor.onlinestoreclaude.features.products.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductCreationDataViewModel : ViewModel() {


    val _state = MutableStateFlow<Product?>(value = null)
    val state = _state.asStateFlow()

    fun load(product: Product?) {
        _state.value = product ?: Product(id = -2142);
    }
}