package com.sbdor.onlinestoreclaude.features.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sbdor.onlinestoreclaude.features.cart.controller.CartState
import com.sbdor.onlinestoreclaude.features.cart.controller.CartViewModel
import com.sbdor.onlinestoreclaude.features.cart.components.components.CartItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // LaunchedEffect(Unit) = runs once when the screen enters composition (like initState in Flutter).
    // Even though viewModel.load() is a normal fun (not suspend), LaunchedEffect is still used here
    // to guarantee it runs only ONCE and not on every recomposition.
    //
    // ALTERNATIVE: call load() inside CartViewModel's init{} block instead.
    // Then this LaunchedEffect would not be needed at all — the ViewModel loads automatically:
    //
    // init { load() }   // in CartViewModel
    //
    // Both approaches are valid. init{} is cleaner when the screen should always start loaded.
    // LaunchedEffect is better when you want the screen to control WHEN loading starts
    // (e.g. load only when the screen is visible, not on ViewModel creation).
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            if (state is CartState.Completed) {
                val completedState = state as CartState.Completed
                Surface(tonalElevation = 3.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Total", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "$${completedState.total}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Button(onClick = onCheckoutClick) {
                            Text("Checkout")
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        when (val currentState = state) {
            is CartState.Initial -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is CartState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Your cart is empty", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onBackClick) {
                            Text("Continue Shopping")
                        }
                    }
                }
            }

            is CartState.Completed -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(currentState.items, key = { it.product.id }) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onIncrease = { viewModel.increaseQuantity(cartItem.product.id) },
                            onDecrease = { viewModel.decreaseQuantity(cartItem.product.id) },
                            onRemove = { viewModel.removeItem(cartItem.product.id) },
                        )
                    }
                }
            }
        }
    }
}
