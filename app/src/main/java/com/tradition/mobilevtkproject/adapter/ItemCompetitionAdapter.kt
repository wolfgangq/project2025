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

class ItemCompetitionAdapter(
    private val onItemClick: (UniversalRegionItem) -> Unit,
    private val actionCompetitionToSend: (UniversalRegionItem) -> Unit
) : ListAdapter<UniversalRegionItem, ItemCompetitionAdapter.VH>(Diff) {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: UniversalRegionItem) = with(view) {
            view.findViewById<TextView>(R.id.competTitle).text = item.title
            view.findViewById<TextView>(R.id.miniDescriptionCompet).text = item.description
            val continueButton: Button = view.findViewById(R.id.sendButton)
            val textViewLocation = view.findViewById<TextView>(R.id.locationText)

            if (item.location != null) {
                textViewLocation.text = item.location
            }
            else {
                textViewLocation.visibility = View.GONE
            }

            view.setOnClickListener { onItemClick(item) }
            doButtonActionWithVibrate(continueButton, view, {actionCompetitionToSend(item)})

            setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_competition, parent, false)
        return VH(v)
    }



    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object Diff : DiffUtil.ItemCallback<UniversalRegionItem>() {
        override fun areItemsTheSame(oldItem: UniversalRegionItem, newItem: UniversalRegionItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UniversalRegionItem, newItem: UniversalRegionItem) =
            oldItem == newItem
    }
}