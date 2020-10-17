package com.adidas.luissilva.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import com.adidas.luissilva.utils.helper.*
import com.adidas.luissilva.utils.manager.*
import javax.inject.Singleton

@Module
class UtilsModule {

    @Singleton
    @Provides
    fun providesIntentManager() = CallManager()

    @Singleton
    @Provides
    fun providesDialogManager() = DialogManager()

    @Singleton
    @Provides
    fun providesPreferencesManager(application: Application) = PreferencesManager(application)

    @Singleton
    @Provides
    fun providesPermissionHelper() = PermissionHelper()

}