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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sbdor.onlinestoreclaude.features.favorites.controller.FavoritesState
import com.sbdor.onlinestoreclaude.features.favorites.controller.FavoritesViewModel
import com.sbdor.onlinestoreclaude.features.products.components.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val state by favoritesViewModel.state.collectAsState()

    // LaunchedEffect = runs a side effect when the composable enters the tree.
    // Unit as the key means it runs only once (like initState in Flutter).
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
                    Box(modifier = Modifier
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