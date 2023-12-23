package com.yj.weather_yan.models

data class Day(
    val datetime: String,
    val temp: Double,
    val humidity: Double,
    val conditions: String,
)