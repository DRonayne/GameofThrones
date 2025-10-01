package com.darach.gameofthrones.core.common.di

import com.darach.gameofthrones.core.common.crash.CrashReportingService
import com.darach.gameofthrones.core.common.crash.FirebaseCrashReportingService
import com.darach.gameofthrones.core.common.performance.FirebasePerformanceMonitor
import com.darach.gameofthrones.core.common.performance.PerformanceMonitor
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    fun provideFirebasePerformance(): FirebasePerformance = FirebasePerformance.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringModule {
    @Binds
    @Singleton
    abstract fun bindCrashReportingService(
        impl: FirebaseCrashReportingService
    ): CrashReportingService

    @Binds
    @Singleton
    abstract fun bindPerformanceMonitor(impl: FirebasePerformanceMonitor): PerformanceMonitor
}
