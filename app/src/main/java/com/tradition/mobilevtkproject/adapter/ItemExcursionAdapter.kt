package com.tradition.mobilevtkproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.UniversalRegionItem
import com.tradition.mobilevtkproject.screens.RegionActivitiesFragment

class ItemExcursionAdapter(
    private val onItemClick: (UniversalRegionItem) -> Unit,
    private val actionExcursionBook: (UniversalRegionItem) -> Unit
) : ListAdapter<UniversalRegionItem, ItemExcursionAdapter.VH>(Diff) {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: UniversalRegionItem) = with(view) {

            findViewById<TextView>(R.id.excursionTitle).text = item.title
            findViewById<TextView>(R.id.excursionDescription).text = item.description
            val imageView: ImageView = view.findViewById(R.id.excursionImage)
            val progressBar: ProgressBar = view.findViewById(R.id.excursionProgressBar)
            val constraintCard: ConstraintLayout = view.findViewById(R.id.excursionConstraintCard)
            val continueButton: Button = view.findViewById(R.id.bookButton)
            val textViewDuration = view.findViewById<TextView>(R.id.excursionDuration)
            val textViewGroupSize = view.findViewById<TextView>(R.id.excursionGroupSize)


            if (item.imageUrl != null) {
                RegionActivitiesFragment.loadImageWithRetry(imageView, item.imageUrl, progressBar)
            } else {
                constraintCard.visibility = View.GONE
            }
            if (item.duration != null) {
                textViewDuration.text = item.duration
            } else {
                textViewDuration.visibility = View.GONE
            }
            if (item.groupSize != null) {
                textViewGroupSize.text = item.groupSize
            } else {
                textViewGroupSize.visibility = View.GONE
            }

            RegionActivitiesFragment.doButtonActionWithVibrate(continueButton, view, { actionExcursionBook(item) })

            setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_excursion, parent, false)
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