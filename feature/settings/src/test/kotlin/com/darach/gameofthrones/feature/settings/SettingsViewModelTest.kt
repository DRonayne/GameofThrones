package com.darach.gameofthrones.feature.settings

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import app.cash.turbine.test
import com.darach.gameofthrones.core.data.preferences.PreferencesDataSource
import com.darach.gameofthrones.core.data.preferences.ThemeMode
import com.darach.gameofthrones.core.data.preferences.UserPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var preferencesDataSource: PreferencesDataSource
    private lateinit var context: Context
    private lateinit var packageManager: PackageManager

    private val testDispatcher = StandardTestDispatcher()

    private val defaultPreferences = UserPreferences(
        themeMode = ThemeMode.SYSTEM,
        useDynamicColors = true,
        cacheExpirationHours = 24,
        searchHistory = emptyList()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Android Log
        io.mockk.mockkStatic(android.util.Log::class)
        every { android.util.Log.w(any(), any<String>(), any<Throwable>()) } returns 0

        preferencesDataSource = mockk(relaxed = true)
        context = mockk(relaxed = true)
        packageManager = mockk(relaxed = true)

        // Mock package info
        val packageInfo = PackageInfo().apply {
            versionName = "1.0.0"
            @Suppress("DEPRECATION")
            versionCode = 1
        }

        every { context.packageManager } returns packageManager
        every { context.packageName } returns "com.darach.gameofthrones"
        every { context.applicationInfo } returns ApplicationInfo()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            every {
                packageManager.getPackageInfo(
                    any<String>(),
                    any<PackageManager.PackageInfoFlags>()
                )
            } returns packageInfo
        } else {
            @Suppress("DEPRECATION")
            every {
                packageManager.getPackageInfo(any<String>(), any<Int>())
            } returns packageInfo
        }

        every { preferencesDataSource.userPreferences } returns flowOf(defaultPreferences)

        viewModel = SettingsViewModel(preferencesDataSource, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains default preferences`() = runTest {
        viewModel.state.test {
            val state = awaitItem()

            assertEquals(ThemeMode.SYSTEM, state.themeMode)
            assertTrue(state.useDynamicColors)
            assertEquals(24, state.cacheExpirationHours)
            assertEquals(0, state.searchHistorySize)
            assertFalse(state.isLoading)
            assertFalse(state.isSyncing)
            assertNull(state.message)
        }
    }

    @Test
    fun `handleIntent UpdateThemeMode updates theme mode`() = runTest {
        coEvery {
            preferencesDataSource.updateThemePreferences(themeMode = ThemeMode.DARK)
        } returns Unit

        viewModel.handleIntent(SettingsIntent.UpdateThemeMode(ThemeMode.DARK))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            preferencesDataSource.updateThemePreferences(themeMode = ThemeMode.DARK)
        }
    }

    @Test
    fun `handleIntent UpdateDynamicColors updates dynamic colors`() = runTest {
        coEvery {
            preferencesDataSource.updateThemePreferences(useDynamicColors = false)
        } returns Unit

        viewModel.handleIntent(SettingsIntent.UpdateDynamicColors(false))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            preferencesDataSource.updateThemePreferences(useDynamicColors = false)
        }
    }

    @Test
    fun `handleIntent UpdateCacheExpiration updates cache expiration`() = runTest {
        coEvery {
            preferencesDataSource.updateCacheExpiration(48)
        } returns Unit

        viewModel.handleIntent(SettingsIntent.UpdateCacheExpiration(48))
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            preferencesDataSource.updateCacheExpiration(48)
        }
    }

    @Test
    fun `handleIntent ClearCache shows success message`() = runTest {
        viewModel.state.test {
            skipItems(1) // Skip initial state

            viewModel.handleIntent(SettingsIntent.ClearCache)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertNotNull(state.message)
            assertTrue(state.message!!.contains("cleared"))
        }
    }

    @Test
    fun `handleIntent ClearSearchHistory clears search history`() = runTest {
        coEvery {
            preferencesDataSource.clearSearchHistory()
        } returns Unit

        viewModel.handleIntent(SettingsIntent.ClearSearchHistory)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            preferencesDataSource.clearSearchHistory()
        }
    }

    @Test
    fun `handleIntent SyncData shows syncing state`() = runTest {
        viewModel.state.test {
            skipItems(1) // Skip initial state

            viewModel.handleIntent(SettingsIntent.SyncData)
            testDispatcher.scheduler.advanceTimeBy(500)

            // Should be syncing
            val syncingState = expectMostRecentItem()
            assertTrue(syncingState.isSyncing)

            testDispatcher.scheduler.advanceUntilIdle()

            // Should complete syncing
            val completedState = expectMostRecentItem()
            assertFalse(completedState.isSyncing)
            assertNotNull(completedState.message)
        }
    }

    @Test
    fun `handleIntent ClearAllData clears all preferences`() = runTest {
        coEvery {
            preferencesDataSource.clearAllPreferences()
        } returns Unit

        viewModel.handleIntent(SettingsIntent.ClearAllData)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            preferencesDataSource.clearAllPreferences()
        }
    }

    @Test
    fun `dismissMessage clears message`() = runTest {
        viewModel.state.test {
            skipItems(1) // Skip initial state

            // Trigger an action that sets a message
            viewModel.handleIntent(SettingsIntent.ClearCache)
            testDispatcher.scheduler.advanceUntilIdle()

            val stateWithMessage = expectMostRecentItem()
            assertNotNull(stateWithMessage.message)

            // Dismiss message
            viewModel.dismissMessage()
            testDispatcher.scheduler.advanceUntilIdle()

            val stateWithoutMessage = expectMostRecentItem()
            assertNull(stateWithoutMessage.message)
        }
    }


    @Test
    fun `error during theme update shows error message`() = runTest {
        coEvery {
            preferencesDataSource.updateThemePreferences(themeMode = ThemeMode.DARK)
        } throws Exception("Test error")

        viewModel.state.test {
            skipItems(1) // Skip initial state

            viewModel.handleIntent(SettingsIntent.UpdateThemeMode(ThemeMode.DARK))
            testDispatcher.scheduler.advanceUntilIdle()

            val state = expectMostRecentItem()
            assertNotNull(state.message)
            assertTrue(state.message!!.contains("Failed"))
        }
    }

    @Test
    fun `app version is retrieved from package info`() = runTest {
        viewModel.state.test {
            val state = awaitItem()

            assertEquals("1.0.0", state.appVersion)
            assertEquals("1", state.buildNumber)
        }
    }

    @Test
    fun `handles package info retrieval error gracefully`() = runTest {
        every { context.packageManager } throws RuntimeException("Test error")

        val errorViewModel = SettingsViewModel(preferencesDataSource, context)
        testDispatcher.scheduler.advanceUntilIdle()

        errorViewModel.state.test {
            val state = awaitItem()

            assertEquals("Unknown", state.appVersion)
            assertEquals("Unknown", state.buildNumber)
        }
    }
}
