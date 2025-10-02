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
import com.darach.gameofthrones.core.domain.repository.CharacterRepository
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
    private val characterRepository: CharacterRepository,
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
                characterRepository.clearCache().fold(
                    onSuccess = {
                        message.value = "Cache cleared successfully"
                        analyticsService.logEvent(
                            AnalyticsEvents.CACHE_CLEARED,
                            mapOf(AnalyticsParams.OPERATION to "clear_cache")
                        )
                    },
                    onFailure = { error ->
                        message.value = "Failed to clear cache: ${error.message}"
                        crashReportingService.logException(error)
                    }
                )
            }.onFailure { error ->
                message.value = "Failed to clear cache: ${error.message}"
                crashReportingService.logException(error)
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
                characterRepository.refreshCharacters().fold(
                    onSuccess = {
                        message.value = "Data synced successfully"
                        analyticsService.logEvent(
                            AnalyticsEvents.DATA_SYNCED,
                            mapOf(AnalyticsParams.OPERATION to "sync_data")
                        )
                    },
                    onFailure = { error ->
                        message.value = "Failed to sync data: ${error.message}"
                        crashReportingService.logException(error)
                    }
                )
            }.onFailure { error ->
                message.value = "Failed to sync data: ${error.message}"
                crashReportingService.logException(error)
            }.also {
                isSyncing.value = false
            }
        }
    }

    private fun clearAllData() {
        viewModelScope.launch {
            runCatching {
                isLoading.value = true
                // Clear both preferences and character cache
                preferencesDataSource.clearAllPreferences()
                characterRepository.clearAllData().fold(
                    onSuccess = {
                        message.value = "All data cleared successfully"
                        analyticsService.logEvent(
                            AnalyticsEvents.ALL_DATA_CLEARED,
                            mapOf(AnalyticsParams.OPERATION to "clear_all_data")
                        )
                    },
                    onFailure = { error ->
                        message.value = "Failed to clear all data: ${error.message}"
                        crashReportingService.logException(error)
                    }
                )
            }.onFailure { error ->
                message.value = "Failed to clear data: ${error.message}"
                crashReportingService.logException(error)
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
