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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sbdor.onlinestoreclaude.core.viewModelFactory
import com.sbdor.onlinestoreclaude.di.LocalDependenciesScope
import com.sbdor.onlinestoreclaude.features.cart.controller.CartState
import com.sbdor.onlinestoreclaude.features.cart.controller.CartViewModel
import com.sbdor.onlinestoreclaude.features.cart.components.components.CartItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
) {
    val container = LocalDependenciesScope.current
//    val viewModel: CartViewModel = viewModel(factory = CartViewModel.factory(container.cartRepository))
    val viewModel: CartViewModel = viewModel(
        factory = viewModelFactory {
            CartViewModel(container.cartRepository)
        }
    )
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
                // Column wraps both the list and the checkout bar so they
                // stack vertically and respect the Scaffold's paddingValues.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    // Checkout bar — pinned to the top of the Column
                    Surface(tonalElevation = 3.dp) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("Total", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = "$${currentState.total}",
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

                    LazyColumn(
                        modifier = Modifier.weight(1f),
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
}
