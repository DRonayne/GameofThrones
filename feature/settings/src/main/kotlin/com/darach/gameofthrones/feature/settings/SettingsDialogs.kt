package com.darach.gameofthrones.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme

@Composable
internal fun ClearHistoryDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    ConfirmationDialog(
        title = stringResource(
            com.darach.gameofthrones.core.ui.R.string.clear_search_history_dialog_title
        ),
        message = stringResource(
            com.darach.gameofthrones.core.ui.R.string.clear_search_history_dialog_message
        ),
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
        title = stringResource(
            com.darach.gameofthrones.core.ui.R.string.clear_all_data_dialog_title
        ),
        message = stringResource(
            com.darach.gameofthrones.core.ui.R.string.clear_all_data_dialog_message
        ),
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
                    text = stringResource(com.darach.gameofthrones.core.ui.R.string.confirm),
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
                Text(stringResource(com.darach.gameofthrones.core.ui.R.string.cancel))
            }
        }
    )
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Clear History Dialog",
    showBackground = true
)
@Composable
private fun ClearHistoryDialogPreview() {
    GameOfThronesTheme {
        ClearHistoryDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Clear All Data Dialog",
    showBackground = true
)
@Composable
private fun ClearAllDataDialogPreview() {
    GameOfThronesTheme {
        ClearAllDataDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Confirmation Dialog - Non-Destructive",
    showBackground = true
)
@Composable
private fun ConfirmationDialogPreview() {
    GameOfThronesTheme {
        ConfirmationDialog(
            title = "Sync Data?",
            message = "This will refresh all data from the server.",
            onConfirm = {},
            onDismiss = {},
            destructive = false
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Confirmation Dialog - Destructive",
    showBackground = true
)
@Composable
private fun ConfirmationDialogDestructivePreview() {
    GameOfThronesTheme {
        ConfirmationDialog(
            title = "Delete Everything?",
            message = "This action cannot be undone. All your data will be permanently deleted.",
            onConfirm = {},
            onDismiss = {},
            destructive = true
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Clear History Dialog - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ClearHistoryDialogDarkPreview() {
    GameOfThronesTheme {
        ClearHistoryDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Clear All Data Dialog - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun ClearAllDataDialogTabletPreview() {
    GameOfThronesTheme {
        ClearAllDataDialog(
            onConfirm = {},
            onDismiss = {}
        )
    }
}
