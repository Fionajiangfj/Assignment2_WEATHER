package com.yj.weather_yan.api

import com.yj.weather_yan.models.Weather
import retrofit2.http.GET
import retrofit2.http.Path

interface MyInterface {
    @GET("/VisualCrossingWebServices/rest/services/timeline/{geo}/today?unitGroup=metric&elements=datetime,temp,humidity,conditions&include=current&key=GBEYVSNS9562GD3DR5ECMDSK2&contentType=json")
    suspend fun getCurrentWeather(@Path("geo") geo:String):Weather
}