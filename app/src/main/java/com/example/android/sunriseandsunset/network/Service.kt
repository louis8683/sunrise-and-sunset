package com.example.android.sunriseandsunset.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SunriseSunsetService {
    @GET("json")
    fun getSunriseSunsetTimes(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double
    ): Call<SunriseSunsetResponse>
}

object Network {
    private const val BASE_URL = "https://api.sunrisesunset.io/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val sunriseSunsetService = retrofit.create(SunriseSunsetService::class.java)
}