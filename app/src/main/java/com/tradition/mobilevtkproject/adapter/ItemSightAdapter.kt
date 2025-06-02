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
import com.yandex.mapkit.mapview.MapView

class ItemSightAdapter(
    private val onItemClick: (UniversalRegionItem) -> Unit,
    private val actionSightDetails: (UniversalRegionItem) -> Unit
) : ListAdapter<UniversalRegionItem, ItemSightAdapter.VH>(Diff) {

    inner class VH(val view: View) : RecyclerView.ViewHolder(view) {
        val mapView: MapView = view.findViewById<MapView>(R.id.mapview)
        init {
            /*mapView.apply {
                mapWindow.map.isScrollGesturesEnabled = false
                mapWindow.map.isZoomGesturesEnabled = false
                mapWindow.map.isRotateGesturesEnabled = false
                mapWindow.map.isTiltGesturesEnabled = false
                }*/
            mapView.setNoninteractive(true)
            mapView.onStart()
        }
        fun bind(item: UniversalRegionItem) = with(view) {
            findViewById<TextView>(R.id.sightName).text = item.title
            val textViewDescription = view.findViewById<TextView>(R.id.miniDescriptionSight)
            val imageView: ImageView = view.findViewById(R.id.sightImage)
            val progressBar: ProgressBar = view.findViewById(R.id.sightProgressBar)
            val constraintCard: ConstraintLayout = view.findViewById(R.id.sightConstraintCard)
            val continueButton: Button = view.findViewById(R.id.detailSightButton)
            val textViewCord = view.findViewById<TextView>(R.id.coordinatesSight)

            if (item.description != null) {
                textViewDescription.visibility = View.VISIBLE
                textViewDescription.text = item.description
            } else {
                textViewDescription.visibility = View.GONE
            }
            if (item.imageUrl != null) {
                constraintCard.visibility = View.VISIBLE
                RegionActivitiesFragment.loadImageWithRetry(imageView, item.imageUrl, progressBar)
            } else {
                constraintCard.visibility = View.GONE
            }
            if (item.coordinates != null) {
                textViewCord.text = item.coordinates
                textViewCord.visibility = View.VISIBLE
                mapView.visibility = View.VISIBLE
                RegionActivitiesFragment.setupSightPoint(mapView, item.coordinates!!)
            } else {
                textViewCord.visibility = View.GONE
                mapView.visibility = View.GONE
                mapView.onStop()
            }

            RegionActivitiesFragment.doButtonActionWithVibrate(continueButton, view, {actionSightDetails(item)})

            setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sight, parent, false)
        return VH(v)
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
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