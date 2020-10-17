package com.adidas.luissilva.app

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.adidas.luissilva.di.component.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import timber.log.Timber


/**
 * Base class fot the application.
 * You need to add this class to your manifest file.
 */
open class App @Inject constructor() : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector() = androidInjector

    override fun onCreate() {
        super.onCreate()

		Timber.plant(Timber.DebugTree())
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        AndroidThreeTen.init(this)
        DaggerAppComponent.builder().application(this).build().inject(this)
    }

}