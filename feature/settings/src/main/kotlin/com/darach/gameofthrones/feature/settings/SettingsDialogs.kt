package com.darach.gameofthrones.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
internal fun ClearHistoryDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ConfirmationDialog(
        title = "Clear Search History?",
        message = "This will remove all your search history.",
        onConfirm = {
            onConfirm()
            onDismiss()
        },
        onDismiss = onDismiss
    )
}

@Composable
internal fun ClearAllDataDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ConfirmationDialog(
        title = "Clear All Data?",
        message = "This will reset all preferences to defaults and clear all cached data. " +
            "This action cannot be undone.",
        onConfirm = {
            onConfirm()
            onDismiss()
        },
        onDismiss = onDismiss,
        destructive = true
    )
}

@Composable
internal fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Confirm",
                    color = if (destructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
