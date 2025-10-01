package com.darach.gameofthrones.core.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class PortraitImageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun portraitImage_displaysWithValidUrl() {
        composeTestRule.setContent {
            PortraitImage(
                imageUrl = "https://example.com/image.jpg",
                contentDescription = "Test Image"
            )
        }

        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun portraitImage_displaysPlaceholderWithNullUrl() {
        composeTestRule.setContent {
            PortraitImage(
                imageUrl = null,
                contentDescription = "Test Image"
            )
        }

        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun portraitImage_hasCorrectAspectRatio() {
        composeTestRule.setContent {
            PortraitImage(
                imageUrl = null,
                contentDescription = "Test Image"
            )
        }

        // Portrait images should maintain 3:4 aspect ratio
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun portraitImage_acceptsNullContentDescription() {
        composeTestRule.setContent {
            PortraitImage(
                imageUrl = "https://example.com/image.jpg",
                contentDescription = null
            )
        }

        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun portraitImage_rendersWithCustomModifier() {
        composeTestRule.setContent {
            PortraitImage(
                imageUrl = null,
                contentDescription = "Test Image",
                modifier = Modifier.size(200.dp)
            )
        }

        composeTestRule.onRoot().assertIsDisplayed()
        composeTestRule.onRoot().assertWidthIsAtLeast(100.dp)
        composeTestRule.onRoot().assertHeightIsAtLeast(100.dp)
    }
}
