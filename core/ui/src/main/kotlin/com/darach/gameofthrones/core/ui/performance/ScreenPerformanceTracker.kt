package com.darach.gameofthrones.core.ui.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.darach.gameofthrones.core.common.performance.PerformanceMonitor
import com.darach.gameofthrones.core.common.performance.Trace

/**
 * Tracks the rendering performance of a screen.
 * Starts a trace when the composable enters the composition and stops it when it leaves.
 *
 * @param screenName The name of the screen to track.
 * @param performanceMonitor The performance monitor instance.
 */
@Composable
fun TrackScreenPerformance(screenName: String, performanceMonitor: PerformanceMonitor) {
    val trace = remember(screenName) {
        performanceMonitor.startTrace("screen_$screenName")
    }

    DisposableEffect(screenName) {
        onDispose {
            trace.stop()
        }
    }
}

/**
 * Tracks the initial load time of a screen.
 * Records the time from when the composable enters composition until a key is provided.
 *
 * @param screenName The name of the screen to track.
 * @param performanceMonitor The performance monitor instance.
 * @param key A key that indicates when the screen has finished loading.
 */
@Composable
fun TrackScreenLoadTime(screenName: String, performanceMonitor: PerformanceMonitor, key: Any?) {
    val startTime = remember(screenName) { System.currentTimeMillis() }

    LaunchedEffect(key) {
        if (key != null) {
            val loadTime = System.currentTimeMillis() - startTime
            performanceMonitor.recordScreenRenderTime(screenName, loadTime)
        }
    }
}

/**
 * Creates a custom trace for a specific operation within a screen.
 *
 * @param traceName The name of the trace.
 * @param performanceMonitor The performance monitor instance.
 * @return A [Trace] object that can be used to add metrics and stop the trace.
 */
@Composable
fun rememberTrace(traceName: String, performanceMonitor: PerformanceMonitor): Trace {
    val trace = remember(traceName) {
        performanceMonitor.startTrace(traceName)
    }

    DisposableEffect(traceName) {
        onDispose {
            trace.stop()
        }
    }

    return trace
}
