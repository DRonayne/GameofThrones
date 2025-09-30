package com.darach.gameofthrones.feature.comparison.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.util.RomanNumeralConverter

/**
 * Character card with selection mode support.
 * Shows selection indicator when selected and allows toggling selection state.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectableCharacterCard(
    character: Character,
    isSelected: Boolean,
    isSelectionEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardAlpha by animateFloatAsState(
        targetValue = if (isSelectionEnabled && !isSelected) 0.6f else 1f,
        label = "card_alpha"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                CharacterCardHeader(character = character)

                CharacterCardSeasonBadges(seasons = character.tvSeriesSeasons)

                CharacterCardAlias(alias = character.aliases.firstOrNull())
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun CharacterCardHeader(character: Character, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CharacterInfo(character = character, modifier = Modifier.weight(1f))

        if (character.isDead) {
            DeathIndicator()
        }
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
                text = "Also known as: $alias",
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
    Text(
        text = "\u271D",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
    )
}
