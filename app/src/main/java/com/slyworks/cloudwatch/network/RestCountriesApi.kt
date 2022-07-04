package com.slyworks.cloudwatch.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RestCountriesApi {
    @GET("alpha")
    fun getCountryForCode(@Query("codes") countryCode:String): Call<JsonObject>

    @GET("alpha")
    fun getCountryForCodes(@Query("codes") countryCodes:String): Call<JsonObject>
}
