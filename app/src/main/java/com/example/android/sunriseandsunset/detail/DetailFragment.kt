package com.example.android.sunriseandsunset.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.sunriseandsunset.R
import com.example.android.sunriseandsunset.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    companion object {
        private const val TAG = "DetailFragment"
    }

    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        val sunriseSunset = args.sunriseSunset
        Log.d(TAG, "Arg: $sunriseSunset")

        binding.viewModel = viewModel

        viewModel.setData(sunriseSunset)

        viewModel.sunriseSunset.observe(viewLifecycleOwner, Observer {
            (activity as? AppCompatActivity)?.supportActionBar?.title = it.locationName
        })

        return binding.root
    }

}