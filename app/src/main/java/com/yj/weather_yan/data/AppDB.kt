package com.yj.weather_yan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

@Database(entities = [WeatherRecord::class], version = 2, exportSchema = false)
abstract class AppDB : RoomDatabase() {

    abstract fun weatherRecordDAO() : WeatherRecordDAO

    companion object{

        private var db : AppDB? = null

        private const val NUMBER_OF_THREADS = 4
        val databaseQueryExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDB(context: Context) : AppDB?{
            //check if the object is null
            if (db == null){
//                if yes, create a new object;
                db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "com.yj.weather_yan_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return db
        }

    }

}