package com.darach.gameofthrones.feature.settings

import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.core.data.preferences.ThemeMode
import com.darach.gameofthrones.core.data.preferences.UserPreferences
import com.darach.gameofthrones.core.ui.test.TestTags
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme

/**
 * Settings screen with theme management, cache controls, and app information.
 */
@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    SettingsContent(
        state = state,
        onIntent = { viewModel.handleIntent(it) },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@VisibleForTesting
@Composable
internal fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag(TestTags.SETTINGS_CONTENT)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = "Theme", icon = Icons.Default.Palette) {
                ThemeSettingsContent(
                    themeMode = state.themeMode,
                    useDynamicColors = state.useDynamicColors,
                    onThemeModeChange = { onIntent(SettingsIntent.UpdateThemeMode(it)) },
                    onDynamicColorsChange = { onIntent(SettingsIntent.UpdateDynamicColors(it)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(title = "Cache Management", icon = Icons.Default.Storage) {
                CacheSettingsContent(
                    cacheExpirationHours = state.cacheExpirationHours,
                    onClearCache = { onIntent(SettingsIntent.ClearCache) },
                    isLoading = state.isLoading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(title = "Data Sync", icon = Icons.Default.Sync) {
                DataSyncContent(
                    searchHistorySize = state.searchHistorySize,
                    onSyncData = { onIntent(SettingsIntent.SyncData) },
                    onClearSearchHistory = { onIntent(SettingsIntent.ClearSearchHistory) },
                    onClearAllData = { onIntent(SettingsIntent.ClearAllData) },
                    isSyncing = state.isSyncing
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsSection(title = "About", icon = Icons.Default.Info) {
                AboutContent(appVersion = state.appVersion, buildNumber = state.buildNumber)
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun ThemeSettingsContent(
    themeMode: ThemeMode,
    useDynamicColors: Boolean,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDynamicColorsChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Theme Mode",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        ThemeModeOption(
            title = "Light",
            subtitle = "Always use light theme",
            icon = Icons.Default.Brightness7,
            selected = themeMode == ThemeMode.LIGHT,
            onClick = { onThemeModeChange(ThemeMode.LIGHT) }
        )

        ThemeModeOption(
            title = "Dark",
            subtitle = "Always use dark theme",
            icon = Icons.Default.Brightness4,
            selected = themeMode == ThemeMode.DARK,
            onClick = { onThemeModeChange(ThemeMode.DARK) }
        )

        ThemeModeOption(
            title = "System Default",
            subtitle = "Follow system theme",
            icon = Icons.Default.BrightnessAuto,
            selected = themeMode == ThemeMode.SYSTEM,
            onClick = { onThemeModeChange(ThemeMode.SYSTEM) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SettingsToggle(
                title = "Dynamic Colors",
                subtitle = "Use Material You dynamic colors",
                checked = useDynamicColors,
                onCheckedChange = onDynamicColorsChange
            )
        } else {
            Text(
                text = "Dynamic colors require Android 12+",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ThemeModeOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

@Composable
private fun CacheSettingsContent(
    cacheExpirationHours: Int,
    onClearCache: () -> Unit,
    isLoading: Boolean
) {
    var showClearDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingsItem(
            title = "Cache Expiration",
            subtitle = "$cacheExpirationHours hours",
            icon = Icons.Default.Refresh
        )

        AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        SettingsButtonContent(
            config = SettingsButtonConfig(
                title = "Clear Cache",
                subtitle = "Remove all cached data",
                icon = Icons.Default.Clear,
                onClick = { showClearDialog = true },
                enabled = !isLoading
            )
        )
    }

    if (showClearDialog) {
        ConfirmationDialog(
            title = "Clear Cache?",
            message = "This will remove all cached character data. " +
                "The data will be re-downloaded when needed.",
            onConfirm = {
                onClearCache()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }
}

@Composable
private fun DataSyncContent(
    searchHistorySize: Int,
    onSyncData: () -> Unit,
    onClearSearchHistory: () -> Unit,
    onClearAllData: () -> Unit,
    isSyncing: Boolean
) {
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    DataSyncActions(
        searchHistorySize = searchHistorySize,
        onSyncData = onSyncData,
        onShowClearHistoryDialog = { showClearHistoryDialog = true },
        onShowClearAllDialog = { showClearAllDialog = true },
        isSyncing = isSyncing
    )

    DataSyncDialogs(
        showClearHistoryDialog = showClearHistoryDialog,
        showClearAllDialog = showClearAllDialog,
        onClearSearchHistory = onClearSearchHistory,
        onClearAllData = onClearAllData,
        onDismissDialogs = { clearHistory, clearAll ->
            showClearHistoryDialog = clearHistory
            showClearAllDialog = clearAll
        }
    )
}

@Composable
private fun DataSyncActions(
    searchHistorySize: Int,
    onSyncData: () -> Unit,
    onShowClearHistoryDialog: () -> Unit,
    onShowClearAllDialog: () -> Unit,
    isSyncing: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingsButtonContent(
            config = SettingsButtonConfig(
                title = "Sync Data",
                subtitle = "Refresh all data from server",
                icon = Icons.Default.Sync,
                onClick = onSyncData,
                enabled = !isSyncing,
                testTag = TestTags.SYNC_DATA_BUTTON
            ),
            trailing = { if (isSyncing) SyncingIndicator() }
        )

        SettingsItem(
            title = "Search History",
            subtitle = "$searchHistorySize items",
            icon = Icons.Default.Storage
        )

        SettingsButtonContent(
            config = SettingsButtonConfig(
                title = "Clear Search History",
                subtitle = "Remove all search queries",
                icon = Icons.Default.Clear,
                onClick = onShowClearHistoryDialog
            )
        )

        HorizontalDivider()

        SettingsButtonContent(
            config = SettingsButtonConfig(
                title = "Clear All Data",
                subtitle = "Reset all preferences and clear cache",
                icon = Icons.Default.Delete,
                onClick = onShowClearAllDialog,
                destructive = true,
                testTag = TestTags.CLEAR_CACHE_BUTTON
            )
        )
    }
}

@Composable
private fun DataSyncDialogs(
    showClearHistoryDialog: Boolean,
    showClearAllDialog: Boolean,
    onClearSearchHistory: () -> Unit,
    onClearAllData: () -> Unit,
    onDismissDialogs: (Boolean, Boolean) -> Unit
) {
    if (showClearHistoryDialog) {
        ClearHistoryDialog(
            onConfirm = onClearSearchHistory,
            onDismiss = { onDismissDialogs(false, showClearAllDialog) }
        )
    }

    if (showClearAllDialog) {
        ClearAllDataDialog(
            onConfirm = onClearAllData,
            onDismiss = { onDismissDialogs(showClearHistoryDialog, false) }
        )
    }
}

@Composable
private fun SyncingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .width(24.dp)
            .height(24.dp)
    )
}

@Composable
private fun AboutContent(appVersion: String, buildNumber: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SettingsItem(
            title = "App Version",
            subtitle = appVersion,
            icon = Icons.Default.Info
        )

        SettingsItem(
            title = "Build Number",
            subtitle = buildNumber,
            icon = Icons.Default.Info
        )

        Text(
            text = "Game of Thrones Character Encyclopedia",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Built with Jetpack Compose and Material 3",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class SettingsButtonConfig(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
    val testTag: String? = null
)

@Composable
private fun SettingsButtonContent(
    config: SettingsButtonConfig,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(config.testTag?.let { Modifier.testTag(it) } ?: Modifier)
            .clickable(enabled = config.enabled, onClick = config.onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = config.icon,
            contentDescription = null,
            tint = when {
                !config.enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                config.destructive -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.bodyLarge,
                color = when {
                    !config.enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    config.destructive -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = config.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (config.enabled) 1f else 0.38f
                )
            )
        }

        trailing?.invoke()
    }
}

// Previews
@androidx.compose.ui.tooling.preview.Preview(
    name = "Settings Content - Light Mode",
    showBackground = true
)
@Composable
private fun SettingsContentPreview() {
    GameOfThronesTheme {
        SettingsContent(
            state = SettingsState(
                userPreferences = UserPreferences(
                    themeMode = ThemeMode.SYSTEM,
                    useDynamicColors = true,
                    cacheExpirationHours = 24,
                    searchHistory = List(15) { "Search $it" }
                ),
                appVersion = "1.0.0",
                buildNumber = "42",
                isLoading = false,
                isSyncing = false,
                message = null
            ),
            onIntent = {},
            snackbarHostState = androidx.compose.material3.SnackbarHostState()
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Settings Content - Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SettingsContentDarkPreview() {
    GameOfThronesTheme {
        SettingsContent(
            state = SettingsState(
                userPreferences = UserPreferences(
                    themeMode = ThemeMode.DARK,
                    useDynamicColors = false,
                    cacheExpirationHours = 48,
                    searchHistory = List(25) { "Search $it" }
                ),
                appVersion = "1.0.0",
                buildNumber = "42",
                isLoading = false,
                isSyncing = false,
                message = null
            ),
            onIntent = {},
            snackbarHostState = androidx.compose.material3.SnackbarHostState()
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Settings Content - Syncing",
    showBackground = true
)
@Composable
private fun SettingsContentSyncingPreview() {
    GameOfThronesTheme {
        SettingsContent(
            state = SettingsState(
                userPreferences = UserPreferences(
                    themeMode = ThemeMode.LIGHT,
                    useDynamicColors = true,
                    cacheExpirationHours = 24,
                    searchHistory = List(10) { "Search $it" }
                ),
                appVersion = "1.0.0",
                buildNumber = "42",
                isLoading = false,
                isSyncing = true,
                message = null
            ),
            onIntent = {},
            snackbarHostState = androidx.compose.material3.SnackbarHostState()
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Settings Content - Tablet",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=240"
)
@Composable
private fun SettingsContentTabletPreview() {
    GameOfThronesTheme {
        SettingsContent(
            state = SettingsState(
                userPreferences = UserPreferences(
                    themeMode = ThemeMode.SYSTEM,
                    useDynamicColors = true,
                    cacheExpirationHours = 24,
                    searchHistory = List(30) { "Search $it" }
                ),
                appVersion = "1.0.0",
                buildNumber = "42",
                isLoading = false,
                isSyncing = false,
                message = null
            ),
            onIntent = {},
            snackbarHostState = androidx.compose.material3.SnackbarHostState()
        )
    }
}
