package com.darach.gameofthrones.accessibility

import androidx.compose.ui.graphics.Color
import com.darach.gameofthrones.core.ui.theme.backgroundDark
import com.darach.gameofthrones.core.ui.theme.backgroundLight
import com.darach.gameofthrones.core.ui.theme.errorDark
import com.darach.gameofthrones.core.ui.theme.errorLight
import com.darach.gameofthrones.core.ui.theme.onBackgroundDark
import com.darach.gameofthrones.core.ui.theme.onBackgroundLight
import com.darach.gameofthrones.core.ui.theme.onErrorDark
import com.darach.gameofthrones.core.ui.theme.onErrorLight
import com.darach.gameofthrones.core.ui.theme.onPrimaryContainerDark
import com.darach.gameofthrones.core.ui.theme.onPrimaryContainerLight
import com.darach.gameofthrones.core.ui.theme.onPrimaryDark
import com.darach.gameofthrones.core.ui.theme.onPrimaryLight
import com.darach.gameofthrones.core.ui.theme.onSecondaryDark
import com.darach.gameofthrones.core.ui.theme.onSecondaryLight
import com.darach.gameofthrones.core.ui.theme.onSurfaceDark
import com.darach.gameofthrones.core.ui.theme.onSurfaceLight
import com.darach.gameofthrones.core.ui.theme.onSurfaceVariantDark
import com.darach.gameofthrones.core.ui.theme.onSurfaceVariantLight
import com.darach.gameofthrones.core.ui.theme.primaryContainerDark
import com.darach.gameofthrones.core.ui.theme.primaryContainerLight
import com.darach.gameofthrones.core.ui.theme.primaryDark
import com.darach.gameofthrones.core.ui.theme.primaryLight
import com.darach.gameofthrones.core.ui.theme.secondaryDark
import com.darach.gameofthrones.core.ui.theme.secondaryLight
import com.darach.gameofthrones.core.ui.theme.surfaceDark
import com.darach.gameofthrones.core.ui.theme.surfaceLight
import com.darach.gameofthrones.core.ui.theme.surfaceVariantDark
import com.darach.gameofthrones.core.ui.theme.surfaceVariantLight
import kotlin.math.pow
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests to verify color contrast ratios meet WCAG AA standards.
 *
 * WCAG AA Requirements:
 * - Normal text (< 18pt): Minimum contrast ratio of 4.5:1
 * - Large text (>= 18pt or bold >= 14pt): Minimum contrast ratio of 3:1
 * - UI components and graphical objects: Minimum contrast ratio of 3:1
 */
class ContrastRatioTest {

    /**
     * Calculates the contrast ratio between two colors according to WCAG standards.
     * Formula: (L1 + 0.05) / (L2 + 0.05), where L1 is the lighter color's relative luminance
     */
    private fun calculateContrastRatio(color1: Color, color2: Color): Double {
        val l1 = calculateRelativeLuminance(color1)
        val l2 = calculateRelativeLuminance(color2)

        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)

        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Calculates relative luminance according to WCAG formula
     */
    private fun calculateRelativeLuminance(color: Color): Double {
        val r = calculateChannelLuminance(color.red)
        val g = calculateChannelLuminance(color.green)
        val b = calculateChannelLuminance(color.blue)

        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun calculateChannelLuminance(channel: Float): Double = if (channel <= 0.03928) {
        (channel / 12.92).toDouble()
    } else {
        ((channel + 0.055) / 1.055).pow(2.4)
    }

    @Test
    fun lightTheme_primaryOnPrimary_meetsAAStandard() {
        val ratio = calculateContrastRatio(primaryLight, onPrimaryLight)
        assertTrue(
            "Primary/OnPrimary contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun lightTheme_secondaryOnSecondary_meetsAAStandard() {
        val ratio = calculateContrastRatio(secondaryLight, onSecondaryLight)
        assertTrue(
            "Secondary/OnSecondary contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun lightTheme_surfaceOnSurface_meetsAAStandard() {
        val ratio = calculateContrastRatio(surfaceLight, onSurfaceLight)
        assertTrue(
            "Surface/OnSurface contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun lightTheme_backgroundOnBackground_meetsAAStandard() {
        val ratio = calculateContrastRatio(backgroundLight, onBackgroundLight)
        assertTrue(
            "Background/OnBackground contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun lightTheme_errorOnError_meetsAAStandard() {
        val ratio = calculateContrastRatio(errorLight, onErrorLight)
        assertTrue(
            "Error/OnError contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun darkTheme_primaryOnPrimary_meetsAAStandard() {
        val ratio = calculateContrastRatio(primaryDark, onPrimaryDark)
        assertTrue(
            "Dark Primary/OnPrimary contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun darkTheme_secondaryOnSecondary_meetsAAStandard() {
        val ratio = calculateContrastRatio(secondaryDark, onSecondaryDark)
        assertTrue(
            "Dark Secondary/OnSecondary contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun darkTheme_surfaceOnSurface_meetsAAStandard() {
        val ratio = calculateContrastRatio(surfaceDark, onSurfaceDark)
        assertTrue(
            "Dark Surface/OnSurface contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun darkTheme_backgroundOnBackground_meetsAAStandard() {
        val ratio = calculateContrastRatio(backgroundDark, onBackgroundDark)
        assertTrue(
            "Dark Background/OnBackground contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun darkTheme_errorOnError_meetsAAStandard() {
        val ratio = calculateContrastRatio(errorDark, onErrorDark)
        assertTrue(
            "Dark Error/OnError contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun lightTheme_primaryContainerOnPrimaryContainer_meetsLargeTextStandard() {
        val ratio = calculateContrastRatio(primaryContainerLight, onPrimaryContainerLight)
        assertTrue(
            "PrimaryContainer/OnPrimaryContainer contrast ratio $ratio should be >= 3.0 for large text",
            ratio >= 3.0
        )
    }

    @Test
    fun darkTheme_primaryContainerOnPrimaryContainer_meetsLargeTextStandard() {
        val ratio = calculateContrastRatio(primaryContainerDark, onPrimaryContainerDark)
        assertTrue(
            "Dark PrimaryContainer/OnPrimaryContainer contrast ratio $ratio should be >= 3.0 for large text",
            ratio >= 3.0
        )
    }

    @Test
    fun lightTheme_surfaceVariantOnSurfaceVariant_meetsAAStandard() {
        val ratio = calculateContrastRatio(surfaceVariantLight, onSurfaceVariantLight)
        assertTrue(
            "SurfaceVariant/OnSurfaceVariant contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }

    @Test
    fun darkTheme_surfaceVariantOnSurfaceVariant_meetsAAStandard() {
        val ratio = calculateContrastRatio(surfaceVariantDark, onSurfaceVariantDark)
        assertTrue(
            "Dark SurfaceVariant/OnSurfaceVariant contrast ratio $ratio should be >= 4.5 for normal text",
            ratio >= 4.5
        )
    }
}
