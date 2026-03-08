package com.sbdor.onlinestoreclaude.features.products.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.models.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

// @HiltViewModel = Hilt knows how to create this ViewModel and inject its dependencies.
// Equivalent to the Flutter controller that receives an IExampleRepository via constructor.
// ViewModel survives screen rotation (unlike plain classes).
@HiltViewModel
class ProductsViewModel @Inject constructor(
    // @Named("real") = tells Hilt which implementation of IProductRepository to inject.
    // Two bindings exist in AppModule: @Named("real") and @Named("fake").
    // To switch to fake data for testing, change "real" to "fake" here — nothing else changes.
    @Named("real") private val productRepository: IProductRepository,
) : ViewModel() {

    // MutableStateFlow = the internal mutable state (private).
    // StateFlow = the public read-only state exposed to the UI.
    // Equivalent to Dart's StreamController or BehaviorSubject pattern.
    private val _state = MutableStateFlow<ProductsState>(ProductsState.Initial)
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    init {
        load()
    }

    // viewModelScope = a coroutine scope tied to the ViewModel's lifecycle.
    // When the ViewModel is destroyed, all coroutines are automatically cancelled.
    // Equivalent to Dart's async/await inside a controller method.
    fun load(query: String = "", category: Category = Category.ALL) {
        viewModelScope.launch {
            _state.value = ProductsState.Loading
            try {
                val products = productRepository.searchProducts(query, category)
                _state.value = ProductsState.Completed(
                    products = products,
                    searchQuery = query,
                    selectedCategory = category,
                )
            } catch (e: Exception) {
                _state.value = ProductsState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
