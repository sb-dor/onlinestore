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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sbdor.onlinestoreclaude.core.viewModelFactory
import com.sbdor.onlinestoreclaude.di.LocalDependenciesScope
import com.sbdor.onlinestoreclaude.features.products.controller.ProductsState
import com.sbdor.onlinestoreclaude.features.products.controller.ProductsViewModel
import com.sbdor.onlinestoreclaude.features.products.models.Category
import com.sbdor.onlinestoreclaude.features.products.components.components.CategoryChips
import com.sbdor.onlinestoreclaude.features.products.components.components.ProductCard
import com.sbdor.onlinestoreclaude.features.products.components.components.SearchBarWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onProductClick: (Int) -> Unit, onFavoritesClick: () -> Unit, onProductAddClick: () -> Unit
) {
    val container = LocalDependenciesScope.current
    val productsViewModel: ProductsViewModel = viewModel(factory = viewModelFactory {
        container.productsViewModel
    })
    // collectAsState() = subscribes to the StateFlow and recomposes when it changes.
    // Equivalent to ListenableBuilder or setState in Flutter.
    val state by productsViewModel.state.collectAsState()

    // remember = stores state that survives recomposition but is LOST on screen recreation
    // (e.g. process death, screen removed from back stack).
    // mutableStateOf = triggers recomposition when value changes.
    //
    // ALTERNATIVE: use rememberSaveable instead of remember to survive process death and
    // configuration changes (screen rotation). For simple types like String and enums,
    // rememberSaveable works automatically:
    //
    // var searchQuery by rememberSaveable { mutableStateOf("") }
    //
    // ANOTHER ALTERNATIVE: move searchQuery and selectedCategory into the ViewModel's state
    // (as fields on ProductsState.Completed). They already exist there as searchQuery and
    // selectedCategory. The local remember here is a UI-only copy that stays in sync by
    // passing both to viewModel.load() on every change.
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.ALL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Online Store") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                },
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = onProductAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }) { paddingValues ->
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
                    productsViewModel.load(query = query, category = selectedCategory)
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    productsViewModel.load(query = searchQuery, category = category)
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            // NOTICE: there is no LaunchedEffect here to trigger load().
            // ProductsViewModel calls load() in its init{} block, so data loads automatically
            // the moment the ViewModel is created — before this screen even renders.
            // Contrast with CartScreen and FavoritesScreen which use LaunchedEffect(Unit)
            // to trigger load() because their ViewModels do NOT have an init block.

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
                        PullToRefreshBox(
                            isRefreshing = isLoad,
                            onRefresh = {
                                productsViewModel.load()
                            }) {
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
}
