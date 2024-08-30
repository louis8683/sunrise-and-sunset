package com.example.android.sunriseandsunset.newitem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.sunriseandsunset.R
import com.example.android.sunriseandsunset.databinding.FragmentNewItemBinding
import com.example.android.sunriseandsunset.list.ListViewModel
import com.example.android.sunriseandsunset.newitem.NewItemFragmentDirections

private const val TAG = "NewItemFragment"

class NewItemFragment : Fragment() {

    private lateinit var binding: FragmentNewItemBinding
    private val viewModel: NewItemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_new_item, container, false)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        viewModel.locationName.observe(viewLifecycleOwner, Observer {
            Log.d("NewItemFragment", "Location name changed: $it")
        })

        binding.buttonSelectLocation.setOnClickListener {
            val action =
                NewItemFragmentDirections.actionNewItemFragmentToMapFragment()
            findNavController().navigate(action)
        }

        binding.buttonSave.setOnClickListener {
            Log.d("NewItemFragment", "Saving: ${viewModel.locationName.value}")
            viewModel.saveToDatabase()
        }

        viewModel.navigateToList.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().popBackStack()
                viewModel.doneNavigatingToList()
            }
        })

        return binding.root
    }
}