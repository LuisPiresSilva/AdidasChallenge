package com.adidas.luissilva.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.AndroidInjectionModule
import com.adidas.luissilva.app.App
import com.adidas.luissilva.di.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ActivityBuilderModule::class,
    DatabaseModule::class,
    DialogBuilderModule::class,
    FragmentBuilderModule::class,
    UtilsModule::class,
    ApiModule::class,
    ReceiverModule::class,
    ServiceModule::class,
    ViewModelModule::class,
    WorkersModule::class
])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent

    }

    override fun inject(app: App)

}