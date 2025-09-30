package com.darach.gameofthrones.core.common.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of [CrashReportingService].
 * Provides crash reporting using Firebase Crashlytics.
 */
@Singleton
class FirebaseCrashReportingService
@Inject
constructor(
    private val crashlytics: FirebaseCrashlytics
) : CrashReportingService {
    override fun logException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Long) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Float) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Double) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setUserId(identifier: String?) {
        crashlytics.setUserId(identifier ?: "")
    }

    override fun forceCrash(): Unit =
        throw TestCrashException("Test crash triggered from Crashlytics integration")
}

/**
 * Exception specifically for testing crash reporting functionality.
 * This should only be thrown in debug builds for testing purposes.
 */
class TestCrashException(message: String) : Exception(message)
