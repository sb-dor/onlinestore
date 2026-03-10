package com.sbdor.onlinestoreclaude.features.order_history_deletion.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sbdor.onlinestoreclaude.di.LocalDependenciesScope
import com.sbdor.onlinestoreclaude.features.order_history_deletion.controller.OrderHistoryDeletionState
import com.sbdor.onlinestoreclaude.features.order_history_deletion.controller.OrderHistoryDeletionViewModel

// ---------------------------------------------------------------------------
// DeleteOrderDialog — confirmation popup before deleting an order
// ---------------------------------------------------------------------------
// AlertDialog is Compose's built-in popup component.
// It is a "dumb" composable — it does not manage its own visibility.
// The CALLER decides when to show/hide it using a Boolean state variable.
//
// Pattern:
//   var showDialog by remember { mutableStateOf(false) }
//
//   Button(onClick = { showDialog = true }) { Text("Delete") }
//
//   if (showDialog) {
//       DeleteOrderDialog(
//           orderId = order.id,
//           onConfirm = { viewModel.delete(order.id); showDialog = false },
//           onDismiss = { showDialog = false },
//       )
//   }
// ---------------------------------------------------------------------------
@Composable
fun DeleteOrderDialog(
    orderId: Int,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
) {

    val localDependenciesScope = LocalDependenciesScope.current

    val deletionViewModel: OrderHistoryDeletionViewModel =
        viewModel(
            factory = OrderHistoryDeletionViewModel.factory(
                localDependenciesScope.orderHistoryDeletionRepository
            )
        )

    val deletionViewModelState = deletionViewModel.state.collectAsState().value

    // true while the request is in-flight or just completed (before onSuccess fires)
    val isBusy = deletionViewModelState is OrderHistoryDeletionState.InProgress

    LaunchedEffect(deletionViewModelState) {
        if (deletionViewModelState is OrderHistoryDeletionState.Completed) {
            onSuccess()
        }
    }

    AlertDialog(
        // block back-press / outside-tap while deletion is in progress or completed
        onDismissRequest = { if (!isBusy) onDismiss() },

        title = { Text("Delete Order") },

        text = { Text("Are you sure you want to delete Order #$orderId? This cannot be undone.") },

        confirmButton = {
            TextButton(
                onClick = { deletionViewModel.delete(orderId) },
                // disable the button once tapped so it can't be triggered twice
                enabled = !isBusy,
            ) {
                // swap label for a spinner while the deletion is running
                if (isBusy) {
                    CircularProgressIndicator()
                } else {
                    Text("Delete")
                }
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isBusy,
            ) {
                Text("Cancel")
            }
        },
    )
}
