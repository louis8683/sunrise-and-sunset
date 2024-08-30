package com.example.android.sunriseandsunset.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.sunriseandsunset.data.FakeSunriseSunsetRepository
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.data.Repository
import com.example.android.sunriseandsunset.data.SunriseSunsetDatabase
import com.example.android.sunriseandsunset.data.SunriseSunsetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime
import kotlin.random.Random

private const val TAG = "ListViewModel"

class ListViewModel(application: Application): AndroidViewModel(application) {

    private val repository: Repository by lazy {
        val sunriseSunsetDao = SunriseSunsetDatabase.getDatabase(application).sunriseSunsetDao()
        SunriseSunsetRepository(sunriseSunsetDao)
    }

    val data: LiveData<List<SunriseSunset>>
        get() = repository.getAllSunriseSunset()

}