package com.sbdor.onlinestoreclaude.features.order_history.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sbdor.onlinestoreclaude.core.SharedPreferencesManager
import com.sbdor.onlinestoreclaude.features.order_history.components.components.OrderHistoryCard
import com.sbdor.onlinestoreclaude.features.order_history.controller.OrderHistoryControllerFactory
import com.sbdor.onlinestoreclaude.features.order_history.controller.OrderHistoryState
import com.sbdor.onlinestoreclaude.features.order_history.controller.OrderHistoryViewModel
import com.sbdor.onlinestoreclaude.features.order_history.data.OrderHistoryImpl
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onBackClick: () -> Unit,

) {
    // ---------------------------------------------------------------------------
    // hiltViewModel<VM, Factory> { factory -> factory.create(...) } is the
    // Compose-side usage of @AssistedInject. Hilt provides the factory instance,
    // we call factory.create() passing our runtime-resolved repository.
    //
    // This is equivalent to:
    //   ChangeNotifierProvider(
    //     create: (_) => OrderHistoryController(repository: repository),
    //     child: ...,
    //   )
    // in Flutter.
    val context = LocalContext.current
    val sharedPreferencesManager = remember {
        SharedPreferencesManager(context)
    }

    val viewModel: OrderHistoryViewModel = hiltViewModel<OrderHistoryViewModel, OrderHistoryControllerFactory> { factory ->
        factory.create(OrderHistoryImpl(sharedPreferencesManager))
    }


    val state by viewModel.state.collectAsState()

    // LaunchedEffect(Unit) — load order history once when screen enters composition.
    // viewModel.load() is a normal fun (not suspend) so LaunchedEffect here is used
    // purely to run it once, not to provide coroutine context.
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        when (val currentState = state) {
            is OrderHistoryState.Initial,
            is OrderHistoryState.InProgress -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is OrderHistoryState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = currentState.error ?: "Something went wrong",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            is OrderHistoryState.Completed -> {
                if (currentState.orderHistory.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No orders yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = currentState.orderHistory,
                            // key = stable ID prevents unnecessary recompositions
                            // when the list changes (same as Flutter's key: ValueKey(order.id))
                        ) { order ->
                            OrderHistoryCard(order = order)
                        }
                    }
                }
            }
        }
    }
}
