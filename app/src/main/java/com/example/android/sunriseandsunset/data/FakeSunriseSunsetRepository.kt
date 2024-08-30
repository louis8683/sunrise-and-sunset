package com.example.android.sunriseandsunset.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object FakeSunriseSunsetRepository : Repository {

    private val sunriseSunsetList = mutableListOf<SunriseSunset>()
    private val sunriseSunsetLiveData = MutableLiveData<List<SunriseSunset>>(sunriseSunsetList)

    override fun getAllSunriseSunset(): LiveData<List<SunriseSunset>> {
        return sunriseSunsetLiveData
    }

    override fun getSunriseSunsetById(id: Long): LiveData<SunriseSunset?> {
        val item = sunriseSunsetList.find { it.id == id }
        return MutableLiveData(item)
    }

    override suspend fun insertSunriseSunset(sunriseSunset: SunriseSunset) {
        sunriseSunsetList.add(sunriseSunset.copy(id = (sunriseSunsetList.size + 1).toLong()))
        sunriseSunsetLiveData.postValue(sunriseSunsetList)
    }

    override suspend fun updateSunriseSunset(sunriseSunset: SunriseSunset) {
        val index = sunriseSunsetList.indexOfFirst { it.id == sunriseSunset.id }
        if (index != -1) {
            sunriseSunsetList[index] = sunriseSunset
            sunriseSunsetLiveData.postValue(sunriseSunsetList)
        }
    }

    override suspend fun deleteSunriseSunset(id: Long) {
        sunriseSunsetList.removeAll { it.id == id }
        sunriseSunsetLiveData.postValue(sunriseSunsetList)
    }
}