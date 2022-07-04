package com.slyworks.cloudwatch.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    fun getWeatherForSingleCity(@Query( "q") id:String, @Query("appid") app_id:String = "c4ae0db64ec975f4ae2da13e8b25780d"): Call<JsonObject>

    @GET("weather")
    fun getWeatherForCity(@Query( "q") id:String, @Query("appid") app_id:String = "c4ae0db64ec975f4ae2da13e8b25780d"): Call<JsonObject>

    @GET("group?")
    fun getWeatherForCities(@Query("id")   ids: String, @Query("appid") app_id:String = "c4ae0db64ec975f4ae2da13e8b25780d"): Call<JsonObject>

}
