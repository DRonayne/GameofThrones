package com.darach.gameofthrones.feature.favorites.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.util.RomanNumeralConverter
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.core.ui.component.PortraitImage
import com.darach.gameofthrones.core.ui.test.TestTags

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteCard(
    character: Character,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    callbacks: FavoriteCardCallbacks,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(TestTags.FAVORITE_CARD)
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) {
                        callbacks.onToggleSelection()
                    } else {
                        callbacks.onCharacterClick()
                    }
                },
                onLongClick = {
                    if (!isSelectionMode) {
                        callbacks.onToggleSelection()
                    }
                }
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        FavoriteCardContent(
            character = character,
            isSelectionMode = isSelectionMode,
            isSelected = isSelected,
            callbacks = callbacks
        )
    }
}

@Composable
private fun FavoriteCardContent(
    character: Character,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    callbacks: FavoriteCardCallbacks,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PortraitImage(
                imageUrl = character.characterImageUrl,
                contentDescription = character.name,
                modifier = Modifier.width(80.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                FavoriteCardHeader(
                    character = character,
                    isSelectionMode = isSelectionMode,
                    onRemoveFavorite = callbacks.onRemoveFavorite
                )

                FavoriteCardSeasonBadges(seasons = character.tvSeriesSeasons)

                FavoriteCardCulture(culture = character.culture)
            }
        }

        if (isSelectionMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { callbacks.onToggleSelection() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun FavoriteCardHeader(
    character: Character,
    isSelectionMode: Boolean,
    onRemoveFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = character.name.ifBlank { "Unknown" },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (character.isDead) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "\u271D",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Deceased",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (!isSelectionMode) {
            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from favorites",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FavoriteCardSeasonBadges(seasons: List<Int>, modifier: Modifier = Modifier) {
    if (seasons.isNotEmpty()) {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                seasons.sorted().take(4).forEach { season ->
                    SeasonBadge(season = season)
                }
                if (seasons.size > 4) {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = "+${seasons.size - 4}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SeasonBadge(season: Int, modifier: Modifier = Modifier) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = RomanNumeralConverter.toRomanNumeral(season),
                style = MaterialTheme.typography.labelMedium
            )
        },
        modifier = modifier
    )
}

@Composable
private fun FavoriteCardCulture(culture: String, modifier: Modifier = Modifier) {
    if (culture.isNotBlank()) {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = culture,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
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

private val sampleAryaStark = Character(
    id = "148",
    name = "Arya Stark",
    gender = "Female",
    culture = "Northmen",
    born = "In 289 AC",
    died = "",
    titles = listOf("Princess"),
    aliases = listOf("Arya Horseface", "Arya Underfoot", "Cat of the Canals", "No One"),
    father = "",
    mother = "",
    spouse = "",
    allegiances = listOf(),
    books = listOf(),
    povBooks = listOf(),
    tvSeries = listOf("Season 1", "Season 2", "Season 3", "Season 4", "Season 5"),
    tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
    playedBy = listOf("Maisie Williams"),
    isFavorite = true,
    isDead = false
)

private val sampleNedStark = Character(
    id = "339",
    name = "Eddard Stark",
    gender = "Male",
    culture = "Northmen",
    born = "In 263 AC",
    died = "In 299 AC, at King's Landing",
    titles = listOf("Lord of Winterfell", "Warden of the North", "Hand of the King"),
    aliases = listOf("Ned", "The Ned", "The Quiet Wolf"),
    father = "",
    mother = "",
    spouse = "Catelyn Tully",
    allegiances = listOf(),
    books = listOf(),
    povBooks = listOf(),
    tvSeries = listOf("Season 1"),
    tvSeriesSeasons = listOf(1),
    playedBy = listOf("Sean Bean"),
    isFavorite = true,
    isDead = true
)

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Normal Mode Alive",
    showBackground = true
)
@Composable
private fun FavoriteCardNormalAlivePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleJonSnow,
            isSelectionMode = false,
            isSelected = false,
            callbacks = FavoriteCardCallbacks(
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Normal Mode Deceased",
    showBackground = true
)
@Composable
private fun FavoriteCardNormalDeceasedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleNedStark,
            isSelectionMode = false,
            isSelected = false,
            callbacks = FavoriteCardCallbacks(
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Selection Mode Unselected",
    showBackground = true
)
@Composable
private fun FavoriteCardSelectionUnselectedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleAryaStark,
            isSelectionMode = true,
            isSelected = false,
            callbacks = FavoriteCardCallbacks(
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Selection Mode Selected",
    showBackground = true
)
@Composable
private fun FavoriteCardSelectionSelectedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleJonSnow,
            isSelectionMode = true,
            isSelected = true,
            callbacks = FavoriteCardCallbacks(
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun FavoriteCardDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleNedStark,
            isSelectionMode = false,
            isSelected = false,
            callbacks = FavoriteCardCallbacks(
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Favorite Card - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun FavoriteCardTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        FavoriteCard(
            character = sampleAryaStark,
            isSelectionMode = false,
            isSelected = false,
            callbacks = FavoriteCardCallbacks(
                onCharacterClick = {},
                onToggleSelection = {},
                onRemoveFavorite = {}
            )
        )
    }
}
