package com.adidas.luissilva.di.module

import com.adidas.luissilva.ui.screens.goalDetails.GoalDetailActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.adidas.luissilva.ui.screens.goals.GoalsActivity

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesGoalsActivity(): GoalsActivity

    @ContributesAndroidInjector
    abstract fun contributesGoalDetailActivity(): GoalDetailActivity
}