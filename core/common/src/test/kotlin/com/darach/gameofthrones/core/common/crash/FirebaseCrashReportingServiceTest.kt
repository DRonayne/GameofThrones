package com.darach.gameofthrones.core.common.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class FirebaseCrashReportingServiceTest {
    private lateinit var crashlytics: FirebaseCrashlytics
    private lateinit var crashReportingService: FirebaseCrashReportingService

    @Before
    fun setup() {
        crashlytics = mockk(relaxed = true)
        crashReportingService = FirebaseCrashReportingService(crashlytics)
    }

    @Test
    fun `logException should record exception`() {
        // Given
        val exception = RuntimeException("Test exception")

        // When
        crashReportingService.logException(exception)

        // Then
        verify {
            crashlytics.recordException(exception)
        }
    }

    @Test
    fun `log should log message`() {
        // Given
        val message = "Test log message"

        // When
        crashReportingService.log(message)

        // Then
        verify {
            crashlytics.log(message)
        }
    }

    @Test
    fun `setCustomKey should set string key`() {
        // Given
        val key = CrashKeys.SCREEN_NAME
        val value = "CharactersScreen"

        // When
        crashReportingService.setCustomKey(key, value)

        // Then
        verify {
            crashlytics.setCustomKey(key, value)
        }
    }

    @Test
    fun `setCustomKey should set boolean key`() {
        // Given
        val key = "is_premium"
        val value = true

        // When
        crashReportingService.setCustomKey(key, value)

        // Then
        verify {
            crashlytics.setCustomKey(key, value)
        }
    }

    @Test
    fun `setCustomKey should set int key`() {
        // Given
        val key = "retry_count"
        val value = 3

        // When
        crashReportingService.setCustomKey(key, value)

        // Then
        verify {
            crashlytics.setCustomKey(key, value)
        }
    }

    @Test
    fun `setCustomKey should set long key`() {
        // Given
        val key = "timestamp"
        val value = 1234567890L

        // When
        crashReportingService.setCustomKey(key, value)

        // Then
        verify {
            crashlytics.setCustomKey(key, value)
        }
    }

    @Test
    fun `setCustomKey should set float key`() {
        // Given
        val key = "rating"
        val value = 4.5f

        // When
        crashReportingService.setCustomKey(key, value)

        // Then
        verify {
            crashlytics.setCustomKey(key, value)
        }
    }

    @Test
    fun `setCustomKey should set double key`() {
        // Given
        val key = "latitude"
        val value = 51.5074

        // When
        crashReportingService.setCustomKey(key, value)

        // Then
        verify {
            crashlytics.setCustomKey(key, value)
        }
    }

    @Test
    fun `setUserId should set user id`() {
        // Given
        val userId = "user123"

        // When
        crashReportingService.setUserId(userId)

        // Then
        verify {
            crashlytics.setUserId(userId)
        }
    }

    @Test
    fun `setUserId should set empty string when null`() {
        // When
        crashReportingService.setUserId(null)

        // Then
        verify {
            crashlytics.setUserId("")
        }
    }

    @Test(expected = TestCrashException::class)
    fun `forceCrash should throw test crash exception`() {
        // When
        crashReportingService.forceCrash()

        // Then - exception is thrown
    }
}
