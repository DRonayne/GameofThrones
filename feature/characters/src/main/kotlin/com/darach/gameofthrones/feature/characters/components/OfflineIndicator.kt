package com.darach.gameofthrones.feature.characters.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * Floating offline indicator that appears when network is unavailable.
 * Automatically hides when online and animates in/out smoothly with spring physics.
 */
@Composable
fun OfflineIndicator(isOffline: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isOffline,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = stringResource(
                        com.darach.gameofthrones.core.ui.R.string.offline
                    ),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(com.darach.gameofthrones.core.ui.R.string.offline),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

// Previews
@Composable
@androidx.compose.ui.tooling.preview.Preview(
    name = "Offline Indicator - Light",
    showBackground = true
)
private fun OfflineIndicatorLightPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            OfflineIndicator(isOffline = true)
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(
    name = "Offline Indicator - Dark",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
private fun OfflineIndicatorDarkPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            OfflineIndicator(isOffline = true)
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(
    name = "Offline Indicator - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
private fun OfflineIndicatorTabletPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            OfflineIndicator(
                isOffline = true,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp)
            )
        }
    }
}
