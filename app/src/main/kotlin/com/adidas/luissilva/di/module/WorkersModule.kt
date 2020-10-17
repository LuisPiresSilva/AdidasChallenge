package com.adidas.luissilva.di.module

import com.adidas.luissilva.workers.FetchGoalsWorker
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class WorkersModule {

    @ContributesAndroidInjector
    abstract fun workerFetchGoals(): FetchGoalsWorker



}