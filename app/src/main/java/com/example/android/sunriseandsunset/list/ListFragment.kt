package com.example.android.sunriseandsunset.list

import android.content.res.Resources
import android.graphics.Canvas
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
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.sunriseandsunset.R
import com.example.android.sunriseandsunset.data.SunriseSunset
import com.example.android.sunriseandsunset.databinding.FragmentListBinding
import kotlin.math.abs
import kotlin.math.sign


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

        return binding.root
    }

    override fun onItemClick(item: SunriseSunset) {
        val action = ListFragmentDirections.actionListFragmentToDetailFragment()
        action.setSunriseSunsetId(item.id)
        findNavController().navigate(action)
    }

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

        val swipeThreshold = 100f

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

        override fun isLongPressDragEnabled(): Boolean {
            return true // Enable drag on long press
        }

        // Swipe related functions

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(UP or DOWN, LEFT)
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            return swipeThreshold / screenWidth
        }

        override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
            return Float.MAX_VALUE
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.d(TAG, "Item Swiped")
            // Get the position of the swiped item
            val position = viewHolder.adapterPosition

            // Remove item from adapter or trigger actions (e.g. delete, edit)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            // Set a maximum swipe distance
            val clampedDx = if (abs(dX) > swipeThreshold) { sign(dX) * swipeThreshold } else dX

            super.onChildDraw(c, recyclerView, viewHolder, clampedDx, dY, actionState, isCurrentlyActive)
        }
    }
}