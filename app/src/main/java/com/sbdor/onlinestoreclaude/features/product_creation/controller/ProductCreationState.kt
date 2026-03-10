package com.sbdor.onlinestoreclaude.features.product_creation.controller

import com.sbdor.onlinestoreclaude.features.products.models.Product

sealed class ProductCreationState {
    object Initial : ProductCreationState()
    object InProgress : ProductCreationState()
    data class Error(val message: String?) : ProductCreationState()
    data class Completed(val product: Product) : ProductCreationState()
}