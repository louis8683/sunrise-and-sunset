package com.example.android.sunriseandsunset.data

import androidx.lifecycle.LiveData

class SunriseSunsetRepository(private val dao: SunriseSunsetDao): Repository {

    override fun getAllSunriseSunset(): LiveData<List<SunriseSunset>> {
        return dao.getAllSunriseSunset()
    }

    override fun getSunriseSunsetById(id: Long): LiveData<SunriseSunset?> {
        return dao.getSunriseSunsetById(id)
    }

    override suspend fun insertSunriseSunset(sunriseSunset: SunriseSunset) {
        return dao.insertSunriseSunset(sunriseSunset)
    }

    override suspend fun updateSunriseSunset(sunriseSunset: SunriseSunset) {
        return dao.updateSunriseSunset(sunriseSunset)
    }

    override suspend fun deleteSunriseSunset(id: Long) {
        val sunriseSunset = getSunriseSunsetById(id).value
        sunriseSunset?.let {
            dao.deleteSunriseSunset(it)
        }
    }
}