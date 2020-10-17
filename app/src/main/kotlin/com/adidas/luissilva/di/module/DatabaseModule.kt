package com.adidas.luissilva.di.module

import android.app.Application
import com.adidas.luissilva.database.AdidasDB
import com.adidas.luissilva.database.dao.GoalsDAO
import com.adidas.luissilva.utils.helper.AppExecutors
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    //Besides already being singleton we initiate it here as singleton

    @Singleton
    @Provides
    fun provideDatabase(application: Application, appExecutors: AppExecutors): AdidasDB = AdidasDB.init(application, appExecutors)


    @Singleton
    @Provides
    fun provideGoalsDao(db: AdidasDB): GoalsDAO {
        return db.GoalsDAO()
    }
}