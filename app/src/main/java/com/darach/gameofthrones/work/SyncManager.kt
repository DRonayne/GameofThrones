package com.darach.gameofthrones.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages background sync operations using WorkManager.
 * Provides methods to schedule periodic syncs and check sync status.
 */
@Singleton
class SyncManager @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedules periodic sync to run every 6 hours.
     * Requires network connectivity to run.
     */
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            SYNC_INTERVAL_HOURS,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    /**
     * Cancels all scheduled sync work.
     */
    fun cancelSync() {
        workManager.cancelUniqueWork(SyncWorker.WORK_NAME)
    }

    /**
     * Returns a Flow indicating whether a sync is currently in progress.
     */
    val isSyncing: Flow<Boolean> = workManager
        .getWorkInfosForUniqueWorkFlow(SyncWorker.WORK_NAME)
        .map { workInfos ->
            workInfos.any { it.state == WorkInfo.State.RUNNING }
        }

    companion object {
        private const val SYNC_INTERVAL_HOURS = 6L
    }
}
