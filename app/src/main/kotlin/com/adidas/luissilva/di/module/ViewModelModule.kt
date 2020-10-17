package com.adidas.luissilva.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.adidas.luissilva.di.annotations.ViewModelKey
import com.adidas.luissilva.di.factory.ViewModelFactory
import com.adidas.luissilva.ui.screens.goalDetails.GoalDetailViewModel
import com.adidas.luissilva.ui.screens.goals.GoalsViewModel

@Module(includes = [RepositoryModule::class])
abstract class ViewModelModule {

    @Binds
    abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(GoalsViewModel::class)
    abstract fun bindsGoalsViewModel(goalsViewModel: GoalsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GoalDetailViewModel::class)
    abstract fun bindsGoalDetailViewModel(goalDetailViewModel: GoalDetailViewModel): ViewModel
}