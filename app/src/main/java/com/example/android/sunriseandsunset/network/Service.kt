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

// API Interface
interface OpenWeatherApiForecastService {
    @GET("data/2.5/forecast")
    fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}

object Network {
    private const val SUNRISE_BASE_URL = "https://api.sunrisesunset.io/"

    private val sunriseRetrofit = Retrofit.Builder()
        .baseUrl(SUNRISE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val sunriseSunsetService: SunriseSunsetService = sunriseRetrofit.create(SunriseSunsetService::class.java)

    private const val OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/"

    val forecastApi: OpenWeatherApiForecastService by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApiForecastService::class.java)
    }
    
}