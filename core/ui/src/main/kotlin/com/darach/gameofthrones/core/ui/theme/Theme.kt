package com.darach.gameofthrones.core.ui.theme

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark
)

@Composable
fun GameOfThronesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

@Composable
@Preview(
    name = "Light Theme",
    showBackground = true,
    widthDp = 1920,
    heightDp = 1080
)
private fun ThemeShowcasePreview() {
    GameOfThronesTheme {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TypographySection()
            SectionDivider()
            ButtonsSection()
            SectionDivider()
            ChipsSection()
            SectionDivider()
            CardsSection()
            SectionDivider()
            BadgesSection()
            SectionDivider()
            SelectionControlsSection()
            SectionDivider()
            SlidersSection()
            SectionDivider()
            ProgressIndicatorsSection()
            SectionDivider()
            TextFieldsSection()
            SectionDivider()
            ColorPaletteSection()
        }
    }
}

@Composable
private fun SectionDivider() {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TypographySection() {
    Column {
        Text(
            text = "Typography Showcase",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Display Large", style = MaterialTheme.typography.displayLarge)
        Text("Display Medium", style = MaterialTheme.typography.displayMedium)
        Text("Display Small", style = MaterialTheme.typography.displaySmall)
        Text("Headline Large", style = MaterialTheme.typography.headlineLarge)
        Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
        Text("Headline Small", style = MaterialTheme.typography.headlineSmall)
        Text("Title Large", style = MaterialTheme.typography.titleLarge)
        Text("Title Medium", style = MaterialTheme.typography.titleMedium)
        Text("Title Small", style = MaterialTheme.typography.titleSmall)
        Text("Body Large", style = MaterialTheme.typography.bodyLarge)
        Text("Body Medium", style = MaterialTheme.typography.bodyMedium)
        Text("Body Small", style = MaterialTheme.typography.bodySmall)
        Text("Label Large", style = MaterialTheme.typography.labelLarge)
        Text("Label Medium", style = MaterialTheme.typography.labelMedium)
        Text("Label Small", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ButtonsSection() {
    Column {
        Text(
            text = "Buttons",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {}) { Text("Filled Button") }
        Spacer(modifier = Modifier.height(8.dp))
        FilledTonalButton(onClick = {}) { Text("Filled Tonal Button") }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = {}) { Text("Outlined Button") }
        Spacer(modifier = Modifier.height(8.dp))
        ElevatedButton(onClick = {}) { Text("Elevated Button") }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = {}) { Text("Text Button") }
    }
}

@Composable
private fun ChipsSection() {
    Column {
        Text(
            text = "Chips",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            AssistChip(onClick = {}, label = { Text("Assist Chip") })
            FilterChip(selected = true, onClick = {}, label = { Text("Filter Chip") })
            InputChip(selected = false, onClick = {}, label = { Text("Input Chip") })
            SuggestionChip(onClick = {}, label = { Text("Suggestion Chip") })
        }
    }
}

@Composable
private fun CardsSection() {
    Column {
        Text(
            text = "Cards",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Card", modifier = Modifier.padding(16.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Elevated Card", modifier = Modifier.padding(16.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Outlined Card", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun BadgesSection() {
    Column {
        Text(
            text = "Badges",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            BadgedBox(badge = { Badge() }) {
                Icon(imageVector = Icons.Default.Star, contentDescription = "Badge")
            }
            BadgedBox(badge = { Badge { Text("9") } }) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notification")
            }
            BadgedBox(badge = { Badge { Text("99+") } }) {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
            }
        }
    }
}

@Composable
private fun SelectionControlsSection() {
    Column {
        Text(
            text = "Switches & Selection Controls",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Switch(checked = true, onCheckedChange = {})
            Switch(checked = false, onCheckedChange = {})
            Checkbox(checked = true, onCheckedChange = {})
            Checkbox(checked = false, onCheckedChange = {})
            RadioButton(selected = true, onClick = {})
            RadioButton(selected = false, onClick = {})
        }
    }
}

@Composable
private fun SlidersSection() {
    Column {
        Text(
            text = "Sliders",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(value = 0.5f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ProgressIndicatorsSection() {
    Column {
        Text(
            text = "Progress Indicators",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(progress = { 0.7f }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator()
            CircularProgressIndicator(progress = { 0.7f })
        }
    }
}

@Composable
private fun TextFieldsSection() {
    Column {
        Text(
            text = "Text Fields",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = "Filled Text Field", onValueChange = {
        }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = "Outlined Text Field",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ColorPaletteSection() {
    Column {
        Text(
            text = "Color Palette",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        with(MaterialTheme.colorScheme) {
            ColorPaletteItem("Primary", primary, onPrimary)
            ColorPaletteItem("Primary Container", primaryContainer, onPrimaryContainer)
            ColorPaletteItem("Secondary", secondary, onSecondary)
            ColorPaletteItem("Secondary Container", secondaryContainer, onSecondaryContainer)
            ColorPaletteItem("Tertiary", tertiary, onTertiary)
            ColorPaletteItem("Tertiary Container", tertiaryContainer, onTertiaryContainer)
            ColorPaletteItem("Error", error, onError)
            ColorPaletteItem("Error Container", errorContainer, onErrorContainer)
            ColorPaletteItem("Surface", surface, onSurface)
            ColorPaletteItem("Surface Variant", surfaceVariant, onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ColorPaletteItem(name: String, backgroundColor: Color, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(backgroundColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = backgroundColor.toString().substringAfter("(").substringBefore(")"),
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
