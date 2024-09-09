package com.example.android.sunriseandsunset.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.android.sunriseandsunset.data.Repository
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.data.SunriseSunsetDatabase
import com.example.android.sunriseandsunset.data.SunriseSunsetRepository
import kotlinx.coroutines.launch

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