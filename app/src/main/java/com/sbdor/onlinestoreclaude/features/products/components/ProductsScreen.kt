package com.sbdor.onlinestoreclaude.features.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sbdor.onlinestoreclaude.features.products.controller.ProductsState
import com.sbdor.onlinestoreclaude.features.products.controller.ProductsViewModel
import com.sbdor.onlinestoreclaude.features.products.models.Category
import com.sbdor.onlinestoreclaude.features.products.components.components.CategoryChips
import com.sbdor.onlinestoreclaude.features.products.components.components.ProductCard
import com.sbdor.onlinestoreclaude.features.products.components.components.SearchBarWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    // hiltViewModel() = Hilt creates and provides the ViewModel automatically.
    // Equivalent to getting a controller from DependenciesScope.of(context) in Flutter.
    viewModel: ProductsViewModel = hiltViewModel(),
) {
    // collectAsState() = subscribes to the StateFlow and recomposes when it changes.
    // Equivalent to ListenableBuilder or setState in Flutter.
    val state by viewModel.state.collectAsState()

    // remember = stores state that survives recomposition (like a local variable in a StatefulWidget).
    // mutableStateOf = triggers recomposition when value changes.
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.ALL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Online Store") },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            SearchBarWidget(
                query = searchQuery,
                onQueryChange = { query ->
                    searchQuery = query
                    viewModel.load(query = query, category = selectedCategory)
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    viewModel.load(query = searchQuery, category = category)
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            // when() in Kotlin = pattern matching on sealed class, like switch() in Dart.
            // The compiler forces you to handle every state — no missing cases.
            when (val currentState = state) {
                is ProductsState.Initial, is ProductsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ProductsState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                is ProductsState.Completed -> {
                    if (currentState.products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("No products found")
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(currentState.products, key = { it.id }) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
