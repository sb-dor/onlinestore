package com.sbdor.onlinestoreclaude.features.order_history.components

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sbdor.onlinestoreclaude.core.viewModelFactory
import com.sbdor.onlinestoreclaude.di.LocalDependenciesScope
import com.sbdor.onlinestoreclaude.features.order_history.components.components.OrderHistoryCard
import com.sbdor.onlinestoreclaude.features.order_history.controller.OrderHistoryState
import com.sbdor.onlinestoreclaude.features.order_history.controller.OrderHistoryViewModel
import com.sbdor.onlinestoreclaude.features.order_history_deletion.components.DeleteOrderDialog
import com.sbdor.onlinestoreclaude.features.order_history_deletion.controller.OrderHistoryDeletionState
import com.sbdor.onlinestoreclaude.features.order_history_deletion.controller.OrderHistoryDeletionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onBackClick: () -> Unit,
) {
    val container = LocalDependenciesScope.current

    val viewModel: OrderHistoryViewModel = viewModel(
        factory = viewModelFactory {
            OrderHistoryViewModel(container.orderHistoryRepository)
        }
    )

    val state by viewModel.state.collectAsState()

    // Load history once when screen appears
    LaunchedEffect(Unit) {
        viewModel.load()
    }

    /// I do not know why, but it's the only way
    // Which order the user has tapped "delete" on.
    // null = no dialog shown. Non-null = dialog is open for that order ID.
    var orderIdPendingDeletion by remember { mutableStateOf<Int?>(null) }
    // Show the confirmation dialog only when an order ID is pending deletion
    orderIdPendingDeletion?.let { orderId ->
        DeleteOrderDialog(
            orderId = orderId,
            onSuccess = {
                viewModel.load()
                orderIdPendingDeletion = null  // close dialog
            },
            onDismiss = {
                orderIdPendingDeletion = null  // close dialog without deleting
            },
        )
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
                        ) { order ->
                            OrderHistoryCard(
                                order = order,
                                // tapping the delete icon sets this order as pending
                                // which triggers the dialog to appear
                                onDeleteClick = { orderIdPendingDeletion = order.id },
                            )
                        }
                    }
                }
            }
        }
    }
}
