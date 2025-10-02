package com.darach.gameofthrones.feature.favorites.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.core.ui.component.PortraitImage
import com.darach.gameofthrones.core.ui.test.TestTags

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteCard(
    character: Character,
    isSelected: Boolean,
    callbacks: FavoriteCardCallbacks,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .testTag(TestTags.FAVORITE_CARD)
            .semantics(mergeDescendants = true) {
                contentDescription = if (isSelected) {
                    "${character.name}, selected"
                } else {
                    character.name
                }
                stateDescription = if (isSelected) "Selected" else "Not selected"
            }
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                callbacks.onToggleSelection()
            }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FavoriteCardImage(
            character = character,
            isSelected = isSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        FavoriteCardName(name = character.name)
    }
}

@Composable
private fun FavoriteCardImage(
    character: Character,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 1.5.dp,
        label = "border_width"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        label = "border_color"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 1.dp,
        label = "elevation"
    )

    Box(
        modifier = modifier
            .size(96.dp)
            .shadow(
                elevation = elevation,
                shape = CircleShape
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = CircleShape
            )
            .padding(2.dp)
    ) {
        PortraitImage(
            imageUrl = character.characterImageUrl,
            contentDescription = character.name,
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
        )

        SelectionCheckmark(
            isSelected = isSelected,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(1f)
        )
    }
}

@Composable
private fun SelectionCheckmark(isSelected: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isSelected,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Selected",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(28.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = CircleShape
                )
                .padding(2.dp)
        )
    }
}

@Composable
private fun FavoriteCardName(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name.ifBlank { "Unknown" },
        style = MaterialTheme.typography.bodySmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

// Sample data for previews
private val sampleJonSnow = Character(
    id = "583",
    name = "Jon Snow",
    gender = "Male",
    culture = "Northmen",
    born = "In 283 AC",
    died = "",
    titles = listOf("Lord Commander of the Night's Watch", "King in the North"),
    aliases = listOf("Lord Snow", "Ned Stark's Bastard", "The White Wolf"),
    father = "",
    mother = "",
    spouse = "",
    allegiances = listOf(),
    books = listOf(),
    povBooks = listOf(),
    tvSeries = listOf("Season 1", "Season 2", "Season 3", "Season 4", "Season 5", "Season 6"),
    tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
    playedBy = listOf("Kit Harington"),
    isFavorite = true,
    isDead = false
)

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Unselected",
    showBackground = true
)
@Composable
private fun FavoriteCardUnselectedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleJonSnow,
            isSelected = false,
            callbacks = FavoriteCardCallbacks(
                onToggleSelection = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Selected",
    showBackground = true
)
@Composable
private fun FavoriteCardSelectedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleJonSnow,
            isSelected = true,
            callbacks = FavoriteCardCallbacks(
                onToggleSelection = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Dark Mode Unselected",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun FavoriteCardDarkUnselectedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleJonSnow,
            isSelected = false,
            callbacks = FavoriteCardCallbacks(
                onToggleSelection = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Dark Mode Selected",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun FavoriteCardDarkSelectedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleJonSnow,
            isSelected = true,
            callbacks = FavoriteCardCallbacks(
                onToggleSelection = {}
            )
        )
    }
}
