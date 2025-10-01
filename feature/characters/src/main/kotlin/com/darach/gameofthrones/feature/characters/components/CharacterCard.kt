package com.darach.gameofthrones.feature.characters.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.domain.util.RomanNumeralConverter
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.core.ui.component.PortraitImage
import com.darach.gameofthrones.core.ui.test.TestTags
import com.darach.gameofthrones.core.ui.transition.SharedTransitionData
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CharacterGridCard(
    character: Character,
    onFavoriteClick: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "Card press scale"
    )

    Card(
        onClick = {
            performHaptic()
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .testTag(TestTags.CHARACTER_CARD),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        GridCardContent(
            character = character,
            onFavoriteClick = onFavoriteClick,
            sharedTransitionData = sharedTransitionData
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun GridCardContent(
    character: Character,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GridCardPortrait(
            character = character,
            onFavoriteClick = onFavoriteClick,
            sharedTransitionData = sharedTransitionData
        )

        Spacer(modifier = Modifier.height(8.dp))

        GridCardDetails(character = character)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun GridCardPortrait(
    character: Character,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
    ) {
        val imageModifier = if (sharedTransitionData != null) {
            with(sharedTransitionData.sharedTransitionScope) {
                Modifier
                    .fillMaxWidth()
                    .sharedElement(
                        rememberSharedContentState(key = "character-image-${character.id}"),
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
            Modifier.fillMaxWidth()
        }

        PortraitImage(
            imageUrl = character.characterImageUrl,
            contentDescription = character.name,
            modifier = imageModifier
        )

        IconButton(
            onClick = {
                performHaptic()
                onFavoriteClick(character.id)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(40.dp)
        ) {
            GridCardFavoriteIcon(isFavorite = character.isFavorite)
        }

        if (character.isDead) {
            GridCardDeathBadge(
                deathDate = character.died,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        } else {
            GridCardAliveBadge(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun GridCardDetails(character: Character, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = character.name.ifBlank { "Unknown" },
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (character.culture.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = character.culture,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (character.tvSeriesSeasons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            GridCardSeasonBadges(seasons = character.tvSeriesSeasons)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GridCardSeasonBadges(seasons: List<Int>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        seasons.sorted().take(8).forEach { season ->
            Surface(
                shape = AssistChipDefaults.shape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .semantics {
                        contentDescription = "Season $season"
                    }
            ) {
                Text(
                    text = RomanNumeralConverter.toRomanNumeral(season),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun GridCardFavoriteIcon(isFavorite: Boolean, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        shape = MaterialTheme.shapes.small,
        modifier = modifier.scale(scale.value)
    ) {
        Icon(
            imageVector = if (isFavorite) {
                Icons.Filled.Favorite
            } else {
                Icons.Outlined.FavoriteBorder
            },
            contentDescription = if (isFavorite) {
                "Remove from favorites"
            } else {
                "Add to favorites"
            },
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .padding(4.dp)
                .size(20.dp)
        )
    }
}

@Composable
private fun GridCardDeathBadge(deathDate: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = deathDate.ifBlank { "Deceased" },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GridCardAliveBadge(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = "Alive",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CharacterCard(
    character: Character,
    onFavoriteClick: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    Card(
        onClick = {
            performHaptic()
            onClick()
        },
        modifier = modifier
            .fillMaxWidth()
            .testTag(TestTags.CHARACTER_CARD),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val imageModifier = if (sharedTransitionData != null) {
                with(sharedTransitionData.sharedTransitionScope) {
                    Modifier
                        .width(80.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "character-image-${character.id}"),
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
                Modifier.width(80.dp)
            }

            PortraitImage(
                imageUrl = character.characterImageUrl,
                contentDescription = character.name,
                modifier = imageModifier
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
private fun FavoriteButton(
    character: Character,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val performHaptic = com.darach.gameofthrones.core.ui.haptics.rememberHapticFeedback()
    val scale = remember { Animatable(1f) }

    val onClick = remember(character.id, onFavoriteClick, performHaptic) {
        {
            performHaptic()
            onFavoriteClick(character.id)
        }
    }

    LaunchedEffect(character.isFavorite) {
        if (character.isFavorite) {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(48.dp)
            .scale(scale.value)
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
    Surface(
        shape = AssistChipDefaults.shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.semantics {
            contentDescription = "Season $season"
        }
    ) {
        Text(
            text = RomanNumeralConverter.toRomanNumeral(season),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DeathIndicator(modifier: Modifier = Modifier) {
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            alpha.animateTo(
                targetValue = 0.5f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessVeryLow
                )
            )
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessVeryLow
                )
            )
            delay(2000)
        }
    }

    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "Deceased",
        tint = MaterialTheme.colorScheme.error,
        modifier = modifier.graphicsLayer(alpha = alpha.value)
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

// Grid Card Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Grid Card - Default",
    showBackground = true,
    widthDp = 180
)
@Composable
private fun CharacterGridCardPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharacterGridCard(
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
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Grid Card - Favorite",
    showBackground = true,
    widthDp = 180
)
@Composable
private fun CharacterGridCardFavoritePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharacterGridCard(
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
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Grid Card - Deceased",
    showBackground = true,
    widthDp = 180
)
@Composable
private fun CharacterGridCardDeceasedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharacterGridCard(
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
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Grid Card - Long Name",
    showBackground = true,
    widthDp = 180
)
@Composable
private fun CharacterGridCardLongNamePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharacterGridCard(
            character = Character(
                id = "4",
                name = "Daenerys Stormborn of House Targaryen",
                gender = "Female",
                culture = "Valyrian",
                born = "284 AC",
                died = "",
                titles = listOf("Queen of the Andals"),
                aliases = listOf("Khaleesi", "Mother of Dragons"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = emptyList(),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1", "Season 2", "Season 3"),
                tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
                playedBy = listOf("Emilia Clarke"),
                isFavorite = false
            ),
            onFavoriteClick = {},
            onClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Grid Card - Few Seasons",
    showBackground = true,
    widthDp = 180
)
@Composable
private fun CharacterGridCardFewSeasonsPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharacterGridCard(
            character = Character(
                id = "5",
                name = "Oberyn Martell",
                gender = "Male",
                culture = "Dornish",
                born = "258 AC",
                died = "300 AC",
                titles = listOf("Prince of Dorne"),
                aliases = listOf("The Red Viper"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = emptyList(),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 4"),
                tvSeriesSeasons = listOf(4),
                playedBy = listOf("Pedro Pascal"),
                isFavorite = true
            ),
            onFavoriteClick = {},
            onClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Grid Card - Dark Mode",
    showBackground = true,
    widthDp = 180,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CharacterGridCardDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        CharacterGridCard(
            character = Character(
                id = "6",
                name = "Tyrion Lannister",
                gender = "Male",
                culture = "Westermen",
                born = "273 AC",
                died = "",
                titles = listOf("Hand of the Queen"),
                aliases = listOf("The Imp", "Halfman"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = emptyList(),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = listOf("Season 1", "Season 2", "Season 3"),
                tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
                playedBy = listOf("Peter Dinklage"),
                isFavorite = true
            ),
            onFavoriteClick = {},
            onClick = {}
        )
    }
}
