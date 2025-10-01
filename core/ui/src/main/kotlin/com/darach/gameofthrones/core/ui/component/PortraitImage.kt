package com.darach.gameofthrones.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage

/**
 * A portrait image with 3:4 aspect ratio for displaying character and actor images
 * Uses Coil 3 with optimized caching and placeholder/error states
 *
 * @param imageUrl The URL of the image to display. If null, a placeholder will be shown
 * @param contentDescription Content description for accessibility
 * @param modifier Optional modifier for the image
 */
@Composable
fun PortraitImage(imageUrl: String?, contentDescription: String?, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(),
                loading = { ImagePlaceholder() },
                error = { ErrorPlaceholder() }
            )
        } else {
            ImagePlaceholder()
        }
    }
}

@Composable
private fun ImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

@Composable
private fun ErrorPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.BrokenImage,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

// Previews
@Composable
@androidx.compose.ui.tooling.preview.Preview(
    name = "Portrait Image - Light",
    showBackground = true
)
private fun PortraitImageLightPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PortraitImage(
                imageUrl = null,
                contentDescription = "Character portrait"
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(
    name = "Portrait Image - Dark",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
private fun PortraitImageDarkPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            PortraitImage(
                imageUrl = null,
                contentDescription = "Character portrait"
            )
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(
    name = "Portrait Image - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
private fun PortraitImageTabletPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PortraitImage(
                imageUrl = null,
                contentDescription = "Character 1",
                modifier = Modifier.width(200.dp)
            )
            PortraitImage(
                imageUrl = null,
                contentDescription = "Character 2",
                modifier = Modifier.width(200.dp)
            )
            PortraitImage(
                imageUrl = null,
                contentDescription = "Character 3",
                modifier = Modifier.width(200.dp)
            )
        }
    }
}
