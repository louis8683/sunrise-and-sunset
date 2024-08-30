package com.example.android.sunriseandsunset.newitem

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.sunriseandsunset.data.FakeSunriseSunsetRepository
import com.example.android.sunriseandsunset.data.Repository
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.data.SunriseSunsetDatabase
import com.example.android.sunriseandsunset.data.SunriseSunsetRepository
import com.example.android.sunriseandsunset.network.Network
import com.example.android.sunriseandsunset.network.Results
import com.example.android.sunriseandsunset.network.SunriseSunsetResponse
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Callback
import retrofit2.await
import java.io.IOException
import java.time.LocalTime
import kotlin.random.Random

private const val TAG = "NewItemViewModel"

class NewItemViewModel(application: Application): AndroidViewModel(application) {

    private val repository: Repository by lazy {
        val sunriseSunsetDao = SunriseSunsetDatabase.getDatabase(application).sunriseSunsetDao()
        SunriseSunsetRepository(sunriseSunsetDao)
    }

    // This is exposed to enable 2-way data binding
    val locationName = MutableLiveData<String>("")

    private val _latitude = MutableLiveData<Double>()
    val latitude: LiveData<Double> = _latitude

    private val _longitude = MutableLiveData<Double>()
    val longitude: LiveData<Double> = _longitude

    private val _sunrise = MutableLiveData<LocalTime>()
    val sunrise: LiveData<LocalTime> = _sunrise

    private val _sunset = MutableLiveData<LocalTime>()
    val sunset: LiveData<LocalTime> = _sunset

    fun setLatLng(latLng: LatLng) {
        _latitude.value = latLng.latitude
        _longitude.value = latLng.longitude
        updateSunriseSunsetTime()
    }

    // Live data for navigation
    private val _navigateToList = MutableLiveData<Boolean>(false)
    val navigateToList = _navigateToList

    init {
        // TODO: Remove mock data
        _latitude.value = 33.9422241784545
        _longitude.value = -118.40361593251714
        locationName.value = "Los Angeles Airport"

        updateSunriseSunsetTime()
    }

    fun updateSunriseSunsetTime() {
        if (_latitude.value == null || _longitude.value == null) return

        viewModelScope.launch {
            var results : Results? = null
            withContext(Dispatchers.IO) {
                try {
                    val response = Network.sunriseSunsetService.getSunriseSunsetTimes(
                        _latitude.value!!,
                        _longitude.value!!
                    ).await()
                    if (response.status == SunriseSunsetResponse.STATUS_OK) {
                        results = response.results
                    } else {
                        // TODO: display some error
                        Log.d(TAG, "Error retrieving data from API")
                    }
                } catch (e: IOException) {
                    Log.d(TAG, "IOException: ${e.message}")
                    // TODO: proper error handling
                } catch (e: Exception) {
                    Log.d(TAG, "Exception: ${e.message}")
                    // TODO: proper error handling
                }
            }
            results?.let {
                Log.d(TAG, "API result: $results")
                Log.d(TAG, "${it.parsedSunrise()}")
                _sunrise.value = it.parsedSunrise()
                _sunset.value = it.parsedSunset()
            }
        }

    }

    fun saveToDatabase() {
        if (locationName.value != null && latitude.value != null && longitude.value != null && _sunrise.value != null && _sunset.value != null) {

            val item = SunriseSunset(
                Random.nextLong(),
                locationName.value!!,
                latitude.value!!,
                longitude.value!!,
                _sunrise.value!!,
                _sunset.value!!
            )

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    repository.insertSunriseSunset(item)
                }
                Log.d(TAG, "Saved")
                _navigateToList.value = true
            }
        }
        else {
            Log.d(TAG, "Did not save: empty fields")
        }
    }

    fun doneNavigatingToList() {
        _navigateToList.value = false
    }

}