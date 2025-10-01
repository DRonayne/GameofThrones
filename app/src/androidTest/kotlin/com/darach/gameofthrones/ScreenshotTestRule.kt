package com.darach.gameofthrones

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit test rule that automatically captures screenshots on test failures.
 *
 * Usage:
 * ```
 * @get:Rule(order = 2)
 * val screenshotRule = ScreenshotTestRule()
 *
 * @Before
 * fun setup() {
 *     screenshotRule.setComposeTestRule(composeTestRule)
 * }
 * ```
 *
 * Screenshots are saved to: /sdcard/Pictures/test_failures/
 */
class ScreenshotTestRule : TestWatcher() {
    private var composeTestRule: ComposeTestRule? = null
    private val tag = "ScreenshotTestRule"

    fun setComposeTestRule(rule: ComposeTestRule) {
        this.composeTestRule = rule
    }

    override fun failed(e: Throwable?, description: Description?) {
        super.failed(e, description)

        val testName = description?.methodName ?: "unknown_test"
        val className = description?.className?.substringAfterLast('.') ?: "unknown_class"
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "${className}_${testName}_$timestamp.png"

        try {
            captureScreenshot(filename)
            Log.i(tag, "Screenshot saved: $filename")
            Log.i(tag, "Test failed with error: ${e?.message}")
        } catch (exception: Exception) {
            Log.e(tag, "Failed to capture screenshot", exception)
        }
    }

    private fun captureScreenshot(filename: String) {
        val rule = composeTestRule ?: run {
            Log.w(tag, "ComposeTestRule not set, cannot capture screenshot")
            return
        }

        try {
            // Capture the root composable as a bitmap
            val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()

            // Save to external storage
            val screenshotDir = File(
                InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(
                    null
                ),
                "test_failures"
            )

            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs()
            }

            val screenshotFile = File(screenshotDir, filename)
            FileOutputStream(screenshotFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            Log.i(tag, "Screenshot saved to: ${screenshotFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(tag, "Error saving screenshot", e)
        }
    }
}

/**
 * Extension function to capture a screenshot of a specific node.
 */
fun SemanticsNodeInteraction.captureScreenshot(filename: String): SemanticsNodeInteraction {
    try {
        val bitmap = this.captureToImage().asAndroidBitmap()

        val screenshotDir = File(
            InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),
            "test_screenshots"
        )

        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs()
        }

        val screenshotFile = File(screenshotDir, filename)
        FileOutputStream(screenshotFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        Log.i("Screenshot", "Screenshot saved to: ${screenshotFile.absolutePath}")
    } catch (e: Exception) {
        Log.e("Screenshot", "Error saving screenshot", e)
    }

    return this
}

/**
 * Extension function to take a screenshot from ComposeTestRule.
 */
fun ComposeTestRule.takeScreenshot(filename: String) {
    try {
        val bitmap = this.onRoot().captureToImage().asAndroidBitmap()

        val screenshotDir = File(
            InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),
            "test_screenshots"
        )

        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs()
        }

        val screenshotFile = File(screenshotDir, filename)
        FileOutputStream(screenshotFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        Log.i("Screenshot", "Screenshot saved to: ${screenshotFile.absolutePath}")
    } catch (e: Exception) {
        Log.e("Screenshot", "Error saving screenshot", e)
    }
}
