package com.darach.gameofthrones.feature.characters.di

import com.darach.gameofthrones.core.analytics.AnalyticsService
import com.darach.gameofthrones.core.common.crash.CrashReportingService
import com.darach.gameofthrones.core.common.performance.PerformanceMonitor
import javax.inject.Inject

/**
 * Wrapper class for common services used in the characters feature.
 * This reduces constructor parameters in ViewModels.
 */
data class CharactersServiceProvider @Inject constructor(
    val analyticsService: AnalyticsService,
    val crashReportingService: CrashReportingService,
    val performanceMonitor: PerformanceMonitor
)
