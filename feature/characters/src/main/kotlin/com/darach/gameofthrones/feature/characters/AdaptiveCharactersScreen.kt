package com.darach.gameofthrones.feature.characters

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.darach.gameofthrones.core.ui.transition.SharedTransitionData
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailIntent
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailScreen
import com.darach.gameofthrones.feature.characterdetail.CharacterDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveCharactersScreen(
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {

    var selectedCharacterId by rememberSaveable(
        saver = Saver(
            save = { it },
            restore = { it }
        )
    ) {
        mutableStateOf<String?>(null)
    }

    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()
    val scope = rememberCoroutineScope()

    val isInTwoPaneMode = navigator.scaffoldDirective.maxHorizontalPartitions > 1
    val gridSize = if (isInTwoPaneMode) 110.dp else 160.dp

    BackHandler(navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                CharactersScreen(
                    onCharacterClick = { characterId ->
                        selectedCharacterId = characterId
                        scope.launch {
                            navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    sharedTransitionData = sharedTransitionData,
                    gridMinSize = gridSize
                )
            }
        },
        detailPane = {
            AnimatedPane {
                selectedCharacterId?.let { characterId ->
                    key(characterId) {
                        CharacterDetailPaneWrapper(
                            characterId = characterId,
                            onBackClick = {
                                selectedCharacterId = null
                                scope.launch {
                                    navigator.navigateBack()
                                }
                            },
                            sharedTransitionData = sharedTransitionData
                        )
                    }
                } ?: run {
                    DetailPlaceholder()
                }
            }
        }
    )
}

@Suppress("ViewModelForwarding")
@Composable
private fun CharacterDetailPaneWrapper(
    characterId: String,
    onBackClick: () -> Unit,
    sharedTransitionData: SharedTransitionData?,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(characterId) {
        viewModel.handleIntent(CharacterDetailIntent.LoadCharacter(characterId))
    }

    CharacterDetailScreen(
        characterId = characterId,
        onBackClick = onBackClick,
        viewModel = viewModel,
        sharedTransitionData = sharedTransitionData
    )
}

@Composable
private fun DetailPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(com.darach.gameofthrones.core.ui.R.string.select_character),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(
                    com.darach.gameofthrones.core.ui.R.string.select_character_description
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

// Previews for different window sizes
@androidx.compose.ui.tooling.preview.Preview(
    name = "Phone - List View",
    device = "spec:width=411dp,height=891dp"
)
@Composable
private fun AdaptiveCharactersScreenPhonePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        AdaptiveCharactersScreen()
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Foldable - Two Pane",
    device = "spec:width=673dp,height=841dp"
)
@Composable
private fun AdaptiveCharactersScreenFoldablePreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        AdaptiveCharactersScreen()
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Tablet - Two Pane",
    device = "spec:width=1280dp,height=800dp"
)
@Composable
private fun AdaptiveCharactersScreenTabletPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        AdaptiveCharactersScreen()
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Tablet - No Selection",
    device = "spec:width=1280dp,height=800dp"
)
@Composable
private fun DetailPlaceholderPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        DetailPlaceholder()
    }
}
