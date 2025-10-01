package com.darach.gameofthrones.feature.characterdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.core.domain.util.RomanNumeralConverter
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.core.ui.component.PortraitImage
import com.darach.gameofthrones.core.ui.transition.SharedTransitionData
import kotlinx.coroutines.launch

@Composable
private fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return androidx.compose.runtime.remember(view) {
        object : HapticFeedback {
            override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
                when (hapticFeedbackType) {
                    HapticFeedbackType.LongPress -> view.performHapticFeedback(
                        android.view.HapticFeedbackConstants.LONG_PRESS
                    )

                    HapticFeedbackType.TextHandleMove -> view.performHapticFeedback(
                        android.view.HapticFeedbackConstants.TEXT_HANDLE_MOVE
                    )

                    else -> view.performHapticFeedback(
                        android.view.HapticFeedbackConstants.CONTEXT_CLICK
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CharacterDetailScreen(
    @Suppress("UnusedParameter") characterId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterDetailViewModel = hiltViewModel(),
    sharedTransitionData: SharedTransitionData? = null
) {
    // characterId is part of the navigation route and extracted by the ViewModel from SavedStateHandle
    val state by viewModel.state.collectAsStateWithLifecycle()

    CharacterDetailContent(
        state = state,
        onBackClick = onBackClick,
        onFavoriteClick = { viewModel.handleIntent(CharacterDetailIntent.ToggleFavorite) },
        onRetryClick = { viewModel.handleIntent(CharacterDetailIntent.RetryLoad) },
        modifier = modifier,
        sharedTransitionData = sharedTransitionData
    )
}

@Composable
private fun FavoriteSnackbarEffect(
    isFavorite: Boolean?,
    snackbarHostState: SnackbarHostState,
    onUndoFavorite: () -> Unit
) {
    val updatedOnUndoFavorite by rememberUpdatedState(onUndoFavorite)
    val scope = rememberCoroutineScope()
    var previousFavoriteState by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(isFavorite) {
        if (previousFavoriteState != null && previousFavoriteState != isFavorite &&
            isFavorite != null
        ) {
            val message = if (isFavorite) "Added to favorites" else "Removed from favorites"

            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    updatedOnUndoFavorite()
                }
            }
        }
        previousFavoriteState = isFavorite
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
@Suppress("LongParameterList")
private fun CharacterDetailContent(
    state: CharacterDetailState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val haptic = rememberHapticFeedback()
    val snackbarHostState = remember { SnackbarHostState() }

    FavoriteSnackbarEffect(
        isFavorite = state.character?.isFavorite,
        snackbarHostState = snackbarHostState,
        onUndoFavorite = onFavoriteClick
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
                onBackClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onBackClick()
                },
                onFavoriteClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFavoriteClick()
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingContent(paddingValues)
            state.error != null -> ErrorContent(
                error = state.error,
                onRetryClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onRetryClick()
                },
                paddingValues = paddingValues
            )

            state.character != null -> CharacterDetails(
                character = state.character,
                paddingValues = paddingValues,
                sharedTransitionData = sharedTransitionData
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
        CircularProgressIndicator(
            modifier = Modifier.semantics {
                contentDescription = "Loading character details"
            }
        )
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CharacterDetails(
    character: Character,
    paddingValues: PaddingValues,
    sharedTransitionData: SharedTransitionData? = null
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroSection(
                character = character,
                sharedTransitionData = sharedTransitionData
            )
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
                ActorsSection(
                    actors = character.playedBy.filter { it.isNotBlank() },
                    actorImageUrls = character.actorImageUrls
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun HeroSection(
    character: Character,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
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
            if (character.characterImageUrl != null) {
                HeroPortraitImage(
                    character = character,
                    sharedTransitionData = sharedTransitionData
                )
            }

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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun HeroPortraitImage(character: Character, sharedTransitionData: SharedTransitionData?) {
    val imageModifier = if (sharedTransitionData != null) {
        with(sharedTransitionData.sharedTransitionScope) {
            Modifier
                .fillMaxWidth(0.6f)
                .padding(bottom = 16.dp)
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
        Modifier
            .fillMaxWidth(0.6f)
            .padding(bottom = 16.dp)
    }

    PortraitImage(
        imageUrl = character.characterImageUrl,
        contentDescription = character.name,
        modifier = imageModifier
    )
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
private fun ExpandableHeader(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = rememberHapticFeedback()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(
            onClick = {
                haptic.performHapticFeedback(
                    if (isExpanded) {
                        HapticFeedbackType.TextHandleMove
                    } else {
                        HapticFeedbackType.LongPress
                    }
                )
                onToggle()
            },
            modifier = Modifier.size(48.dp)
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
            ExpandableHeader(
                title = "Titles (${titles.size})",
                isExpanded = isExpanded,
                onToggle = { isExpanded = !isExpanded }
            )

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
private fun ActorsSection(
    actors: List<String>,
    actorImageUrls: Map<String, String?>,
    modifier: Modifier = Modifier
) {
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                actors.forEach { actor ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PortraitImage(
                            imageUrl = actorImageUrls[actor],
                            contentDescription = actor,
                            modifier = Modifier.size(width = 60.dp, height = 80.dp)
                        )
                        Text(
                            text = actor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

// Sample data for previews
private val sampleCharacter = Character(
    id = "583",
    name = "Jon Snow",
    gender = "Male",
    culture = "Northmen",
    born = "In 283 AC",
    died = "",
    titles = listOf(
        "Lord Commander of the Night's Watch",
        "King in the North",
        "Warden of the North"
    ),
    aliases = listOf("Lord Snow", "Ned Stark's Bastard", "The White Wolf", "King Crow"),
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

private val sampleDeceasedCharacter = Character(
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
    isFavorite = false,
    isDead = true
)

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Loading",
    showBackground = true
)
@Composable
private fun LoadingContentPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        LoadingContent(paddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Error",
    showBackground = true
)
@Composable
private fun ErrorContentPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ErrorContent(
            error = "Character not found",
            onRetryClick = {},
            paddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Hero Section",
    showBackground = true
)
@Composable
private fun HeroSectionPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        HeroSection(character = sampleCharacter)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Hero Section Deceased",
    showBackground = true
)
@Composable
private fun HeroSectionDeceasedPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        HeroSection(character = sampleDeceasedCharacter)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Basic Info Section",
    showBackground = true
)
@Composable
private fun BasicInfoSectionPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        BasicInfoSection(character = sampleCharacter)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Titles Section",
    showBackground = true
)
@Composable
private fun TitlesSectionPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        TitlesSection(titles = sampleCharacter.titles)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - TV Series Section",
    showBackground = true
)
@Composable
private fun TVSeriesSectionPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        TVSeriesSection(seasons = sampleCharacter.tvSeriesSeasons)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HeroSectionDarkPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        HeroSection(character = sampleCharacter)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun BasicInfoSectionTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        BasicInfoSection(character = sampleDeceasedCharacter)
    }
}
