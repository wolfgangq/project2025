package com.tradition.mobilevtkproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.UniversalRegionItem
import com.tradition.mobilevtkproject.screens.RegionActivitiesFragment.Companion.doButtonActionWithVibrate

class ItemEventAdapter(
    private val onItemClick: (UniversalRegionItem) -> Unit,
    private val actionEventRegister: (UniversalRegionItem) -> Unit
) : ListAdapter<UniversalRegionItem, ItemEventAdapter.VH>(Diff) {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: UniversalRegionItem) = with(view) {
            view.findViewById<TextView>(R.id.eventTitle).text = item.title
            view.findViewById<TextView>(R.id.eventDescription).text = item.description
            val continueButton: Button = view.findViewById(R.id.registerButton)
            val textViewDate = view.findViewById<TextView>(R.id.eventDate)
            val textViewTime = view.findViewById<TextView>(R.id.eventTime)
            if (item.date != null) {
                textViewDate.text = item.date
            } else {
                textViewDate.visibility = View.GONE
            }

            if (item.startTime != null && item.endTime != null) {
                textViewTime.text = "${item.startTime} - ${item.endTime}"
            } else if (item.startTime != null) {
                textViewTime.text = item.startTime
            } else {
                textViewTime.visibility = View.GONE
            }



            view.setOnClickListener { onItemClick(item) }
            doButtonActionWithVibrate(continueButton, view, {actionEventRegister(item)})


            setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return VH(v)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object Diff : DiffUtil.ItemCallback<UniversalRegionItem>() {
        override fun areItemsTheSame(oldItem: UniversalRegionItem, newItem: UniversalRegionItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: UniversalRegionItem,
            newItem: UniversalRegionItem
        ) =
            oldItem == newItem
    }
}