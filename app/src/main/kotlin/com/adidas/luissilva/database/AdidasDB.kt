package com.adidas.luissilva.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.adidas.luissilva.app.DATABASE_NAME
import com.adidas.luissilva.database.dao.GoalsDAO
import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.utils.helper.AppExecutors
import java.util.concurrent.Executors

@Database(entities = [Goal::class], version = 1, exportSchema = false)
abstract class AdidasDB : RoomDatabase() {

    abstract fun GoalsDAO(): GoalsDAO

    fun runAsync(func: AdidasDB.() -> Unit) {
        appExecutors?.getDiskIO()?.execute {
            func.invoke(this)
        }
    }

    fun deleteDatabase(isAsync: Boolean = true) {
        if (isAsync) {
            runAsync { clearAllTables() }
        } else {
            clearAllTables()
        }
    }

    companion object {
        @Volatile
        private var database: AdidasDB? = null

        private var appExecutors: AppExecutors? = null


//        val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(db: SupportSQLiteDatabase) {
////                database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " + "PRIMARY KEY(`id`))")
//            }
//        }

//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
////                database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " + "PRIMARY KEY(`id`))")
//            }
//        }


        fun init(context: Context, appExecutors: AppExecutors): AdidasDB {
            AdidasDB.appExecutors = appExecutors

            return database ?: synchronized(this) {
                database ?: initDB(context).also { database = it }
            }
        }

        fun getInstance(context: Context): AdidasDB {
            return database ?: synchronized(this) {
                database ?: initDB(context).also { database = it }
            }
        }

        private fun initDB(context: Context): AdidasDB {
            return Room
                    .databaseBuilder(context, AdidasDB::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            // do something after database has been created

                            Executors.newSingleThreadScheduledExecutor().execute {
//                            //RUN IN BACKGROUND
                                database?.let {
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            // do something every time database is open
                            Executors.newSingleThreadScheduledExecutor().execute {
                                //RUN IN BACKGROUND
                                database?.let {

                                }
                            }
                        }

                        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                            // do something every time database is destroyed
                            Executors.newSingleThreadScheduledExecutor().execute {
                                //RUN IN BACKGROUND
                                database?.let {

                                }
                            }
                        }
                    })
//                .addMigrations(
//                        AdidasDB.MIGRATION_2_3,
//                        AdidasDB.MIGRATION_1_2
//                )
                    .fallbackToDestructiveMigration()//TODO Remove when app is ready for first release
                    .build()
        }
    }
}