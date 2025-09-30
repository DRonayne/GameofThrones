package com.darach.gameofthrones.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darach.gameofthrones.core.domain.usecase.RefreshCharactersUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker that syncs character data from the network.
 * Runs periodically to keep the local cache up to date.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val refreshCharactersUseCase: RefreshCharactersUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = refreshCharactersUseCase().fold(
        onSuccess = {
            Result.success()
        },
        onFailure = { error ->
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    )

    companion object {
        private const val MAX_RETRIES = 3
        const val WORK_NAME = "sync_characters"
    }
}
