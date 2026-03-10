package com.sbdor.onlinestoreclaude.features.favorites.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sbdor.onlinestoreclaude.di.LocalDependenciesScope
import com.sbdor.onlinestoreclaude.features.favorites.controller.FavoritesState
import com.sbdor.onlinestoreclaude.features.favorites.controller.FavoritesViewModel
import com.sbdor.onlinestoreclaude.features.products.components.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
) {
    val container = LocalDependenciesScope.current
    val favoritesViewModel: FavoritesViewModel =
        viewModel(factory = FavoritesViewModel.factory(container.favoritesRepository))
    val state by favoritesViewModel.state.collectAsState()

    // LaunchedEffect(Unit) here serves a DIFFERENT purpose than in CartScreen.
    // In CartScreen, LaunchedEffect calls a normal fun — it just ensures it runs once.
    // HERE, LaunchedEffect calls favoritesViewModel.load() which is a 'suspend fun'.
    // LaunchedEffect provides the coroutine context required to call a suspend function.
    // Without LaunchedEffect (or another coroutine wrapper), calling load() here would
    // be a compile error: "suspend function can only be called from a coroutine".
    //
    // This is exactly the trade-off of marking ViewModel functions as suspend:
    // the caller must always be inside a coroutine to invoke them.
    //
    // RECOMMENDED ALTERNATIVE: change load() in FavoritesViewModel to a normal fun
    // with viewModelScope.launch inside (see comment in FavoritesViewModel.kt).
    // Then this LaunchedEffect would just be triggering a normal fun, same as CartScreen.
    LaunchedEffect(Unit) {
        favoritesViewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        when (val currentState = state) {
            is FavoritesState.Completed ->
                if (currentState.favorites.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No favorites")
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(currentState.favorites, key = { it.product.id }) { favorite ->
                                ProductCard(
                                    product = favorite.product,
                                    onClick = { },
                                )
                            }
                        }
                    }
                }


            FavoritesState.Error -> Box(modifier = Modifier.fillMaxSize()) {
                Text("Error state")
            }

            FavoritesState.InProgress -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            FavoritesState.Initial -> Box(modifier = Modifier.fillMaxSize()) {
                Text("InitialState")
            }
        }
    }
}