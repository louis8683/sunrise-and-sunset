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
import kotlinx.coroutines.delay
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

    // Private copy of the latest list to be updated with LiveData changes
    private var latestDataList: List<SunriseSunset> = emptyList()

    init {
        // Observe LiveData and update the private list whenever data changes
        data.observeForever { newList ->
            latestDataList = newList ?: emptyList()
            Log.d(TAG, "Latest data updated: $latestDataList")
        }
    }

    // Method to handle reordering items when dragged
    fun onItemMoved(fromPosition: Int, toPosition: Int) {

        if (latestDataList.isEmpty()) {
            Log.d(TAG, "Data list is empty")
            return
        }

        // Convert the list to mutable to modify it
        val currentList = latestDataList.toMutableList()

        // Swap the items in the list
        val movedItem = currentList.removeAt(fromPosition)
        currentList.add(toPosition, movedItem)

        // Update the position field in the list
        currentList.forEachIndexed { index, item ->
            item.position = index
        }

        // Update the local list but don't refresh the whole dataset
        latestDataList = currentList
    }

    fun onItemMovedCompleted() {
        viewModelScope.launch {
            repository.updateItemPositions(latestDataList)
        }
    }

}