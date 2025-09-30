package com.darach.gameofthrones.core.common.di

import android.content.Context
import com.darach.gameofthrones.core.common.analytics.AnalyticsService
import com.darach.gameofthrones.core.common.analytics.FirebaseAnalyticsService
import com.darach.gameofthrones.core.common.crash.CrashReportingService
import com.darach.gameofthrones.core.common.crash.FirebaseCrashReportingService
import com.darach.gameofthrones.core.common.performance.FirebasePerformanceMonitor
import com.darach.gameofthrones.core.common.performance.PerformanceMonitor
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    fun provideFirebasePerformance(): FirebasePerformance = FirebasePerformance.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsService(impl: FirebaseAnalyticsService): AnalyticsService

    @Binds
    @Singleton
    abstract fun bindCrashReportingService(
        impl: FirebaseCrashReportingService
    ): CrashReportingService

    @Binds
    @Singleton
    abstract fun bindPerformanceMonitor(impl: FirebasePerformanceMonitor): PerformanceMonitor
}
