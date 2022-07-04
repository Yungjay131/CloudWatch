package com.slyworks.cloudwatch.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 *Created by Joshua Sylvanus, 2:10 PM, 3/24/2022.
 */
class ApiClient {
    //region Vars
    private val BASE_URL_WEATHER: String = "https://api.openweathermap.org/data/2.5/"
    private val BASE_URL_REST_COUNTRIES: String = "https://restcountries.com/v2/"

    private var INSTANCE_WEATHER: Retrofit? = null
    private var INSTANCE_REST_COUNTRIES: Retrofit? = null
    //endregion

    private fun getWeatherRetrofitInstance():Retrofit{
            if(INSTANCE_WEATHER == null){
                    INSTANCE_WEATHER = Retrofit.Builder()
                            .baseUrl(BASE_URL_WEATHER)
                            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
            }

            return INSTANCE_WEATHER!!
    }

    private fun getRestCountriesRetrofitInstance():Retrofit{
            if(INSTANCE_REST_COUNTRIES == null){
                    INSTANCE_REST_COUNTRIES = Retrofit.Builder()
                            .baseUrl(BASE_URL_REST_COUNTRIES)
                            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
            }

            return INSTANCE_REST_COUNTRIES!!
    }


    fun getWeatherApiInterface():WeatherApi{
         return getWeatherRetrofitInstance()
             .create(WeatherApi::class.java)
    }

    fun getRestCountriesApiInterface():RestCountriesApi {
          return getRestCountriesRetrofitInstance()
              .create(RestCountriesApi::class.java)
    }
}