package com.darach.gameofthrones.feature.comparison

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darach.gameofthrones.core.ui.component.PortraitImage
import com.darach.gameofthrones.core.ui.test.TestTags

/**
 * Main comparison screen showing side-by-side character comparison.
 * Displays attributes with difference highlighting and synchronized scrolling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    comparisonResult: ComparisonResult?,
    isLoading: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Character Comparison") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
                comparisonResult != null -> ComparisonContent(comparisonResult)
                else -> EmptyState()
            }
        }
    }
}

@Composable
private fun ComparisonContent(comparisonResult: ComparisonResult, modifier: Modifier = Modifier) {
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
        TaleOfTheTapeHeader(character1, character2)

        Spacer(modifier = Modifier.height(24.dp))

        // Attributes in boxing style
        comparisonResult.attributes.forEach { attribute ->
            TaleOfTheTapeRow(attribute)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TaleOfTheTapeHeader(
    character1: com.darach.gameofthrones.core.model.Character?,
    character2: com.darach.gameofthrones.core.model.Character?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TALE OF THE TAPE",
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
                modifier = Modifier.weight(1f)
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
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CharacterColumn(
    character: com.darach.gameofthrones.core.model.Character?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PortraitImage(
            imageUrl = character?.characterImageUrl,
            contentDescription = character?.name,
            modifier = Modifier.width(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = character?.name ?: "",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
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
                value1?.let { AttributeValueText(it, Alignment.Start) }
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
                value2?.let { AttributeValueText(it, Alignment.End) }
            }
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
                text = "Error",
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
            text = "No comparison data available",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Sample data for previews
private val sampleJonSnow = com.darach.gameofthrones.core.model.Character(
    id = "583",
    name = "Jon Snow",
    gender = "Male",
    culture = "Northmen",
    born = "In 283 AC",
    died = "",
    titles = listOf("Lord Commander of the Night's Watch", "King in the North"),
    aliases = listOf("Lord Snow", "The White Wolf"),
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

private val sampleAryaStark = com.darach.gameofthrones.core.model.Character(
    id = "148",
    name = "Arya Stark",
    gender = "Female",
    culture = "Northmen",
    born = "In 289 AC",
    died = "",
    titles = listOf("Princess"),
    aliases = listOf("No One", "Cat of the Canals"),
    father = "",
    mother = "",
    spouse = "",
    allegiances = listOf(),
    books = listOf(),
    povBooks = listOf(),
    tvSeries = listOf("Season 1", "Season 2", "Season 3"),
    tvSeriesSeasons = listOf(1, 2, 3, 4, 5, 6, 7, 8),
    playedBy = listOf("Maisie Williams"),
    isFavorite = true,
    isDead = false
)

private val sampleComparisonResult = ComparisonResult(
    characters = listOf(sampleJonSnow, sampleAryaStark),
    attributes = listOf(
        ComparisonAttribute(
            name = "Gender",
            values = listOf(
                AttributeValue("Male", isDifferent = true),
                AttributeValue("Female", isDifferent = true)
            ),
            hasDifference = true
        ),
        ComparisonAttribute(
            name = "Culture",
            values = listOf(
                AttributeValue("Northmen", isDifferent = false),
                AttributeValue("Northmen", isDifferent = false)
            ),
            hasDifference = false
        ),
        ComparisonAttribute(
            name = "Born",
            values = listOf(
                AttributeValue("In 283 AC", isDifferent = true),
                AttributeValue("In 289 AC", isDifferent = true)
            ),
            hasDifference = true
        ),
        ComparisonAttribute(
            name = "Status",
            values = listOf(
                AttributeValue("Alive", isDifferent = false),
                AttributeValue("Alive", isDifferent = false)
            ),
            hasDifference = false
        ),
        ComparisonAttribute(
            name = "Seasons",
            values = listOf(
                AttributeValue("8 seasons", isDifferent = false),
                AttributeValue("8 seasons", isDifferent = false)
            ),
            hasDifference = false
        )
    )
)

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Comparison Screen - Loading",
    showBackground = true
)
@Composable
private fun ComparisonScreenLoadingPreview() {
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
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
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = sampleComparisonResult,
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
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
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
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
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
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = sampleComparisonResult,
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
    com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme {
        ComparisonScreen(
            comparisonResult = sampleComparisonResult,
            isLoading = false,
            error = null,
            onBackClick = {}
        )
    }
}
