package br.com.weatherapp.api

import br.com.weatherapp.entity.FindResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("find")
    fun find(
        @Query("q") cityName: String,
        @Query("appid") appId: String,
        @Query("lang") language: String,
        @Query("units") temperature: String) : Call<FindResult>

    @GET("group")
    fun group (
        @Query("id") id: String,
        @Query("appid") appKey: String,
        @Query("lang") lang: String,
        @Query("units") units: String): Call<FindResult>
}