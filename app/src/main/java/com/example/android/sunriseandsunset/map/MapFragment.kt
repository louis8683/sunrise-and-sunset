package com.example.android.sunriseandsunset.map

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.sunriseandsunset.BuildConfig
import com.example.android.sunriseandsunset.R
import com.example.android.sunriseandsunset.databinding.FragmentMapBinding
import com.example.android.sunriseandsunset.newitem.NewItemViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar

class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val TAG = "MapFragment"
    }

    private val viewModel: MapViewModel by viewModels()
    private lateinit var binding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true || permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    enableMyLocation()
                }
                else -> {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_map, container, false)

        // Map
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.buttonConfirmSelection.setOnClickListener {
            // Save the selection
            viewModel.selection.value?.let {
                val latLng = it.latLng
                val newItemViewModel: NewItemViewModel by activityViewModels()
                newItemViewModel.locationName.value = it.title
                newItemViewModel.setLatLng(latLng)
                findNavController().popBackStack()
            } ?: run {
                Snackbar.make(binding.root, "Please select a location before proceeding.", Snackbar.LENGTH_LONG)
                    .setAction("OK") {
                        // Optional: Provide an action, like focusing the map or opening the POI selector
                    }
                    .show()
            }
        }

        // Initialize Places
        if (!Places.isInitialized()) {
            // Old version
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        }

        initAutocompleteFragment()

        return binding.root
    }

    private fun initAutocompleteFragment() {
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}")

                place.latLng?.let {
                    viewModel.updateSelection(it, place.name)
                }
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {

        viewModel.selection.observe(viewLifecycleOwner, Observer {
            selectLatLngOnMap(it.latLng, it.title)
        })

        this.googleMap = googleMap

        // Zoom to default location
        val newYorkCity = LatLng(40.7128, -74.0060)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newYorkCity, 12f))

        // Make POIs selectable
        googleMap.setOnPoiClickListener { poi ->
            viewModel.updateSelection(poi.latLng, poi.name)
        }

        // Enable location
        enableMyLocation()

        // Enable long press
        googleMap.setOnMapLongClickListener { latLng ->
            viewModel.updateSelection(latLng, null)
        }
    }

    private fun selectLatLngOnMap(latLng: LatLng, displayTitle: String?) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))

        // Remove any existing markers
        googleMap.clear()

        displayTitle?.let {
            // Add a marker at the location
            googleMap.addMarker(MarkerOptions()
                .position(latLng)
                .title(displayTitle)
            )?.showInfoWindow()
        } ?: run {
            googleMap.addMarker(MarkerOptions()
                .position(latLng)
                .title("Custom Location")
            )?.showInfoWindow()
        }
    }

    // Request permission for location access and enable location for maps
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Show rationale dialog, then request permission
            showRationaleDialog()
        } else {
            // Directly request for permission
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Location Permission Needed")
            .setMessage("This app needs the Location permission to show your current location on the map.")
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
            .setNegativeButton("Skip", null)
            .create()
            .show()
    }
}