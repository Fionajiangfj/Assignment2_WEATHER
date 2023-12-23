package com.yj.weather_yan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_weather_records")
class WeatherRecord(
    val cityName: String,
    val datetime: String,
    val temp: Double,
    val humidity: Double,
    val conditions: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    override fun toString(): String {
        return "WeatherRecord(datetime='$datetime', temp=$temp, humidity=$humidity, conditions='$conditions', id=$id)"
    }
}