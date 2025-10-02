package com.darach.gameofthrones.feature.characterdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.core.domain.util.RomanNumeralConverter
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.core.ui.component.PortraitImage
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme
import com.darach.gameofthrones.core.ui.transition.SharedTransitionData
import kotlinx.coroutines.launch

@Composable
private fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember(view) {
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

    val addedMessage = stringResource(com.darach.gameofthrones.core.ui.R.string.added_to_favorites)
    val removedMessage =
        stringResource(com.darach.gameofthrones.core.ui.R.string.removed_from_favorites)
    val undoLabel = stringResource(com.darach.gameofthrones.core.ui.R.string.undo)

    LaunchedEffect(isFavorite) {
        if (previousFavoriteState != null && previousFavoriteState != isFavorite &&
            isFavorite != null
        ) {
            val message = if (isFavorite) addedMessage else removedMessage

            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = undoLabel,
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

@Composable
private fun DetailSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            actionColor = MaterialTheme.colorScheme.inversePrimary,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailScaffoldContent(
    state: CharacterDetailState,
    onRetryClick: () -> Unit,
    haptic: HapticFeedback,
    paddingValues: PaddingValues,
    sharedTransitionData: SharedTransitionData?
) {
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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
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
        snackbarHost = { DetailSnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        DetailScaffoldContent(state, onRetryClick, haptic, paddingValues, sharedTransitionData)
    }
}

private data class TopBarState(
    val characterName: String,
    val isFavorite: Boolean,
    val showFavoriteButton: Boolean
)

@Composable
private fun BackNavigationIcon(onBackClick: () -> Unit) {
    IconButton(
        onClick = onBackClick,
        modifier = Modifier
            .padding(4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(
                com.darach.gameofthrones.core.ui.R.string.navigate_back
            )
        )
    }
}

@Composable
private fun FavoriteActionButton(isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "favorite_scale"
    )

    IconButton(
        onClick = onFavoriteClick,
        modifier = Modifier
            .padding(4.dp)
            .scale(favoriteScale)
    ) {
        Icon(
            imageVector = if (isFavorite) {
                Icons.Filled.Favorite
            } else {
                Icons.Outlined.FavoriteBorder
            },
            contentDescription = if (isFavorite) {
                stringResource(com.darach.gameofthrones.core.ui.R.string.remove_from_favorites)
            } else {
                stringResource(com.darach.gameofthrones.core.ui.R.string.add_to_favorites)
            },
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

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
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = { BackNavigationIcon(onBackClick) },
        actions = {
            if (topBarState.showFavoriteButton) {
                FavoriteActionButton(topBarState.isFavorite, onFavoriteClick)
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun LoadingContent(paddingValues: PaddingValues) {
    val loadingDescription = stringResource(
        com.darach.gameofthrones.core.ui.R.string.loading_character_details
    )
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
            CircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
                    .semantics {
                        contentDescription = loadingDescription
                    },
                strokeWidth = 6.dp
            )
            Text(
                text = stringResource(com.darach.gameofthrones.core.ui.R.string.loading_character),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "⚠",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Text(
                text = error,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onRetryClick,
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    stringResource(com.darach.gameofthrones.core.ui.R.string.retry),
                    fontWeight = FontWeight.SemiBold
                )
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
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (isTablet) {
        TabletCharacterDetails(character, paddingValues, sharedTransitionData)
    } else {
        PhoneCharacterDetails(character, paddingValues, sharedTransitionData)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhoneCharacterDetails(
    character: Character,
    paddingValues: PaddingValues,
    sharedTransitionData: SharedTransitionData?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = paddingValues.calculateTopPadding() + 8.dp,
            bottom = paddingValues.calculateBottomPadding() + 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeroSection(character, sharedTransitionData) }
        item { BasicInfoSection(character) }

        if (character.titles.any { it.isNotBlank() }) {
            item { TitlesSection(character.titles.filter { it.isNotBlank() }) }
        }

        if (character.aliases.any { it.isNotBlank() }) {
            item { AliasesSection(character.aliases.filter { it.isNotBlank() }) }
        }

        if (character.tvSeriesSeasons.isNotEmpty()) {
            item { TVSeriesSection(character.tvSeriesSeasons) }
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
private fun TabletCharacterDetails(
    character: Character,
    paddingValues: PaddingValues,
    sharedTransitionData: SharedTransitionData?
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 16.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Left column: Hero section (40% width)
        Box(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxSize()
        ) {
            androidx.compose.foundation.lazy.LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { HeroSection(character, sharedTransitionData) }
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

        // Right column: Details (60% width)
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { BasicInfoSection(character) }

            if (character.titles.any { it.isNotBlank() }) {
                item { TitlesSection(character.titles.filter { it.isNotBlank() }) }
            }

            if (character.aliases.any { it.isNotBlank() }) {
                item { AliasesSection(character.aliases.filter { it.isNotBlank() }) }
            }

            if (character.tvSeriesSeasons.isNotEmpty()) {
                item { TVSeriesSection(character.tvSeriesSeasons) }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun HeroSection(character: Character, sharedTransitionData: SharedTransitionData? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (character.characterImageUrl != null) {
                    HeroPortraitImage(character, sharedTransitionData)
                }

                if (character.isDead) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = stringResource(
                                com.darach.gameofthrones.core.ui.R.string.status_deceased_symbol
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
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
                .padding(bottom = 20.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .sharedBounds(
                    rememberSharedContentState(key = "character-image-${character.id}"),
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
                .clip(RoundedCornerShape(20.dp))
        }
    } else {
        Modifier
            .fillMaxWidth(0.6f)
            .padding(bottom = 20.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
    }

    PortraitImage(
        imageUrl = character.characterImageUrl,
        contentDescription = character.name,
        modifier = imageModifier
    )
}

@Composable
private fun BasicInfoSection(character: Character) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(com.darach.gameofthrones.core.ui.R.string.basic_information),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (character.gender.isNotBlank()) {
                InfoRow(
                    label = stringResource(com.darach.gameofthrones.core.ui.R.string.label_gender),
                    value = character.gender,
                    emoji = "⚧"
                )
            }
            if (character.culture.isNotBlank()) {
                InfoRow(
                    label = stringResource(com.darach.gameofthrones.core.ui.R.string.label_culture),
                    value = character.culture,
                    emoji = "🏰"
                )
            }
            if (character.born.isNotBlank()) {
                InfoRow(
                    label = stringResource(com.darach.gameofthrones.core.ui.R.string.label_born),
                    value = character.born,
                    emoji = "🎂"
                )
            }
            if (character.died.isNotBlank()) {
                InfoRow(
                    label = stringResource(com.darach.gameofthrones.core.ui.R.string.label_died),
                    value = character.died,
                    emoji = "✝"
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, emoji: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleMedium
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
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
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "rotation"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
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
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) {
                    stringResource(
                        com.darach.gameofthrones.core.ui.R.string.collapse
                    )
                } else {
                    stringResource(com.darach.gameofthrones.core.ui.R.string.expand)
                },
                modifier = Modifier.rotate(rotation),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun TitlesSection(titles: List<String>) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            ExpandableHeader(
                title = stringResource(
                    com.darach.gameofthrones.core.ui.R.string.titles_section,
                    titles.size
                ),
                isExpanded = isExpanded,
                onToggle = { isExpanded = !isExpanded }
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    titles.forEach { title ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👑",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AliasesSection(aliases: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(com.darach.gameofthrones.core.ui.R.string.also_known_as),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                aliases.forEach { alias ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = alias,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        border = null,
                        elevation = AssistChipDefaults.assistChipElevation(elevation = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TVSeriesSection(seasons: List<Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(
                    com.darach.gameofthrones.core.ui.R.string.tv_series_appearances
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                seasons.sorted().forEach { season ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shadowElevation = 3.dp
                    ) {
                        Text(
                            text = stringResource(
                                com.darach.gameofthrones.core.ui.R.string.season_roman,
                                RomanNumeralConverter.toRomanNumeral(season)
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActorsSection(actors: List<String>, actorImageUrls: Map<String, String?>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(com.darach.gameofthrones.core.ui.R.string.played_by),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Loading",
    showBackground = true
)
@Composable
private fun LoadingContentPreview() {
    GameOfThronesTheme {
        LoadingContent(paddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Character Detail - Error",
    showBackground = true
)
@Composable
private fun ErrorContentPreview() {
    GameOfThronesTheme {
        ErrorContent(
            error = "Character not found",
            onRetryClick = {},
            paddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp)
        )
    }
}
