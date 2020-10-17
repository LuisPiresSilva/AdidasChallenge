package com.adidas.luissilva.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.adidas.luissilva.ui.dialog.ErrorDialog
import com.adidas.luissilva.ui.dialog.LoadingDialog

@Module
abstract class DialogBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesLoadingDialog(): LoadingDialog

    @ContributesAndroidInjector
    abstract fun contributesErrorDialog(): ErrorDialog
}