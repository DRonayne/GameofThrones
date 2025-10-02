package com.darach.gameofthrones.feature.favorites

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.ui.geometry.Rect
import androidx.window.core.layout.WindowSizeClass
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class FavoritesScreenTest {

    private fun createMockWindowAdaptiveInfo(widthDp: Int): WindowAdaptiveInfo {
        val mockWindowSizeClass = mockk<WindowSizeClass>()

        // Mock the isWidthAtLeastBreakpoint method based on the widthDp
        every {
            mockWindowSizeClass.isWidthAtLeastBreakpoint(
                WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
            )
        } returns (widthDp >= WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

        every {
            mockWindowSizeClass.isWidthAtLeastBreakpoint(
                WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
            )
        } returns (widthDp >= WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        val mockWindowAdaptiveInfo = mockk<WindowAdaptiveInfo>()
        every { mockWindowAdaptiveInfo.windowSizeClass } returns mockWindowSizeClass
        every { mockWindowAdaptiveInfo.windowPosture } returns mockk()

        return mockWindowAdaptiveInfo
    }

    @Test
    fun `calculateGridColumns returns 3 columns for compact width`() {
        val windowInfo = createMockWindowAdaptiveInfo(360)
        val columns = calculateGridColumns(windowInfo)
        assertEquals(3, columns)
    }

    @Test
    fun `calculateGridColumns returns 4 columns for medium width`() {
        val windowInfo = createMockWindowAdaptiveInfo(720)
        val columns = calculateGridColumns(windowInfo)
        assertEquals(4, columns)
    }

    @Test
    fun `calculateGridColumns returns 6 columns for expanded width`() {
        val windowInfo = createMockWindowAdaptiveInfo(1024)
        val columns = calculateGridColumns(windowInfo)
        assertEquals(6, columns)
    }

    @Test
    fun `calculateGridColumns returns 3 columns at compact boundary`() {
        val windowInfo = createMockWindowAdaptiveInfo(599)
        val columns = calculateGridColumns(windowInfo)
        assertEquals(3, columns)
    }

    @Test
    fun `calculateGridColumns returns 4 columns at medium lower boundary`() {
        val windowInfo = createMockWindowAdaptiveInfo(600)
        val columns = calculateGridColumns(windowInfo)
        assertEquals(4, columns)
    }

    @Test
    fun `calculateGridColumns returns 4 columns at medium upper boundary`() {
        val windowInfo = createMockWindowAdaptiveInfo(839)
        val columns = calculateGridColumns(windowInfo)
        assertEquals(4, columns)
    }

    @Test
    fun `calculateGridColumns returns 6 columns at expanded boundary`() {
        val windowInfo = createMockWindowAdaptiveInfo(840)
        val columns = calculateGridColumns(windowInfo)
        assertEquals(6, columns)
    }
}
