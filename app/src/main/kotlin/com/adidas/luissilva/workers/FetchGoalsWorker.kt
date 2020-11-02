package com.adidas.luissilva.workers

import android.content.Context
import androidx.work.*
import com.adidas.luissilva.data.common.CallResult
import com.adidas.luissilva.data.repository.goals.GoalsRepository
import com.adidas.luissilva.utils.manager.PreferencesManager
import dagger.android.HasAndroidInjector
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FetchGoalsWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {


    companion object {
        const val KEY_RESULT = "result"
        const val TAG_GOALSWORKER = "TAG_GOALSWORKER"

        private val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        fun startUniqueUITask(ctx: Context) : UUID {
            val worker = OneTimeWorkRequestBuilder<FetchGoalsWorker>()
                    .setInputData(Data.Builder().putBoolean("Retry", false).build())
                    .build()
            WorkManager.getInstance(ctx).enqueueUniqueWork(
                    TAG_GOALSWORKER,
                    ExistingWorkPolicy.REPLACE,
                    worker

            )
            return worker.id
        }

        fun startUniqueGlobalTask(ctx: Context) : UUID {
            val worker = OneTimeWorkRequestBuilder<FetchGoalsWorker>()
                    .setConstraints(constraints)
                    .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            30,
                            TimeUnit.MINUTES
                    )
                    .setInputData(Data.Builder().putBoolean("Retry", true).build())
                    .build()
            WorkManager.getInstance(ctx).enqueueUniqueWork(
                    TAG_GOALSWORKER,
                    ExistingWorkPolicy.KEEP,
                    worker

            )
            return worker.id
        }


        fun getProgressResult(workInfo: WorkInfo): CallResult<*>? {
           return PreferencesManager.getObject(workInfo.outputData.keyValueMap[KEY_RESULT] as String?) as CallResult<*>?
        }

    }

    init {
        val injector = context.applicationContext as HasAndroidInjector
        injector.androidInjector().inject(this)
    }

    @Inject
    lateinit var goalsRepository: GoalsRepository


    override fun doWork(): Result {
        val result = goalsRepository.fetchGoalsSync()

        return if (result.status.isSuccess) {
            Result.success()
        } else {
            if(inputData.getBoolean("Retry", false)){
                if (runAttemptCount < 10) {
                    Result.retry()
                } else {
                    Result.failure(Data.Builder().putString(KEY_RESULT, PreferencesManager.getJson(result)).build())
                }
            } else {
                Result.failure(Data.Builder().putString(KEY_RESULT, PreferencesManager.getJson(result)).build())
            }

        }
    }

}