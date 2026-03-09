package com.sbdor.onlinestoreclaude.features.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.sbdor.onlinestoreclaude.core.LocalAppContainer
import com.sbdor.onlinestoreclaude.features.order.controller.OrderState
import com.sbdor.onlinestoreclaude.features.order.controller.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBackClick: () -> Unit,
    onContinueShoppingClick: () -> Unit,
) {
    val container = LocalAppContainer.current
    val viewModel: OrderViewModel = viewModel(
        factory = OrderViewModel.factory(
            orderRepository = container.orderRepository,
            cartRepository = container.cartRepository,
        )
    )

    val state by viewModel.state.collectAsState()

    // CURRENT BEHAVIOR:
    // placeOrder() is a normal fun — it starts a coroutine internally (viewModelScope.launch)
    // and returns immediately without waiting. So println runs right after placeOrder() is called,
    // without waiting for the order to finish.
    //
    // Execution order:
    // 1. placeOrder() is called — internally fires viewModelScope.launch and returns immediately
    // 2. println("test message") runs right away
    // 3. the order completes in the background, state updates, UI recomposes
    LaunchedEffect(Unit) {
        viewModel.placeOrder()
        println("test message")  // runs immediately — does NOT wait for order to complete
    }

    // ---------------------------------------------------------------------------
    // ALTERNATIVE 1 — if placeOrder() were a suspend fun (no launch inside ViewModel):
    // ---------------------------------------------------------------------------
    // LaunchedEffect runs sequentially like Dart's await — each line waits for the previous.
    // So println would NOT run until placeOrder() fully completes.
    //
    // LaunchedEffect(Unit) {
    //     viewModel.placeOrder()   // suspend fun — LaunchedEffect awaits it automatically
    //     println("test message")  // runs ONLY after order is fully placed
    // }
    //
    // ---------------------------------------------------------------------------
    // ALTERNATIVE 2 — if placeOrder() were a suspend fun but we use launch inside LaunchedEffect:
    // ---------------------------------------------------------------------------
    // launch{} fires and forgets — it does NOT block the outer coroutine.
    // So println runs first, and placeOrder runs in the background simultaneously.
    //
    // LaunchedEffect(Unit) {
    //     launch {
    //         viewModel.placeOrder()   // suspend fun — runs in background, not awaited
    //     }
    //     println("test message")      // runs IMMEDIATELY — does not wait for placeOrder
    // }
    // ---------------------------------------------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Summary") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        when (val currentState = state) {
            is OrderState.Initial, is OrderState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Placing your order...")
                    }
                }
            }

            is OrderState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBackClick) {
                            Text("Go Back")
                        }
                    }
                }
            }

            is OrderState.Completed -> {
                val order = currentState.order

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                ) {
                    // Confirmation banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Order Confirmed!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                text = "Order #${order.id}  •  ${order.status.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // weight(1f) = takes remaining space, pushing the total/button to the bottom
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(order.items) { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "${item.product.name} × ${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = "$${item.totalPrice}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "$${order.totalPrice}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onContinueShoppingClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Continue Shopping")
                    }
                }
            }
        }
    }
}
