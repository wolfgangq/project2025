package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
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
import android.widget.ProgressBar
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tradition.mobilevtkproject.TransitionActivity
import kotlin.math.pow

class RegionActivitiesFragment : Fragment() {

    lateinit var binding: FragmentRegionActivitiesBinding
    val db = Firebase.firestore
    lateinit var bundle: Bundle
    var sightItems = mutableListOf<SightItem>()
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
                        sightItems.clear()
                        for (sight in sightsSnapshot.documents) {
                            val sightData = sight.data
                            val curTitle = sightData?.get("sightName").toString()
                            val curDescr = sightData?.get("sightDescription").toString()
                            val curImageUrl = sightData?.get("sightImageUrl").toString()
                            sightItems.add(SightItem(curTitle, curDescr, curImageUrl))
                            Log.d("Firestore", "Sight data: $sightData")
                        }
                        sightItems.sortBy { it.title }
                        populateCards()

                    } else {
                        Log.d("Firestore", "No sights found for this region.")
                    }

                }
            }
            catch (e: Exception) {
                Log.w("Firestore", "Error updating document.", e)
            }
        }
        return binding.root
    }


    val MAX_ATTEMPTS = 10
    fun loadImageWithRetry(imageView: ImageView, imageUrl: String, progressBar: ProgressBar, attempt: Int = 1) {
        if (attempt > MAX_ATTEMPTS) {
            Log.e("Glide", "Max attempts reached for loading image")
            val toast = Toast.makeText(MAIN2, "Не удалось загрузить изображение за отведенное время", Toast.LENGTH_SHORT)
            toast.show()
            Handler(Looper.getMainLooper()).postDelayed({
                toast.cancel()
            }, 500)
            progressBar.visibility = View.GONE
            return
        }

        Glide.with(imageView.context).clear(imageView)

        Glide.with(imageView.context)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("Glide", "Image load failed", e)

                    val delayMillis = (500 * 2.0.pow(attempt.toDouble())).toLong()


                    Handler(Looper.getMainLooper()).postDelayed({
                        loadImageWithRetry(imageView, imageUrl, progressBar, attempt + 1)
                    }, delayMillis)

                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: com.bumptech.glide.load.DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(imageView)
    }

    private fun populateCards() {
        val inflater = LayoutInflater.from(MAIN2)
        binding.LinearLayoutSightsContainer.removeAllViews()

        if (sightItems.isEmpty()) {

            return
        }

        for (item in sightItems) {
            val view = inflater.inflate(R.layout.sight_item_layout, binding.LinearLayoutSightsContainer, false)
            view.findViewById<TextView>(R.id.event_title).text = item.title
            view.findViewById<TextView>(R.id.event_description).text = item.description
            val imageView: ImageView = view.findViewById(R.id.event_image)
            val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

            loadImageWithRetry(imageView, item.imageUrl, progressBar)

            view.setOnClickListener { onItemClick(item) }
            binding.LinearLayoutSightsContainer.addView(view)
        }
    }

    private fun onItemClick(item: SightItem) {
        bundle = Bundle()
        bundle.putString("SightName", item.title)
        //(activity as? TransitionActivity)?.goFragment("Map", SettingsFragment(), bundle) //!!!
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
                populateCards()
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
                populateCards()
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
                populateCards()
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
                populateCards()
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