package com.example.android.sunriseandsunset.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.sunriseandsunset.R
import com.example.android.sunriseandsunset.data.SunriseSunset

class SunriseSunsetAdapter(private val onItemClickListener: OnItemClickListener)
    : ListAdapter<SunriseSunset, SunriseSunsetAdapter.ViewHolder>(SunriseSunsetDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Adapter", "onBindViewHolder: $position")
        val item = getItem(position)
        val res = holder.itemView.context.resources

        holder.bind(item, onItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.list_item_sunrise_sunset, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(item: SunriseSunset, clickListener: OnItemClickListener) {
            location.text = item.locationName
            sunrise.text = item.formattedSunriseTime()
            sunset.text = item.formattedSunsetTime()

            itemView.setOnClickListener {
                clickListener.onItemClick(item)
            }
        }

        val location: TextView = itemView.findViewById(R.id.textLocation)
        val sunrise: TextView = itemView.findViewById(R.id.textSunrise)
        val sunset: TextView = itemView.findViewById(R.id.textSunset)
    }

}

class SunriseSunsetDiffCallback: DiffUtil.ItemCallback<SunriseSunset>() {
    override fun areItemsTheSame(oldItem: SunriseSunset, newItem: SunriseSunset): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SunriseSunset, newItem: SunriseSunset): Boolean {
        return oldItem == newItem
    }

}