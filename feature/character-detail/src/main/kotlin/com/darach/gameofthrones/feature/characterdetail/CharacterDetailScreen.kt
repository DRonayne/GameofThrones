package com.darach.gameofthrones.feature.characterdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.core.domain.model.Character
import com.darach.gameofthrones.core.domain.util.RomanNumeralConverter
import com.darach.gameofthrones.feature.characterdetail.ui.CharacterDetailViewModel

@Composable
fun CharacterDetailScreen(
    @Suppress("UNUSED_PARAMETER") characterId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    // characterId is part of the navigation route and extracted by the ViewModel from SavedStateHandle
    val state by viewModel.state.collectAsStateWithLifecycle()

    CharacterDetailContent(
        state = state,
        onBackClick = onBackClick,
        onFavoriteClick = { viewModel.handleIntent(CharacterDetailIntent.ToggleFavorite) },
        onRetryClick = { viewModel.handleIntent(CharacterDetailIntent.RetryLoad) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterDetailContent(
    state: CharacterDetailState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CharacterDetailTopBar(
                topBarState = TopBarState(
                    characterName = state.character?.name?.ifBlank { "Unknown" } ?: "Character",
                    isFavorite = state.character?.isFavorite ?: false,
                    showFavoriteButton = state.character != null
                ),
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingContent(paddingValues)
            state.error != null -> ErrorContent(
                error = state.error,
                onRetryClick = onRetryClick,
                paddingValues = paddingValues
            )
            state.character != null -> CharacterDetails(
                character = state.character,
                paddingValues = paddingValues
            )
        }
    }
}

private data class TopBarState(
    val characterName: String,
    val isFavorite: Boolean,
    val showFavoriteButton: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterDetailTopBar(
    topBarState: TopBarState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = topBarState.characterName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            if (topBarState.showFavoriteButton) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (topBarState.isFavorite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Outlined.FavoriteBorder
                        },
                        contentDescription = if (topBarState.isFavorite) {
                            "Remove from favorites"
                        } else {
                            "Add to favorites"
                        },
                        tint = if (topBarState.isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun LoadingContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(error: String, onRetryClick: () -> Unit, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetryClick) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun CharacterDetails(character: Character, paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroSection(character = character)
        }

        item {
            BasicInfoSection(character = character)
        }

        if (character.titles.any { it.isNotBlank() }) {
            item {
                TitlesSection(titles = character.titles.filter { it.isNotBlank() })
            }
        }

        if (character.aliases.any { it.isNotBlank() }) {
            item {
                AliasesSection(aliases = character.aliases.filter { it.isNotBlank() })
            }
        }

        if (character.tvSeriesSeasons.isNotEmpty()) {
            item {
                TVSeriesSection(seasons = character.tvSeriesSeasons)
            }
        }

        if (character.playedBy.any { it.isNotBlank() }) {
            item {
                ActorsSection(actors = character.playedBy.filter { it.isNotBlank() })
            }
        }
    }
}

@Composable
private fun HeroSection(character: Character, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = character.name.ifBlank { "Unknown" },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (character.isDead) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\u271D Deceased",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun BasicInfoSection(character: Character, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Basic Information",
                style = MaterialTheme.typography.titleLarge
            )

            if (character.gender.isNotBlank()) {
                InfoRow(label = "Gender", value = character.gender)
            }
            if (character.culture.isNotBlank()) {
                InfoRow(label = "Culture", value = character.culture)
            }
            if (character.born.isNotBlank()) {
                InfoRow(label = "Born", value = character.born)
            }
            if (character.died.isNotBlank()) {
                InfoRow(label = "Died", value = character.died)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
private fun TitlesSection(titles: List<String>, modifier: Modifier = Modifier) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(modifier = modifier.fillMaxWidth()) {
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
                Text(
                    text = "Titles (${titles.size})",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) {
                            Icons.Default.ExpandLess
                        } else {
                            Icons.Default.ExpandMore
                        },
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    titles.forEach { title ->
                        Text(
                            text = "• $title",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AliasesSection(aliases: List<String>, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Also Known As",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                aliases.forEach { alias ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = alias,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TVSeriesSection(seasons: List<Int>, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "TV Series Appearances",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                seasons.sorted().forEach { season ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = "Season ${RomanNumeralConverter.toRomanNumeral(season)}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActorsSection(actors: List<String>, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Played By",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                actors.forEach { actor ->
                    Text(
                        text = actor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
