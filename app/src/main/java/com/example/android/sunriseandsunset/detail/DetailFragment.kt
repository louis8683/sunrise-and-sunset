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
        val sunriseSunset: SunriseSunset? = args.sunriseSunset
        Log.d(TAG, "Arg: $sunriseSunset")

        // Create the ViewModelFactory
        val viewModelFactory = DetailViewModelFactory(requireActivity().application, sunriseSunset?.id)

        // Get the ViewModel using the factory
        viewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)


        if (sunriseSunset != null)
            {viewModel.setData(sunriseSunset.id)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.sunriseSunset.observe(viewLifecycleOwner, Observer {
            it?.let {
                (activity as? AppCompatActivity)?.supportActionBar?.title = it.locationName
                Log.d(TAG, "$it")
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_detail, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_list -> {
                        Log.d(TAG, "Clicked the list menu icon")
                        val action = DetailFragmentDirections.actionDetailFragmentToListFragment()
                        findNavController().navigate(action)
                        true
                    }
                    else -> {
                        Log.d(TAG, "Unhandled icon clicked")
                        false
                    }
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}