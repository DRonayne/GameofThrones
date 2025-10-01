package com.darach.gameofthrones.feature.characters.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.util.RomanNumeralConverter
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.core.ui.component.PortraitImage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterCard(
    character: Character,
    onFavoriteClick: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
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
                modifier = Modifier
                    .weight(1f)
            ) {
                CharacterCardHeader(
                    character = character,
                    onFavoriteClick = onFavoriteClick
                )

                CharacterCardSeasonBadges(seasons = character.tvSeriesSeasons)

                CharacterCardAlias(alias = character.aliases.firstOrNull())
            }
        }
    }
}

@Composable
private fun CharacterCardHeader(
    character: Character,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CharacterInfo(character = character, modifier = Modifier.weight(1f))

        CharacterActions(character = character, onFavoriteClick = onFavoriteClick)
    }
}

@Composable
private fun CharacterInfo(character: Character, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = character.name.ifBlank { "Unknown" },
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (character.culture.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = character.culture,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (character.gender.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = character.gender,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CharacterActions(
    character: Character,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (character.isDead) {
            DeathIndicator()
            Spacer(modifier = Modifier.width(8.dp))
        }

        FavoriteButton(
            character = character,
            onFavoriteClick = onFavoriteClick
        )
    }
}

@Composable
private fun FavoriteButton(character: Character, onFavoriteClick: (String) -> Unit) {
    IconButton(
        onClick = { onFavoriteClick(character.id) },
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = if (character.isFavorite) {
                Icons.Filled.Favorite
            } else {
                Icons.Outlined.FavoriteBorder
            },
            contentDescription = if (character.isFavorite) {
                "Remove from favorites"
            } else {
                "Add to favorites"
            },
            tint = if (character.isFavorite) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CharacterCardSeasonBadges(seasons: List<Int>, modifier: Modifier = Modifier) {
    if (seasons.isNotEmpty()) {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                seasons.sorted().forEach { season ->
                    SeasonBadge(season = season)
                }
            }
        }
    }
}

@Composable
private fun CharacterCardAlias(alias: String?, modifier: Modifier = Modifier) {
    if (alias?.isNotBlank() == true) {
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = alias,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
private fun DeathIndicator(modifier: Modifier = Modifier) {
    androidx.compose.material3.Icon(
        imageVector = androidx.compose.material.icons.Icons.Default.Close,
        contentDescription = "Deceased",
        tint = MaterialTheme.colorScheme.error,
        modifier = modifier
    )
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Card - Default",
    showBackground = true
)
@Composable
private fun CharacterCardPreview() {
    CharacterCard(
        character = Character(
            id = "1",
            name = "Jon Snow",
            gender = "Male",
            culture = "Northmen",
            born = "283 AC",
            died = "",
            titles = listOf("King in the North"),
            aliases = listOf("Lord Snow", "The White Wolf"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1", "Season 2", "Season 3"),
            tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
            playedBy = listOf("Kit Harington"),
            isFavorite = false
        ),
        onFavoriteClick = {},
        onClick = {}
    )
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Card - Favorite",
    showBackground = true
)
@Composable
private fun CharacterCardFavoritePreview() {
    CharacterCard(
        character = Character(
            id = "2",
            name = "Arya Stark",
            gender = "Female",
            culture = "Northmen",
            born = "289 AC",
            died = "",
            titles = emptyList(),
            aliases = listOf("No One", "Arry"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1", "Season 2"),
            tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
            playedBy = listOf("Maisie Williams"),
            isFavorite = true
        ),
        onFavoriteClick = {},
        onClick = {}
    )
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Card - Deceased",
    showBackground = true
)
@Composable
private fun CharacterCardDeceasedPreview() {
    CharacterCard(
        character = Character(
            id = "3",
            name = "Ned Stark",
            gender = "Male",
            culture = "Northmen",
            born = "263 AC",
            died = "299 AC",
            titles = listOf("Lord of Winterfell", "Warden of the North"),
            aliases = listOf("The Quiet Wolf"),
            father = "",
            mother = "",
            spouse = "",
            allegiances = emptyList(),
            books = emptyList(),
            povBooks = emptyList(),
            tvSeries = listOf("Season 1"),
            tvSeriesSeasons = listOf(1),
            playedBy = listOf("Sean Bean"),
            isFavorite = false
        ),
        onFavoriteClick = {},
        onClick = {}
    )
}
