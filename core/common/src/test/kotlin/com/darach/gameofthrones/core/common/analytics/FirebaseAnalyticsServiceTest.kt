package com.darach.gameofthrones.core.common.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class FirebaseAnalyticsServiceTest {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var analyticsService: FirebaseAnalyticsService

    @Before
    fun setup() {
        firebaseAnalytics = mockk(relaxed = true)
        analyticsService = FirebaseAnalyticsService(firebaseAnalytics)
    }

    @Test
    fun `setUserProperty should set user property with correct values`() {
        // Given
        val propertyName = UserProperties.THEME_PREFERENCE
        val propertyValue = "DARK"

        // When
        analyticsService.setUserProperty(propertyName, propertyValue)

        // Then
        verify {
            firebaseAnalytics.setUserProperty(propertyName, propertyValue)
        }
    }

    @Test
    fun `setUserId should set user id`() {
        // Given
        val userId = "user123"

        // When
        analyticsService.setUserId(userId)

        // Then
        verify {
            firebaseAnalytics.setUserId(userId)
        }
    }

    @Test
    fun `setUserId should clear user id when null`() {
        // When
        analyticsService.setUserId(null)

        // Then
        verify {
            firebaseAnalytics.setUserId(null)
        }
    }
}
