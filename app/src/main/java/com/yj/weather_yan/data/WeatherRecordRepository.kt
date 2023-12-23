package com.yj.weather_yan.data

import android.app.Application
import androidx.lifecycle.LiveData

class WeatherRecordRepository (application: Application) {

    //obtain the instance of the database and notesDAO
    private var db: AppDB? = null
    private var weatherRecordDAO = AppDB.getDB(application)?.weatherRecordDAO()

    var allRecords: LiveData<List<WeatherRecord>>? = weatherRecordDAO?.getAllRecords()

    init {
        this.db = AppDB.getDB(application)
    }

    fun insertRecord(weatherRecord: WeatherRecord){
        AppDB.databaseQueryExecutor.execute {
            this.weatherRecordDAO?.insertRecord(weatherRecord)
        }
    }

}