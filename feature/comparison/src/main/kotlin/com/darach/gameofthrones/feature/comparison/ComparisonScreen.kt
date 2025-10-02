package com.darach.gameofthrones.feature.comparison

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.model.Character
import com.darach.gameofthrones.core.ui.component.PortraitImage
import com.darach.gameofthrones.core.ui.test.TestTags
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme
import com.darach.gameofthrones.core.ui.transition.SharedTransitionData

/**
 * Main comparison screen showing side-by-side character comparison.
 * Displays attributes with difference highlighting and synchronized scrolling.
 */
@Suppress("LongParameterList") // Compose screens require multiple callbacks and state parameters
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ComparisonScreen(
    comparisonResult: ComparisonResult?,
    isLoading: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    val haptics = LocalHapticFeedback.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            com.darach.gameofthrones.core.ui.R.string.character_comparison
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                com.darach.gameofthrones.core.ui.R.string.back
                            )
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> LoadingState()
                error != null -> ErrorState(error)
                comparisonResult != null -> ComparisonContent(
                    comparisonResult = comparisonResult,
                    sharedTransitionData = sharedTransitionData
                )
                else -> EmptyState()
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ComparisonContent(
    comparisonResult: ComparisonResult,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    if (isTablet) {
        TabletComparisonContent(comparisonResult, modifier, sharedTransitionData)
    } else {
        PhoneComparisonContent(comparisonResult, modifier, sharedTransitionData)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhoneComparisonContent(
    comparisonResult: ComparisonResult,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    val scrollState = rememberScrollState()
    val character1 = comparisonResult.characters.getOrNull(0)
    val character2 = comparisonResult.characters.getOrNull(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(TestTags.COMPARISON_RESULT_TABLE)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // VS Header
        TaleOfTheTapeHeader(
            character1 = character1,
            character2 = character2,
            sharedTransitionData = sharedTransitionData
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Attributes in boxing style
        comparisonResult.attributes.forEach { attribute ->
            TaleOfTheTapeRow(attribute)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TabletComparisonContent(
    comparisonResult: ComparisonResult,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    val scrollState = rememberScrollState()
    val character1 = comparisonResult.characters.getOrNull(0)
    val character2 = comparisonResult.characters.getOrNull(1)

    Row(
        modifier = modifier
            .fillMaxSize()
            .testTag(TestTags.COMPARISON_RESULT_TABLE)
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        TabletCharacterColumn(
            character = character1,
            attributes = comparisonResult.attributes,
            characterIndex = 0,
            scrollState = scrollState,
            sharedTransitionData = sharedTransitionData,
            modifier = Modifier.weight(0.3f)
        )

        TabletVSDivider(modifier = Modifier.weight(0.1f))

        TabletCharacterColumn(
            character = character2,
            attributes = comparisonResult.attributes,
            characterIndex = 1,
            scrollState = scrollState,
            sharedTransitionData = sharedTransitionData,
            modifier = Modifier.weight(0.3f)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Suppress("LongParameterList")
private fun TabletCharacterColumn(
    character: Character?,
    attributes: List<ComparisonAttribute>,
    characterIndex: Int,
    scrollState: androidx.compose.foundation.ScrollState,
    sharedTransitionData: SharedTransitionData?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CharacterPortraitWithTransition(character, sharedTransitionData)
        CharacterNameWithTransition(character, sharedTransitionData)

        attributes.forEach { attribute ->
            attribute.values.getOrNull(characterIndex)?.let { value ->
                TabletAttributeCard(
                    label = attribute.name,
                    value = value,
                    isDifferent = attribute.hasDifference
                )
            }
        }
    }
}

@Composable
private fun TabletVSDivider(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(com.darach.gameofthrones.core.ui.R.string.vs),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TabletAttributeCard(
    label: String,
    value: AttributeValue,
    isDifferent: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDifferent) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabletAttributeCardLabel(label)

            if (value.actorData.isNotEmpty()) {
                TabletActorDataDisplay(value.actorData)
            } else {
                TabletTextValueDisplay(value)
            }
        }
    }
}

@Composable
private fun TabletAttributeCardLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun TabletActorDataDisplay(actorData: List<ActorInfo>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        actorData.forEach { actor ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PortraitImage(
                    imageUrl = actor.imageUrl,
                    contentDescription = actor.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = actor.name,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TabletTextValueDisplay(value: AttributeValue) {
    val textColor = when {
        value.isEmpty -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        value.isDifferent -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = value.value,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (value.isDifferent) FontWeight.Bold else FontWeight.Normal,
        color = textColor,
        textAlign = TextAlign.Center,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun TaleOfTheTapeHeader(
    character1: Character?,
    character2: Character?,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(com.darach.gameofthrones.core.ui.R.string.tale_of_the_tape),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            CharacterColumn(
                character = character1,
                modifier = Modifier.weight(1f),
                sharedTransitionData = sharedTransitionData
            )

            Text(
                text = "VS",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 40.dp)
            )

            CharacterColumn(
                character = character2,
                modifier = Modifier.weight(1f),
                sharedTransitionData = sharedTransitionData
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CharacterColumn(
    character: Character?,
    modifier: Modifier = Modifier,
    sharedTransitionData: SharedTransitionData? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CharacterPortraitWithTransition(
            character = character,
            sharedTransitionData = sharedTransitionData
        )

        Spacer(modifier = Modifier.height(8.dp))

        CharacterNameWithTransition(
            character = character,
            sharedTransitionData = sharedTransitionData
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CharacterPortraitWithTransition(
    character: Character?,
    sharedTransitionData: SharedTransitionData?
) {
    val imageModifier = createCharacterImageModifier(character, sharedTransitionData)

    PortraitImage(
        imageUrl = character?.characterImageUrl.takeIf { !it.isNullOrBlank() },
        contentDescription = character?.name,
        modifier = imageModifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun createCharacterImageModifier(
    character: Character?,
    sharedTransitionData: SharedTransitionData?
): Modifier = if (sharedTransitionData != null && character != null) {
    with(sharedTransitionData.sharedTransitionScope) {
        Modifier
            .width(120.dp)
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
            .clip(RoundedCornerShape(8.dp))
    }
} else {
    Modifier
        .width(120.dp)
        .clip(RoundedCornerShape(8.dp))
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun CharacterNameWithTransition(
    character: Character?,
    sharedTransitionData: SharedTransitionData?
) {
    val nameModifier = createCharacterNameModifier(character, sharedTransitionData)

    Text(
        text = character?.name ?: "",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = nameModifier
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun createCharacterNameModifier(
    character: Character?,
    sharedTransitionData: SharedTransitionData?
): Modifier = if (sharedTransitionData != null && character != null) {
    with(sharedTransitionData.sharedTransitionScope) {
        Modifier.sharedElement(
            rememberSharedContentState(key = "character-name-${character.id}"),
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
}

@Composable
private fun TaleOfTheTapeRow(attribute: ComparisonAttribute, modifier: Modifier = Modifier) {
    val value1 = attribute.values.getOrNull(0)
    val value2 = attribute.values.getOrNull(1)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (attribute.hasDifference) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left value
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                value1?.let { AttributeValueDisplay(it, Alignment.Start) }
            }

            // Center label
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = attribute.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right value
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                value2?.let { AttributeValueDisplay(it, Alignment.End) }
            }
        }
    }
}

@Composable
private fun AttributeValueDisplay(
    value: AttributeValue,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    if (value.actorData.isNotEmpty()) {
        // Display actors with images
        ActorListDisplay(
            actors = value.actorData,
            alignment = alignment,
            modifier = modifier
        )
    } else {
        // Display regular text
        AttributeValueText(
            value = value,
            alignment = alignment,
            modifier = modifier
        )
    }
}

@Composable
private fun ActorListDisplay(
    actors: List<ActorInfo>,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    val arrangement = when (alignment) {
        Alignment.Start -> Arrangement.Start
        Alignment.End -> Arrangement.End
        else -> Arrangement.Center
    }

    Column(
        modifier = modifier,
        horizontalAlignment = when (alignment) {
            Alignment.Start -> Alignment.Start
            Alignment.End -> Alignment.End
            else -> Alignment.CenterHorizontally
        },
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        actors.forEach { actor ->
            Row(
                horizontalArrangement = arrangement,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (alignment == Alignment.Start) {
                    ActorImageAndName(actor, alignment)
                } else {
                    ActorImageAndName(actor, alignment)
                }
            }
        }
    }
}

@Composable
private fun ActorImageAndName(
    actor: ActorInfo,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    if (alignment == Alignment.Start) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier
        ) {
            PortraitImage(
                imageUrl = actor.imageUrl,
                contentDescription = actor.name,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = actor.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = modifier
        ) {
            Text(
                text = actor.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.width(8.dp))
            PortraitImage(
                imageUrl = actor.imageUrl,
                contentDescription = actor.name,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun AttributeValueText(
    value: AttributeValue,
    alignment: Alignment.Horizontal,
    modifier: Modifier = Modifier
) {
    val textColor = when {
        value.isEmpty -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        value.isDifferent -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = value.value,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (value.isDifferent) FontWeight.Bold else FontWeight.Normal,
        color = textColor,
        textAlign = when (alignment) {
            Alignment.Start -> TextAlign.Start
            Alignment.End -> TextAlign.End
            else -> TextAlign.Center
        },
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(error: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(com.darach.gameofthrones.core.ui.R.string.error),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(com.darach.gameofthrones.core.ui.R.string.no_comparison_data),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Screen - Loading",
    showBackground = true
)
@Composable
private fun ComparisonScreenLoadingPreview() {
    GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = null,
            isLoading = true,
            error = null,
            onBackClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Screen - With Data",
    showBackground = true
)
@Composable
private fun ComparisonScreenWithDataPreview() {
    GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = ComparisonPreviewData.comparisonResult,
            isLoading = false,
            error = null,
            onBackClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Screen - Error",
    showBackground = true
)
@Composable
private fun ComparisonScreenErrorPreview() {
    GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = null,
            isLoading = false,
            error = "Failed to load comparison data",
            onBackClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Screen - Empty",
    showBackground = true
)
@Composable
private fun ComparisonScreenEmptyPreview() {
    GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = null,
            isLoading = false,
            error = null,
            onBackClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Screen - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ComparisonScreenDarkPreview() {
    GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = ComparisonPreviewData.comparisonResult,
            isLoading = false,
            error = null,
            onBackClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Screen - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun ComparisonScreenTabletPreview() {
    GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = ComparisonPreviewData.comparisonResult,
            isLoading = false,
            error = null,
            onBackClick = {}
        )
    }
}
