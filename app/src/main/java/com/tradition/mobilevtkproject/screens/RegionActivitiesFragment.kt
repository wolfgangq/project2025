package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.SightItem
import com.tradition.mobilevtkproject.databinding.FragmentRegionActivitiesBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.os.Handler
import android.os.Looper
import com.tradition.mobilevtkproject.TransitionActivity

class RegionActivitiesFragment : Fragment() {

    lateinit var binding: FragmentRegionActivitiesBinding
    val db = Firebase.firestore
    lateinit var bundle: Bundle
    lateinit var sightItems: MutableList<SightItem>
    lateinit var regionName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegionActivitiesBinding.inflate(layoutInflater, container, false)
        regionName = arguments?.getString("RegionName").toString()
        lifecycleScope.launch {
            try {
                val snapshot = db.collection("regions").whereEqualTo("regionName", regionName).get().await()
                if (!snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    val sightsSnapshot = document.reference.collection("sights").get().await()

                    if (!sightsSnapshot.isEmpty) {
                        sightItems = mutableListOf<SightItem>()
                        for (sight in sightsSnapshot.documents) {
                            val sightData = sight.data
                            val curTitle = sightData?.get("sightName").toString()
                            val curDescr = sightData?.get("sightDescription").toString()
                            val curImageUrl = sightData?.get("sightImageUrl").toString()
                            sightItems.add(SightItem(curTitle, curDescr, curImageUrl))
                            Log.d("Firestore", "Sight data: $sightData")
                        }
                        sightItems.sortBy { it.title }

                    } else {
                        Log.d("Firestore", "No sights found for this region.")
                    }

                }
            }
            catch (e: Exception) {
                Log.w("Firestore", "Error updating document.", e)
            }
            populateCards()
        }
        return binding.root

    }
    private fun populateCards() {
        val inflater = LayoutInflater.from(MAIN2)

        for (item in sightItems) {
            val view = inflater.inflate(R.layout.sight_item_layout, binding.LinearLayoutSightsContainer, false)
            view.findViewById<TextView>(R.id.event_title).text = item.title
            view.findViewById<TextView>(R.id.event_description).text = item.description
            val imageView: ImageView = view.findViewById(R.id.event_image)
            Glide.with(view.context)
                .load(item.imageUrl)
                .into(imageView)
            view.setOnClickListener { onItemClick(item) }


            binding.LinearLayoutSightsContainer.addView(view)
        }
    }

    private fun onItemClick(item: SightItem) {
        bundle = Bundle()
        bundle.putString("SightName", item.title)
        (activity as? TransitionActivity)?.goFragment("Map", SettingsFragment(), bundle) //!!!
        val toast = Toast.makeText(MAIN2, "Вы нажали на ${item.title}", Toast.LENGTH_SHORT)
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, 1000)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var f1 = 0
        var f2 = 0
        var f3 = 0
        var f4 = 0

        binding.buttonExcursions.setOnClickListener{
            if(f1==0){
                f1 = 1
                binding.buttonExcursions.setCompoundDrawablesWithIntrinsicBounds(R.drawable.top_arrow, 0, 0, 0)
                binding.LinearLayoutExcursionsContainer.visibility = View.VISIBLE
            }
            else{
                f1 = 0
                binding.buttonExcursions.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bottom_arrow, 0, 0, 0)
                binding.LinearLayoutExcursionsContainer.visibility = View.GONE
            }
        }
        binding.buttonEvents.setOnClickListener{
            if(f2==0){
                f2 = 1
                binding.buttonEvents.setCompoundDrawablesWithIntrinsicBounds(R.drawable.top_arrow, 0, 0, 0)
                binding.LinearLayoutEventsContainer.visibility = View.VISIBLE
            }
            else{
                f2 = 0
                binding.buttonEvents.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bottom_arrow, 0, 0, 0)
                binding.LinearLayoutEventsContainer.visibility = View.GONE
            }
        }
        binding.buttonSights.setOnClickListener{
            if(f3==0){
                f3 = 1
                binding.buttonSights.setCompoundDrawablesWithIntrinsicBounds(R.drawable.top_arrow, 0, 0, 0)
                binding.LinearLayoutSightsContainer.visibility = View.VISIBLE
            }
            else{
                f3 = 0
                binding.buttonSights.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bottom_arrow, 0, 0, 0)
                binding.LinearLayoutSightsContainer.visibility = View.GONE
            }
        }
        binding.buttonLocalCompetitions.setOnClickListener{
            if(f4==0){
                f4 = 1
                binding.buttonLocalCompetitions.setCompoundDrawablesWithIntrinsicBounds(R.drawable.top_arrow, 0, 0, 0)
                binding.LinearLayoutCompetitionsContainer.visibility = View.VISIBLE
            }
            else{
                f4 = 0
                binding.buttonLocalCompetitions.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bottom_arrow, 0, 0, 0)
                binding.LinearLayoutCompetitionsContainer.visibility = View.GONE
            }
        }
        binding.imageButtonBack.setOnClickListener {
            (activity as? TransitionActivity)?.onBackPressed()
        }
    }
}