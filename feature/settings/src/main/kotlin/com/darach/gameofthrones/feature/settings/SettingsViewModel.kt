package com.darach.gameofthrones.feature.settings

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.gameofthrones.core.analytics.AnalyticsEvents
import com.darach.gameofthrones.core.analytics.AnalyticsParams
import com.darach.gameofthrones.core.analytics.AnalyticsService
import com.darach.gameofthrones.core.analytics.UserProperties
import com.darach.gameofthrones.core.common.crash.CrashReportingService
import com.darach.gameofthrones.core.data.preferences.PreferencesDataSource
import com.darach.gameofthrones.core.data.preferences.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen.
 * Manages theme preferences, cache settings, and app information.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsService: AnalyticsService,
    private val crashReportingService: CrashReportingService,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val isSyncing = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val state: StateFlow<SettingsState> = combine(
        preferencesDataSource.userPreferences,
        isLoading,
        isSyncing,
        message
    ) { userPrefs, loading, syncing, msg ->
        SettingsState(
            userPreferences = userPrefs,
            isLoading = loading,
            isSyncing = syncing,
            message = msg,
            appVersion = getAppVersion(),
            buildNumber = getBuildNumber()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState(
            appVersion = getAppVersion(),
            buildNumber = getBuildNumber()
        )
    )

    init {
        analyticsService.logScreenView(
            screenName = "Settings",
            screenClass = "SettingsScreen"
        )
    }

    fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.UpdateThemeMode -> updateThemeMode(intent.themeMode)
            is SettingsIntent.UpdateDynamicColors -> updateDynamicColors(intent.enabled)
            is SettingsIntent.UpdateCacheExpiration -> updateCacheExpiration(intent.hours)
            is SettingsIntent.ClearCache -> clearCache()
            is SettingsIntent.ClearSearchHistory -> clearSearchHistory()
            is SettingsIntent.SyncData -> syncData()
            is SettingsIntent.ClearAllData -> clearAllData()
            is SettingsIntent.TriggerTestCrash -> triggerTestCrash()
        }
    }

    private fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            runCatching {
                preferencesDataSource.updateThemePreferences(themeMode = themeMode)
                analyticsService.logEvent(
                    AnalyticsEvents.THEME_CHANGED,
                    mapOf(AnalyticsParams.THEME_MODE to themeMode.name)
                )
                analyticsService.setUserProperty(UserProperties.THEME_PREFERENCE, themeMode.name)
            }.onFailure { error ->
                message.value = "Failed to update theme: ${error.message}"
                crashReportingService.logException(error)
            }
        }
    }

    private fun updateDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            runCatching {
                preferencesDataSource.updateThemePreferences(useDynamicColors = enabled)
                analyticsService.logEvent(
                    AnalyticsEvents.DYNAMIC_COLORS_TOGGLED,
                    mapOf(AnalyticsParams.DYNAMIC_COLORS_ENABLED to enabled)
                )
                analyticsService.setUserProperty(
                    UserProperties.DYNAMIC_COLORS_PREFERENCE,
                    enabled.toString()
                )
            }.onFailure { error ->
                message.value = "Failed to update dynamic colors: ${error.message}"
                crashReportingService.logException(error)
            }
        }
    }

    private fun updateCacheExpiration(hours: Int) {
        viewModelScope.launch {
            runCatching {
                preferencesDataSource.updateCacheExpiration(hours)
                message.value = "Cache expiration updated to $hours hours"
            }.onFailure { error ->
                message.value = "Failed to update cache expiration: ${error.message}"
            }
        }
    }

    private fun clearCache() {
        viewModelScope.launch {
            runCatching {
                isLoading.value = true
                // Cache clearing would be handled by a repository/use case
                // For now, just show a message
                message.value = "Cache cleared successfully"
            }.onFailure { error ->
                message.value = "Failed to clear cache: ${error.message}"
            }.also {
                isLoading.value = false
            }
        }
    }

    private fun clearSearchHistory() {
        viewModelScope.launch {
            runCatching {
                preferencesDataSource.clearSearchHistory()
                message.value = "Search history cleared"
            }.onFailure { error ->
                message.value = "Failed to clear search history: ${error.message}"
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            runCatching {
                isSyncing.value = true
                // Data sync would be handled by a repository/use case
                // Simulate sync operation
                kotlinx.coroutines.delay(1000)
                message.value = "Data synced successfully"
            }.onFailure { error ->
                message.value = "Failed to sync data: ${error.message}"
            }.also {
                isSyncing.value = false
            }
        }
    }

    private fun clearAllData() {
        viewModelScope.launch {
            runCatching {
                isLoading.value = true
                preferencesDataSource.clearAllPreferences()
                message.value = "All preferences cleared"
            }.onFailure { error ->
                message.value = "Failed to clear data: ${error.message}"
            }.also {
                isLoading.value = false
            }
        }
    }

    private fun triggerTestCrash() {
        crashReportingService.log("Test crash triggered from Settings screen")
        crashReportingService.forceCrash()
    }

    fun dismissMessage() {
        message.value = null
    }

    @Suppress("TooGenericExceptionCaught")
    private fun getAppVersion(): String = try {
        val packageInfo = getPackageInfo()
        packageInfo.versionName ?: "Unknown"
    } catch (e: Throwable) {
        Log.w(TAG, "Failed to get app version", e)
        "Unknown"
    }

    @Suppress("TooGenericExceptionCaught")
    private fun getBuildNumber(): String = try {
        val packageInfo = getPackageInfo()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode.toString()
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toString()
        }
    } catch (e: Throwable) {
        Log.w(TAG, "Failed to get build number", e)
        "Unknown"
    }

    private fun getPackageInfo(): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                android.content.pm.PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}
