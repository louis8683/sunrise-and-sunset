package com.example.android.sunriseandsunset.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.sunriseandsunset.R
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.databinding.FragmentDetailBinding
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class DetailFragment : Fragment() {

    companion object {
        private const val TAG = "DetailFragment"
    }

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        val sunriseSunsetId: Long = args.sunriseSunsetId
        Log.d(TAG, "Arg: $sunriseSunsetId")

        // Create the ViewModelFactory
        val viewModelFactory = if (sunriseSunsetId == 0L) {
            DetailViewModelFactory(requireActivity().application, null)
        }
        else {
            DetailViewModelFactory(requireActivity().application, sunriseSunsetId)
        }

        // Get the ViewModel using the factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.solarClockDesign.viewModel = viewModel

        viewModel.sunriseSunset.observe(viewLifecycleOwner, Observer {
            it?.let {
                (activity as? AppCompatActivity)?.supportActionBar?.title = it.locationName
                Log.d(TAG, "$it")
            }
        })

        // Set the sun position
        viewModel.sunPercentage.observe(viewLifecycleOwner, Observer { p ->
            val theta = 2 * PI * p
            val biasX = (cos(theta) + 1).toFloat() / 2
            val biasY = (sin(theta) + 1).toFloat() / 2

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.solarClockDesign.constraintLayoutSolarClock)
            constraintSet.setHorizontalBias(R.id.imageViewSun, biasX)
            constraintSet.setVerticalBias(R.id.imageViewSun, biasY)
            constraintSet.applyTo(binding.solarClockDesign.constraintLayoutSolarClock)
        })

        return binding.root
    }
}