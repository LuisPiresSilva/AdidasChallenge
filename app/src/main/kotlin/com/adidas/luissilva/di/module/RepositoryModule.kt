package com.adidas.luissilva.di.module

import com.adidas.luissilva.data.repository.goals.GoalsRepository
import com.adidas.luissilva.data.repository.goals.GoalsRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindsGoalsRepository(goalsRepositoryImpl: GoalsRepositoryImpl): GoalsRepository

}