package com.example.android.sunriseandsunset.data

import androidx.lifecycle.LiveData

interface Repository {
    fun getAllSunriseSunset(): LiveData<List<SunriseSunset>>
    fun getSunriseSunsetById(id: Long): LiveData<SunriseSunset?>
    suspend fun insertSunriseSunset(sunriseSunset: SunriseSunset)
    suspend fun updateSunriseSunset(sunriseSunset: SunriseSunset)
    suspend fun deleteSunriseSunset(id: Long)
}