package com.example.android.sunriseandsunset.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.sunriseandsunset.R
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.databinding.FragmentListBinding
import com.example.android.sunriseandsunset.list.ListFragmentDirections
import java.time.LocalTime

class ListFragment : Fragment(), OnItemClickListener {

    companion object {
        private const val TAG = "ListFragment"
    }

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by viewModels()
    private lateinit var listAdapter: SunriseSunsetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_list, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        listAdapter = SunriseSunsetAdapter(this)
        binding.recyclerView.adapter = listAdapter
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        viewModel.data.observe(viewLifecycleOwner, Observer { list ->
            Log.d(TAG, "data updated: $list")
            (binding.recyclerView.adapter as SunriseSunsetAdapter).submitList(list)
        })

        binding.fab.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToNewItemFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onItemClick(item: SunriseSunset) {
        val action = ListFragmentDirections.actionListFragmentToDetailFragment()
        action.setSunriseSunset(item)
        findNavController().navigate(action)
    }

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition

            Log.d(TAG, "onMove, $fromPosition -> $toPosition")

            // Notify the adapter about the move
            listAdapter.notifyItemMoved(fromPosition, toPosition)

            // Modify the internal list but don't call submitList here
            viewModel.onItemMoved(fromPosition, toPosition)

            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)

            viewModel.onItemMovedCompleted()
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true // Enable drag on long press
        }
    }
}