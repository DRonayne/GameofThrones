package com.darach.gameofthrones.core.ui.adaptive

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

enum class DevicePosture {
    NORMAL,
    BOOK,
    TABLE_TOP,
    TENT,
    FLAT,
    SEPARATING_FOLD
}

data class FoldingFeatureInfo(
    val state: FoldingFeature.State,
    val orientation: FoldingFeature.Orientation,
    val isSeparating: Boolean,
    val bounds: android.graphics.Rect,
    val occlusionType: FoldingFeature.OcclusionType
)

fun WindowLayoutInfo.getDevicePosture(): DevicePosture {
    val foldingFeature = displayFeatures.firstOrNull() as? FoldingFeature

    return when {
        foldingFeature == null -> DevicePosture.NORMAL
        foldingFeature.isSeparating -> DevicePosture.SEPARATING_FOLD
        foldingFeature.orientation == FoldingFeature.Orientation.VERTICAL &&
            foldingFeature.state == FoldingFeature.State.HALF_OPENED -> DevicePosture.BOOK
        foldingFeature.orientation == FoldingFeature.Orientation.HORIZONTAL &&
            foldingFeature.state == FoldingFeature.State.HALF_OPENED -> DevicePosture.TABLE_TOP
        foldingFeature.state == FoldingFeature.State.FLAT -> DevicePosture.FLAT
        else -> DevicePosture.NORMAL
    }
}

fun WindowLayoutInfo.getFoldingFeatureInfo(): FoldingFeatureInfo? {
    val foldingFeature = displayFeatures.firstOrNull() as? FoldingFeature ?: return null

    return FoldingFeatureInfo(
        state = foldingFeature.state,
        orientation = foldingFeature.orientation,
        isSeparating = foldingFeature.isSeparating,
        bounds = foldingFeature.bounds,
        occlusionType = foldingFeature.occlusionType
    )
}

val WindowLayoutInfo.hasVerticalFold: Boolean
    get() {
        val foldingFeature = displayFeatures.firstOrNull() as? FoldingFeature
        return foldingFeature?.orientation == FoldingFeature.Orientation.VERTICAL
    }

val WindowLayoutInfo.hasHorizontalFold: Boolean
    get() {
        val foldingFeature = displayFeatures.firstOrNull() as? FoldingFeature
        return foldingFeature?.orientation == FoldingFeature.Orientation.HORIZONTAL
    }

val WindowLayoutInfo.hasSeparatingFold: Boolean
    get() {
        val foldingFeature = displayFeatures.firstOrNull() as? FoldingFeature
        return foldingFeature?.isSeparating == true
    }

@Composable
fun rememberWindowLayoutInfo(): WindowLayoutInfo {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var windowLayoutInfo by remember {
        mutableStateOf(
            WindowLayoutInfo(emptyList())
        )
    }

    DisposableEffect(context) {
        val activity = context as? Activity
        if (activity != null) {
            val windowInfoTracker = WindowInfoTracker.getOrCreate(activity)
            val job = scope.launch {
                windowInfoTracker.windowLayoutInfo(activity).collect { info ->
                    windowLayoutInfo = info
                }
            }

            onDispose {
                job.cancel()
            }
        } else {
            onDispose { }
        }
    }

    return windowLayoutInfo
}

@Composable
fun rememberDevicePosture(): DevicePosture {
    val windowLayoutInfo = rememberWindowLayoutInfo()
    return remember(windowLayoutInfo) {
        windowLayoutInfo.getDevicePosture()
    }
}

@Composable
fun rememberFoldingFeatureInfo(): FoldingFeatureInfo? {
    val windowLayoutInfo = rememberWindowLayoutInfo()
    return remember(windowLayoutInfo) {
        windowLayoutInfo.getFoldingFeatureInfo()
    }
}

fun Activity.getWindowLayoutInfoFlow(): Flow<WindowLayoutInfo> =
    WindowInfoTracker.getOrCreate(this).windowLayoutInfo(this)
