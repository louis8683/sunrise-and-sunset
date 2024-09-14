package com.example.android.sunriseandsunset.detail

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.android.sunriseandsunset.BuildConfig
import com.example.android.sunriseandsunset.data.Repository
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.data.SunriseSunsetDatabase
import com.example.android.sunriseandsunset.data.SunriseSunsetRepository
import com.example.android.sunriseandsunset.network.Network
import com.example.android.sunriseandsunset.network.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

class DetailViewModel(application: Application, itemId: Long?): AndroidViewModel(application) {

    private val repository: Repository by lazy {
        val sunriseSunsetDao = SunriseSunsetDatabase.getDatabase(application).sunriseSunsetDao()
        SunriseSunsetRepository(sunriseSunsetDao)
    }
    private var _sunriseSunset: LiveData<SunriseSunset?> = if (itemId == null) {
        repository.getFirstSunriseSunset()
    } else {
        repository.getSunriseSunsetById(itemId)
    }
    val sunriseSunset: LiveData<SunriseSunset?>
        get() = _sunriseSunset

    private val _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse = _weatherResponse

    private val _sunPercentage = MutableLiveData<Float>()
    val sunPercentage: LiveData<Float> = _sunPercentage

    // TODO: remove this after testing the implementation of the sun position
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        var counter = 0
        override fun run() {
            counter++
            _sunPercentage.postValue((counter % 600).toFloat() / 600)
            handler.postDelayed(this, 100)
        }
    }

    init {
        _sunriseSunset.observeForever(Observer {
            it?.let { updateForecast(it) }
        })

        // TODO: remove this after testing the implementation of the sun position
        handler.post(runnable)
    }

    // Add the Open Weather Forecast API
    private fun updateForecast(item: SunriseSunset) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response = Network.forecastApi.getWeatherForecast(
                    item.latitude,
                    item.longitude,
                    BuildConfig.OPEN_WEATHER_API_KEY).await()
                _weatherResponse.postValue(response)
            }
        }
    }
}

class DetailViewModelFactory(
    private val application: Application,
    private val itemId: Long?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(application, itemId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}