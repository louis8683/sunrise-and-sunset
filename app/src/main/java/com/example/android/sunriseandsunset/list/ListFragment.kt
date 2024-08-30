package com.example.android.sunriseandsunset.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.sunriseandsunset.R
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.databinding.FragmentListBinding
import com.example.android.sunriseandsunset.list.ListFragmentDirections
import java.time.LocalTime

class ListFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_list, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = SunriseSunsetAdapter(this)

        viewModel.data.observe(viewLifecycleOwner, Observer { list ->
            (binding.recyclerView.adapter as SunriseSunsetAdapter).submitList(list)
        })

        binding.fab.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToNewItemFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onItemClick(item: SunriseSunset) {
        val action = ListFragmentDirections.actionListFragmentToDetailFragment(item)
        findNavController().navigate(action)
    }
}