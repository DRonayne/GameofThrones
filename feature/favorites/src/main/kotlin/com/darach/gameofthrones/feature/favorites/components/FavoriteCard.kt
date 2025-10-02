package com.darach.gameofthrones.feature.favorites.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import com.darach.gameofthrones.core.ui.transition.SharedTransitionData

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FavoriteCard(
    character: Character,
    isSelected: Boolean,
    callbacks: FavoriteCardCallbacks,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
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
            isSelected = isSelected,
            sharedTransitionData = sharedTransitionData
        )

        Spacer(modifier = Modifier.height(8.dp))

        FavoriteCardName(
            name = character.name,
            sharedTransitionData = sharedTransitionData,
            characterId = character.id
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FavoriteCardImage(
    character: Character,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
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

    Box(modifier = modifier.size(96.dp)) {
        FavoriteCardImageContainer(
            character = character,
            borderWidth = borderWidth,
            borderColor = borderColor,
            elevation = elevation,
            sharedTransitionData = sharedTransitionData
        )

        SelectionCheckmark(
            isSelected = isSelected,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FavoriteCardImageContainer(
    character: Character,
    borderWidth: androidx.compose.ui.unit.Dp,
    borderColor: androidx.compose.ui.graphics.Color,
    elevation: androidx.compose.ui.unit.Dp,
    sharedTransitionData: SharedTransitionData?
) {
    val containerModifier = createContainerModifier(
        borderWidth = borderWidth,
        borderColor = borderColor,
        elevation = elevation,
        characterId = character.id,
        sharedTransitionData = sharedTransitionData
    )

    Box(modifier = containerModifier) {
        val portraitModifier = createPortraitModifier(
            characterId = character.id,
            sharedTransitionData = sharedTransitionData
        )

        PortraitImage(
            imageUrl = character.characterImageUrl,
            contentDescription = character.name,
            modifier = portraitModifier
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun createContainerModifier(
    borderWidth: androidx.compose.ui.unit.Dp,
    borderColor: androidx.compose.ui.graphics.Color,
    elevation: androidx.compose.ui.unit.Dp,
    characterId: String,
    sharedTransitionData: SharedTransitionData?
): Modifier {
    val baseModifier = Modifier
        .size(96.dp)
        .shadow(elevation = elevation, shape = CircleShape)
        .border(width = borderWidth, color = borderColor, shape = CircleShape)
        .padding(2.dp)

    return if (sharedTransitionData != null) {
        with(sharedTransitionData.sharedTransitionScope) {
            baseModifier.sharedBounds(
                rememberSharedContentState(key = "character-container-$characterId"),
                animatedVisibilityScope = sharedTransitionData.animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                }
            )
        }
    } else {
        baseModifier
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun createPortraitModifier(
    characterId: String,
    sharedTransitionData: SharedTransitionData?
): Modifier = if (sharedTransitionData != null) {
    with(sharedTransitionData.sharedTransitionScope) {
        Modifier
            .size(92.dp)
            .sharedBounds(
                rememberSharedContentState(key = "character-image-$characterId"),
                animatedVisibilityScope = sharedTransitionData.animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                },
                enter = fadeIn(),
                exit = fadeOut(),
                resizeMode =
                androidx.compose.animation.SharedTransitionScope.ResizeMode.ScaleToBounds()
            )
            .clip(CircleShape)
    }
} else {
    Modifier.size(92.dp).clip(CircleShape)
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FavoriteCardName(
    name: String,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null,
    characterId: String = ""
) {
    val nameModifier = if (sharedTransitionData != null) {
        with(sharedTransitionData.sharedTransitionScope) {
            modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .sharedElement(
                    rememberSharedContentState(key = "character-name-$characterId"),
                    animatedVisibilityScope = sharedTransitionData.animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    }
                )
        }
    } else {
        modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    }

    Text(
        text = name.ifBlank { "Unknown" },
        style = MaterialTheme.typography.bodySmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = nameModifier
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
