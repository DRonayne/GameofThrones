package com.darach.gameofthrones.feature.comparison.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.ui.test.TestTags

/**
 * State for the comparison selection bar.
 */
data class ComparisonSelectionBarState(
    val selectionCount: Int,
    val maxSelection: Int,
    val canCompare: Boolean
)

/**
 * Callbacks for the comparison selection bar.
 */
data class ComparisonSelectionBarCallbacks(
    val onClearClick: () -> Unit,
    val onCompareClick: () -> Unit,
    val onCloseClick: () -> Unit
)

/**
 * Bottom bar shown during selection mode.
 * Displays selection count, clear button, and compare button.
 */
@Composable
fun ComparisonSelectionBar(
    state: ComparisonSelectionBarState,
    callbacks: ComparisonSelectionBarCallbacks,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.selectionCount > 0,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = MaterialTheme.shapes.extraLarge.copy(
                bottomStart = androidx.compose.foundation.shape.CornerSize(0.dp),
                bottomEnd = androidx.compose.foundation.shape.CornerSize(0.dp)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelectionInfo(
                        selectionCount = state.selectionCount,
                        maxSelection = state.maxSelection
                    )

                    IconButton(onClick = callbacks.onCloseClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close selection mode"
                        )
                    }
                }

                SelectionActions(
                    canCompare = state.canCompare,
                    callbacks = callbacks
                )
            }
        }
    }
}

@Composable
private fun SelectionActions(
    canCompare: Boolean,
    callbacks: ComparisonSelectionBarCallbacks,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = callbacks.onClearClick,
            modifier = Modifier
                .weight(1f)
                .testTag(TestTags.CLEAR_SELECTION_BUTTON)
        ) {
            Text("Clear")
        }

        Button(
            onClick = callbacks.onCompareClick,
            enabled = canCompare,
            modifier = Modifier
                .weight(1f)
                .testTag(TestTags.COMPARE_BUTTON)
        ) {
            Text("Compare")
        }
    }
}

@Composable
private fun SelectionInfo(selectionCount: Int, maxSelection: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$selectionCount / $maxSelection selected",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (selectionCount >= maxSelection) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(Maximum)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Selection Bar - One Selected",
    showBackground = true
)
@Composable
private fun ComparisonSelectionBarOnePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionBar(
            state = ComparisonSelectionBarState(
                selectionCount = 1,
                maxSelection = 3,
                canCompare = false
            ),
            callbacks = ComparisonSelectionBarCallbacks(
                onClearClick = {},
                onCompareClick = {},
                onCloseClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Selection Bar - Two Selected (Can Compare)",
    showBackground = true
)
@Composable
private fun ComparisonSelectionBarTwoPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionBar(
            state = ComparisonSelectionBarState(
                selectionCount = 2,
                maxSelection = 3,
                canCompare = true
            ),
            callbacks = ComparisonSelectionBarCallbacks(
                onClearClick = {},
                onCompareClick = {},
                onCloseClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Selection Bar - Maximum Selected",
    showBackground = true
)
@Composable
private fun ComparisonSelectionBarMaxPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionBar(
            state = ComparisonSelectionBarState(
                selectionCount = 3,
                maxSelection = 3,
                canCompare = true
            ),
            callbacks = ComparisonSelectionBarCallbacks(
                onClearClick = {},
                onCompareClick = {},
                onCloseClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Selection Bar - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ComparisonSelectionBarDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionBar(
            state = ComparisonSelectionBarState(
                selectionCount = 2,
                maxSelection = 3,
                canCompare = true
            ),
            callbacks = ComparisonSelectionBarCallbacks(
                onClearClick = {},
                onCompareClick = {},
                onCloseClick = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Selection Bar - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun ComparisonSelectionBarTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonSelectionBar(
            state = ComparisonSelectionBarState(
                selectionCount = 3,
                maxSelection = 3,
                canCompare = true
            ),
            callbacks = ComparisonSelectionBarCallbacks(
                onClearClick = {},
                onCompareClick = {},
                onCloseClick = {}
            )
        )
    }
}
