package com.yj.weather_yan.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherRecordDAO {

    @Insert
    fun insertRecord(weatherRecord: WeatherRecord)

    @Query("SELECT * FROM table_weather_records ORDER BY id")
    fun getAllRecords() : LiveData<List<WeatherRecord>>
}