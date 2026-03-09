package com.sbdor.onlinestoreclaude.features.products.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sbdor.onlinestoreclaude.features.products.data.IProductRepository
import com.sbdor.onlinestoreclaude.features.products.models.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel survives screen rotation (unlike plain classes).
// Dependencies are now passed via the companion object factory instead of Hilt.
class ProductsViewModel(
    private val productRepository: IProductRepository,
) : ViewModel() {

    companion object {
        fun factory(productRepository: IProductRepository) = viewModelFactory {
            initializer { ProductsViewModel(productRepository) }
        }
    }

    // MutableStateFlow = the internal mutable state (private).
    // StateFlow = the public read-only state exposed to the UI.
    // Equivalent to Dart's StreamController or BehaviorSubject pattern.
    private val _state = MutableStateFlow<ProductsState>(ProductsState.Initial)
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    // init{} runs once when the ViewModel is first created — before any screen calls load().
    // Equivalent to calling _load() directly inside a Flutter controller constructor.
    // ALTERNATIVE: do NOT call load() here and instead use LaunchedEffect(Unit) in the screen.
    // Both work, but init{} keeps the screen completely passive (it never triggers a load).
    init {
        load()
    }

    // viewModelScope = a coroutine scope tied to the ViewModel's lifecycle.
    // When the ViewModel is destroyed, all coroutines are automatically cancelled.
    // Equivalent to Dart's async/await inside a controller method.
    //
    // NOTICE: viewModelScope.launch is required here because searchProducts() is a suspend fun.
    // Contrast with CartViewModel.load() which has no launch because its repository calls are synchronous.
    //
    // ALTERNATIVE: load() could be a suspend fun — but then the caller (screen or init block)
    // would need to provide the coroutine context:
    //
    // suspend fun load(...) {        // caller must use launch{} or be inside a coroutine
    //     _state.value = ProductsState.Loading
    //     val products = productRepository.searchProducts(query, category)
    //     _state.value = ProductsState.Completed(...)
    // }
    //
    // init { viewModelScope.launch { load() } }   // init would need launch if load() is suspend
    //
    // The current approach (normal fun + launch inside) is preferred — the screen can call
    // viewModel.load() from onClick or anywhere without needing a coroutine context.
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
