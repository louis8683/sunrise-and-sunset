package com.example.android.sunriseandsunset.map

import android.text.Selection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel() {

    data class Selection(val latLng: LatLng, val title: String?)

    private val _selection = MutableLiveData<Selection>()
    val selection: LiveData<Selection> = _selection

    fun updateSelection(latLng: LatLng, title: String?) {
        _selection.value = Selection(latLng, title)
    }
}