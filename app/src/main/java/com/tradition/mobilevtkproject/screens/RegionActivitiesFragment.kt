package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
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
import com.tradition.mobilevtkproject.databinding.FragmentRegionActivitiesBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tradition.mobilevtkproject.Card
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.TransitionActivity.Companion.setColors
import com.tradition.mobilevtkproject.UniversalRegionItem
import kotlin.math.pow

class RegionActivitiesFragment : Fragment() {

    lateinit var binding: FragmentRegionActivitiesBinding
    val db = Firebase.firestore
    lateinit var bundle: Bundle
    var excursionItems = mutableListOf<UniversalRegionItem>()
    var eventItems = mutableListOf<UniversalRegionItem>()
    var sightItems = mutableListOf<UniversalRegionItem>()
    var competitionItems = mutableListOf<UniversalRegionItem>()
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


                    val excursionsSnapshot = document.reference.collection("excursions").get().await()
                    if (!excursionsSnapshot.isEmpty) {
                        excursionItems.clear()
                        for (excursion in excursionsSnapshot.documents) {
                            val excursionData = excursion.data
                            val curTitle = excursionData?.get("excursionName").toString()
                            val curDescr = excursionData?.get("excursionDescription").toString()
                            val url = excursionData?.get("excursionImageUrl")
                            if (url != null){
                                val curImageUrl = url.toString()
                                excursionItems.add(UniversalRegionItem(curTitle, curDescr, curImageUrl))
                            }
                            else{
                                val curImageUrl = url
                                excursionItems.add(UniversalRegionItem(curTitle, curDescr, curImageUrl))
                            }
                            Log.d("Firestore", "Sight data: $excursionData")
                        }
                        excursionItems.sortBy { it.title }
                        populateCards(binding.LinearLayoutExcursionsContainer, excursionItems, Card.Excursion)

                    } else {
                        Log.d("Firestore", "No excursions found for this region.")
                    }

                    val eventsSnapshot = document.reference.collection("events").get().await()
                    if (!eventsSnapshot.isEmpty) {
                        eventItems.clear()
                        for (event in eventsSnapshot.documents) {
                            val eventData = event.data
                            val curTitle = eventData?.get("eventName").toString()
                            val curDescr = eventData?.get("eventDescription").toString()
                            val url = eventData?.get("eventImageUrl")
                            if (url != null){
                                val curImageUrl = url.toString()
                                eventItems.add(UniversalRegionItem(curTitle, curDescr, curImageUrl))
                            }
                            else{
                                val curImageUrl = url
                                eventItems.add(UniversalRegionItem(curTitle, curDescr, curImageUrl))
                            }
                            Log.d("Firestore", "Sight data: $eventData")
                        }
                        eventItems.sortBy { it.title }
                        populateCards(binding.LinearLayoutEventsContainer, eventItems, Card.Event)

                    } else {
                        Log.d("Firestore", "No events found for this region.")
                    }

                    val sightsSnapshot = document.reference.collection("sights").get().await()
                    if (!sightsSnapshot.isEmpty) {
                        sightItems.clear()
                        for (sight in sightsSnapshot.documents) {
                            val sightData = sight.data
                            val curTitle = sightData?.get("sightName").toString()
                            val curDescr = sightData?.get("sightDescription").toString()
                            val url = sightData?.get("sightImageUrl")
                            if (url != null){
                                val curImageUrl = url.toString()
                                sightItems.add(UniversalRegionItem(curTitle, curDescr, curImageUrl))
                            }
                            else{
                                val curImageUrl = url
                                sightItems.add(UniversalRegionItem(curTitle, curDescr, curImageUrl))
                            }
                            Log.d("Firestore", "Sight data: $sightData")
                        }
                        sightItems.sortByDescending { it.imageUrl }
                        populateCards(binding.LinearLayoutSightsContainer, sightItems, Card.Sight)

                    } else {
                        Log.d("Firestore", "No sights found for this region.")
                    }

                    val competitionsSnapshot = document.reference.collection("competitions").get().await()
                    if (!competitionsSnapshot.isEmpty) {
                        competitionItems.clear()
                        for (competition in competitionsSnapshot.documents) {
                            val competitionData = competition.data
                            val curTitle = competitionData?.get("competitionName").toString()
                            val curDescr = competitionData?.get("competitionDescription").toString()
                            val url = competitionData?.get("competitionImageUrl")
                            if (url != null){
                                val curImageUrl = url.toString()
                                competitionItems.add(UniversalRegionItem(curTitle, curDescr, curImageUrl))
                            }
                            else{
                                val curImageUrl = url
                                competitionItems.add(UniversalRegionItem(curTitle, curDescr, curImageUrl))
                            }
                            Log.d("Firestore", "Sight data: $competitionData")
                        }
                        competitionItems.sortBy { it.title }
                        populateCards(binding.LinearLayoutCompetitionsContainer, competitionItems, Card.Competition)

                    } else {
                        Log.d("Firestore", "No competitions found for this region.")
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
    fun loadImageWithRetry(imageView: ImageView, imageUrl: String?, progressBar: ProgressBar, attempt: Int = 1) {
        if (imageUrl != null){
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
        else{
            return
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun populateCards(container: android.widget.LinearLayout, someList: MutableList<UniversalRegionItem>, objectType: Card) {
        val inflater = LayoutInflater.from(MAIN2)
        container.removeAllViews()

        if (someList.isEmpty()) {
            val view = inflater.inflate(R.layout.sight_item_layout, container, false)
            view.findViewById<TextView>(R.id.event_title).text = "Находится в разработке"
            val continueButton: Button = view.findViewById(R.id.buttonToContinue)
            val constraintCard: ConstraintLayout = view.findViewById(R.id.constraintCard)
            constraintCard.visibility = View.GONE
            continueButton.visibility = View.GONE
            container.addView(view)
            return
        }

        for (item in someList) {
            val view = inflater.inflate(R.layout.sight_item_layout, container, false)
            view.findViewById<TextView>(R.id.event_title).text = item.title
            view.findViewById<TextView>(R.id.event_description).text = item.description
            val imageView: ImageView = view.findViewById(R.id.event_image)
            val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
            val constraintCard: ConstraintLayout = view.findViewById(R.id.constraintCard)
            val continueButton: Button = view.findViewById(R.id.buttonToContinue)
            when (objectType){
                Card.Excursion -> {
                    continueButton.text = "Записаться"
                }
                Card.Event -> {}
                Card.Sight -> continueButton.visibility = View.GONE
                Card.Competition -> continueButton.text = "Отправить работу"
            }

            if (item.imageUrl != null){
                loadImageWithRetry(imageView, item.imageUrl, progressBar)
            }
            else{
                constraintCard.visibility = View.GONE
            }

            view.setOnClickListener { onViewClick(item) }
            continueButton.setOnClickListener { onButtonClick(item, objectType) }
            container.addView(view)
        }
    }

    private fun onViewClick(item: UniversalRegionItem) {
        val toast = Toast.makeText(MAIN2, item.title, Toast.LENGTH_SHORT)
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, 1000)
    }

    private fun onButtonClick(item: UniversalRegionItem, objectType: Card) {
        bundle = Bundle()
        bundle.putString("CardName", item.title)
        bundle.putSerializable("CardType", objectType)
        //(activity as? TransitionActivity)?.goFragment("Map", SettingsFragment(), bundle) //!!!
        when (objectType){
            Card.Excursion -> {
                val builder = AlertDialog.Builder(MAIN2)
                builder.setTitle("Экскурсия")
                    .setMessage("Вы уверены, что хотите записаться на эскурсию?")
                builder.setNegativeButton("Да") { dialog, which ->
                    db
                }
                builder.setPositiveButton("Назад") { dialog, which ->
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
            Card.Event -> ""
            Card.Sight -> ""
            Card.Competition -> (activity as? TransitionActivity)?.goFragment("Map", CompetitionFragment(), bundle)
        }
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
                populateCards(binding.LinearLayoutExcursionsContainer, excursionItems, Card.Excursion)
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
                populateCards(binding.LinearLayoutEventsContainer, eventItems, Card.Event)
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
                populateCards(binding.LinearLayoutSightsContainer, sightItems, Card.Sight)
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
                populateCards(binding.LinearLayoutCompetitionsContainer, competitionItems, Card.Competition)
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

    override fun onResume() {
        super.onResume()
        requireActivity().window.navigationBarColor = Color.WHITE
        setColors(requireActivity(), "mainGreen")
    }
}