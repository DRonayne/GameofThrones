package com.darach.gameofthrones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darach.gameofthrones.core.data.preferences.PreferencesDataSource
import com.darach.gameofthrones.core.data.preferences.ThemeMode
import com.darach.gameofthrones.core.data.preferences.UserPreferences
import com.darach.gameofthrones.core.ui.theme.GameOfThronesTheme
import com.darach.gameofthrones.navigation.GoTApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesDataSource: PreferencesDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userPreferences by preferencesDataSource
                .userPreferences
                .collectAsStateWithLifecycle(
                    initialValue = UserPreferences()
                )

            val darkTheme = when (userPreferences.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            GameOfThronesTheme(
                darkTheme = darkTheme,
                dynamicColor = userPreferences.useDynamicColors
            ) {
                GoTApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
