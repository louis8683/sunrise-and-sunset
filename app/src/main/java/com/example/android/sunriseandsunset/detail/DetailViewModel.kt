package com.example.android.sunriseandsunset.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.sunriseandsunset.data.SunriseSunset

class DetailViewModel: ViewModel() {

    private val _sunriseSunset = MutableLiveData<SunriseSunset>()
    val sunriseSunset: LiveData<SunriseSunset> = _sunriseSunset

    fun setData(sunriseSunset: SunriseSunset) {
        _sunriseSunset.value = sunriseSunset
    }
}