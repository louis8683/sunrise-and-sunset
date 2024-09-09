package com.example.android.sunriseandsunset.map

import android.app.Application
import android.text.Selection
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.sunriseandsunset.data.Repository
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.data.SunriseSunsetDatabase
import com.example.android.sunriseandsunset.data.SunriseSunsetRepository
import com.example.android.sunriseandsunset.network.Network
import com.example.android.sunriseandsunset.network.Results
import com.example.android.sunriseandsunset.network.SunriseSunsetResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import java.io.IOException
import java.time.LocalTime
import kotlin.random.Random

class MapViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "MapViewModel"
    }

    private val repository: Repository by lazy {
        val sunriseSunsetDao = SunriseSunsetDatabase.getDatabase(application).sunriseSunsetDao()
        SunriseSunsetRepository(sunriseSunsetDao)
    }

    data class Selection(val latLng: LatLng, val title: String?)

    private val _selection = MutableLiveData<Selection>()
    val selection: LiveData<Selection> = _selection

    private val _sunrise = MutableLiveData<LocalTime>()
    val sunrise: LiveData<LocalTime> = _sunrise

    private val _sunset = MutableLiveData<LocalTime>()
    val sunset: LiveData<LocalTime> = _sunset

    // Live data for navigation
    private val _navigateToList = MutableLiveData<SunriseSunset>()
    val navigateToList = _navigateToList

    fun updateSelection(latLng: LatLng, title: String?) {
        _selection.value = Selection(latLng, title)
    }

    fun saveSelection() {
        viewModelScope.launch {
            updateSunriseSunsetTime()
            saveToDatabase()
        }
    }

    suspend fun updateSunriseSunsetTime() {
        selection.value?.let { mapSelection ->
            var results : Results? = null
            withContext(Dispatchers.IO) {
                try {
                    val response = Network.sunriseSunsetService.getSunriseSunsetTimes(
                        mapSelection.latLng.latitude,
                        mapSelection.latLng.longitude
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

    suspend fun saveToDatabase() {

        val title = _selection.value?.title
        val latLng = _selection.value?.latLng
        val sunriseTime = _sunrise.value
        val sunsetTime = _sunset.value

        if (title == null || latLng == null || sunsetTime == null || sunriseTime == null) {
            Log.d(TAG, "Data missing while saving to database")
        }
        else {
            // Determine the next position (last position + 1)
            val nextPosition = withContext(Dispatchers.IO) { repository.getSize() }

            val item = SunriseSunset(
                    Random.nextLong(),
                    title,
                    latLng.latitude,
                    latLng.longitude,
                    sunriseTime,
                    sunsetTime,
                    position = nextPosition
                )

            withContext(Dispatchers.IO) {
                repository.insertSunriseSunset(item)
            }

            _navigateToList.value = item

            Log.d(TAG, "Saved")
        }
    }
}