package com.darach.gameofthrones.core.ui.haptics

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Performs haptic feedback using the Android View system for more reliable feedback.
 * Uses KEYBOARD_TAP which provides a light, consistent haptic response across devices.
 */
@Composable
fun rememberHapticFeedback(): () -> Unit {
    val view = LocalView.current
    return remember(view) {
        {
            view.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
            )
        }
    }
}

/**
 * Creates a remembered callback that performs haptic feedback and executes an action.
 * Useful for combining haptics with button clicks.
 */
@Composable
fun rememberHapticClickCallback(onClick: () -> Unit): () -> Unit {
    val view = LocalView.current
    return remember(view, onClick) {
        {
            view.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
            )
            onClick()
        }
    }
}
