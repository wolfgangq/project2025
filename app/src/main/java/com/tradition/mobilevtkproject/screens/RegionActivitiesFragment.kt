package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.tradition.mobilevtkproject.Card
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.UniversalRegionItem
import com.tradition.mobilevtkproject.databinding.FragmentRegionActivitiesBinding
import com.tradition.mobilevtkproject.utils.DateTimeUtils
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.pow

class RegionActivitiesFragment : Fragment() {

    lateinit var binding: FragmentRegionActivitiesBinding
    val db = Firebase.firestore
    val auth = Firebase.auth
    lateinit var bundle: Bundle
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
                    var excursionItems = getItemList("excursions")
                    binding.progressBarExcursions.visibility = View.GONE
                    populateCards(binding.LinearLayoutExcursionsContainer, excursionItems, Card.Excursion)
                    var eventItems = getItemList("events")
                    binding.progressBarEvents.visibility = View.GONE
                    populateCards(binding.LinearLayoutEventsContainer, eventItems, Card.Event)
                    var sightItems = getItemList("sights")
                    binding.progressBarSights.visibility = View.GONE
                    populateCards(binding.LinearLayoutSightsContainer, sightItems, Card.Sight)
                    var competitionItems = getItemList("competitions")
                    binding.progressBarCompetitions.visibility = View.GONE
                    populateCards(binding.LinearLayoutCompetitionsContainer, competitionItems, Card.Competition)
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

    private suspend fun getItemList(collection: String): MutableList<UniversalRegionItem>{
        var list = mutableListOf<UniversalRegionItem>()
        val snapshot = db.collection("regions").whereEqualTo("regionName", regionName).get().await()
        if (!snapshot.isEmpty) {
            val document = snapshot.documents[0]
            val itemSnapshot = document.reference.collection(collection).get().await()
            if (!itemSnapshot.isEmpty) {
                list.clear()
                for (item in itemSnapshot.documents) {
                    val itemData = item.data
                    val curTitle = itemData?.get("itemName").toString()
                    val curDescr = itemData?.get("itemDescription").toString()
                    val coordinates = itemData?.get("itemCoordinates").toString()
                    val url = itemData?.get("itemImageUrl")
                    if (url != null){
                        val curImageUrl = url.toString()
                        list.add(UniversalRegionItem(curTitle, curDescr, curImageUrl, coordinates = coordinates))
                    }
                    else{
                        val curImageUrl = url
                        list.add(UniversalRegionItem(curTitle, curDescr, curImageUrl, coordinates = coordinates))
                    }
                    Log.d("Firestore", "Sight data: $itemData")
                }
                list.sortBy { it.title }
            }
        }
        return list
    }

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    private fun populateCards(container: android.widget.LinearLayout, someList: MutableList<UniversalRegionItem>, objectType: Card) {
        val inflater = LayoutInflater.from(MAIN2)
        val scaleDownValue = 0.97f
        val scaleUpValue = 1f
        val animationDuration = 150L
        if (container.size >= 2){
            return
        }
        fun setupButton(continueButton: Button, item: UniversalRegionItem, view: View){
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

            continueButton.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.tag = true
                        view.animate()
                            .scaleX(scaleDownValue)
                            .scaleY(scaleDownValue)
                            .setDuration(animationDuration)
                            .withStartAction {
                                if (Build.VERSION.SDK_INT >= 26) {
                                    vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                                } else {
                                    vibrator?.vibrate(50)
                                }
                            }
                            .start()
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val rect = Rect()
                        v.getGlobalVisibleRect(rect)

                        val isInside = rect.contains(event.rawX.toInt(), event.rawY.toInt())

                        if (!isInside) {
                            v.tag = false
                            v.post {
                                v.isPressed = false
                                v.jumpDrawablesToCurrentState()
                            }
                            view.animate()
                                .scaleX(scaleUpValue)
                                .scaleY(scaleUpValue)
                                .setDuration(animationDuration)
                                .start()
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (v.tag as? Boolean == true) {
                            v.post {
                                v.isPressed = false
                                v.jumpDrawablesToCurrentState()
                            }
                            view.animate()
                                .scaleX(scaleUpValue)
                                .scaleY(scaleUpValue)
                                .setDuration(animationDuration)
                                .withEndAction {
                                    onButtonClick(item, objectType)
                                }
                                .start()
                        } else {
                            view.animate()
                                .scaleX(scaleUpValue)
                                .scaleY(scaleUpValue)
                                .setDuration(animationDuration)
                                .start()
                        }
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        v.post {
                            v.isPressed = false
                            v.jumpDrawablesToCurrentState()
                        }
                        view.animate()
                            .scaleX(scaleUpValue)
                            .scaleY(scaleUpValue)
                            .setDuration(animationDuration)
                            .start()
                        true
                    }
                    else -> false
                }
            }
        }
        when(objectType){
            Card.Excursion -> {
                for (item in someList) {
                    val view = inflater.inflate(R.layout.sight_item_layout, container, false)
                    view.findViewById<TextView>(R.id.event_title).text = item.title
                    view.findViewById<TextView>(R.id.event_description).text = item.description
                    val imageView: ImageView = view.findViewById(R.id.event_image)
                    val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
                    val constraintCard: ConstraintLayout = view.findViewById(R.id.constraintCard)
                    val continueButton: Button = view.findViewById(R.id.buttonToContinue)
                    continueButton.text = "Записаться"

                    if (item.imageUrl != null){
                        loadImageWithRetry(imageView, item.imageUrl, progressBar)
                    }
                    else{
                        constraintCard.visibility = View.GONE
                    }
                    view.setOnClickListener { onViewClick(item) }

                    setupButton(continueButton, item, view)

                    container.addView(view)
                }
            }
            Card.Event -> {
                binding.progressBarEvents.visibility = View.GONE
                val view = inflater.inflate(R.layout.sight_item_layout, container, false)
                view.findViewById<TextView>(R.id.event_title).text = "Находится в разработке"
                view.findViewById<TextView>(R.id.event_title).setTextColor(Color.BLACK)
                val continueButton: Button = view.findViewById(R.id.buttonToContinue)
                val constraintCard: ConstraintLayout = view.findViewById(R.id.constraintCard)
                view.findViewById<TextView>(R.id.event_description).visibility = View.GONE
                constraintCard.visibility = View.GONE
                continueButton.visibility = View.GONE
                container.addView(view)
                return
            }
            Card.Sight -> {
                for (item in someList) {
                    val view = inflater.inflate(R.layout.item_design_sight, container, false)
                    view.findViewById<TextView>(R.id.sightName).text = item.title
                    val textViewDescr = view.findViewById<TextView>(R.id.miniDescriptionSight)
                    if (item.description != "null") {
                        textViewDescr.text = item.description
                    }
                    else{
                        textViewDescr.visibility = View.GONE
                    }
                    val imageView: ImageView = view.findViewById(R.id.sightImage)
                    val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
                    val constraintCard: ConstraintLayout = view.findViewById(R.id.constraintCard)
                    val continueButton: Button = view.findViewById(R.id.detailSightButton)
                    val textViewCord = view.findViewById<TextView>(R.id.coordinatesSight)
                    val mapView = view.findViewById<MapView>(R.id.mapview)

                    if (item.coordinates != "null") {
                        textViewCord.text = item.coordinates
                        mapView.apply {
                            mapWindow.map.isScrollGesturesEnabled = false
                            mapWindow.map.isZoomGesturesEnabled = false
                            mapWindow.map.isRotateGesturesEnabled = false
                            mapWindow.map.isTiltGesturesEnabled = false
                            onStart()
                            setupSightPoint(this, item.coordinates!!)
                        }
                    }
                    else{
                        textViewCord.visibility = View.GONE
                        mapView.visibility = View.GONE
                        view.findViewById<ImageView>(R.id.imageViewIconCord).visibility = View.GONE
                    }
                    if (item.imageUrl != null){
                        loadImageWithRetry(imageView, item.imageUrl, progressBar)
                    }
                    else{
                        constraintCard.visibility = View.GONE
                    }

                    view.setOnClickListener { onViewClick(item) }
                    setupButton(continueButton, item, view)

                    container.addView(view)
                }
            }
            Card.Competition -> {
                for (item in someList) {
                    val view = inflater.inflate(R.layout.item_design_compet, container, false)
                    view.findViewById<TextView>(R.id.competTitle).text = item.title.substringBefore(" (")
                    view.findViewById<TextView>(R.id.miniDescriptionCompet).text = item.description
                    view.findViewById<TextView>(R.id.locationText).text = item.title.substringAfter(" (").substringBeforeLast(")")
                    val continueButton: Button = view.findViewById(R.id.sendButton)

                    view.setOnClickListener { onViewClick(item) }
                    setupButton(continueButton, item, view)

                    container.addView(view)
                }
            }
        }
    }

    private fun onViewClick(item: UniversalRegionItem) {
        val toast = Toast.makeText(MAIN2, item.title, Toast.LENGTH_SHORT)
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, 1000)
    }

    @SuppressLint("NewApi")
    private fun onButtonClick(item: UniversalRegionItem, objectType: Card) {
        var cardName = item.title
        bundle = Bundle()
        bundle.putString("CardName", item.title)
        bundle.putSerializable("CardType", objectType)
        //(activity as? TransitionActivity)?.goFragment("Map", SettingsFragment(), bundle) //!!!
        when (objectType){
            Card.Excursion -> {
                db.collection("excursionApplications").whereEqualTo("userId", auth.currentUser!!.uid)
                    .whereEqualTo("cardName", cardName)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            val builder = AlertDialog.Builder(MAIN2)
                            builder.setTitle("Экскурсия")
                                .setMessage("Вы уверены, что хотите записаться на эскурсию «${item.title}»?")
                            builder.setNegativeButton("Назад") { dialog, which ->

                            }
                            builder.setPositiveButton("Да") { dialog, which ->
                                val myDate = DateTimeUtils.getMoscowTime()
                                db.collection("excursionApplications").whereEqualTo("userId", auth.currentUser!!.uid)
                                    .whereEqualTo("cardName", cardName)
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        val application = hashMapOf(
                                            "userId" to auth.currentUser!!.uid,
                                            "cardName" to cardName,
                                            "applicationDate" to myDate,
                                        )
                                        db.collection("excursionApplications")
                                            .add(application)
                                            .addOnSuccessListener {
                                                Snackbar.make(requireView(), "Запись сохранена!", 2000)
                                                    .setAction("Отменить") {
                                                        lifecycleScope.launch {
                                                            val snapshot =
                                                                db.collection("excursionApplications")
                                                                    .whereEqualTo("userId", auth.currentUser!!.uid)
                                                                    .whereEqualTo("cardName", cardName).get().await()
                                                            if (!snapshot.isEmpty) {
                                                                val document = snapshot.documents[0]
                                                                document.reference.delete()
                                                            }
                                                        }
                                                    }.setBackgroundTint(resources.getColor(R.color.black, null)).setTextColor(resources.getColor(R.color.greenSuccess, null)).setActionTextColor(resources.getColor(R.color.discard, null))
                                                    .show()
                                                Log.d(TAG, "Excursion application for ${cardName}added ")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w(TAG, "Error adding document", e)
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        //!
                                        Log.w(TAG, "Error getting documents: ", e)
                                    }
                            }
                            val alertDialog = builder.create()
                            alertDialog.show()
                        } else {
                            Log.d(TAG, "Excursion application on $cardName already exists.")
                            showSnackbar(requireView(), "Вы уже записаны на эту экскурсию")
                        }
                    }
                    .addOnFailureListener { e ->
                        //!
                        Log.w(TAG, "Error getting documents: ", e)
                    }
            }
            Card.Event -> {}
            Card.Sight -> {}
            Card.Competition -> {
                db.collection("competitiveApplications").whereEqualTo("userId", auth.currentUser!!.uid)
                    .whereEqualTo("cardName", cardName)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            (activity as? TransitionActivity)?.goFragment("Map", CompetitionFragment(), bundle)
                        } else {
                            Log.d(TAG, "Competitive application on $cardName already exists.")
                            showSnackbar(requireView(), "Вы уже отправили заявку")
                        }
                    }
                    .addOnFailureListener { e ->
                        //!
                        Log.w(TAG, "Error getting documents: ", e)
                    }
            }
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
                lifecycleScope.launch {
                    var excursionItems = getItemList("excursions")
                    binding.progressBarExcursions.visibility = View.GONE
                    populateCards(binding.LinearLayoutExcursionsContainer, excursionItems, Card.Excursion)
                }
                binding.buttonExcursions.setCompoundDrawablesWithIntrinsicBounds(R.drawable.top_arrow, 0, 0, 0)
                binding.LinearLayoutExcursionsContainer.visibility = View.VISIBLE
                if (binding.LinearLayoutExcursionsContainer.size >= 2){
                    binding.progressBarExcursions.visibility = View.GONE
                }
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
                lifecycleScope.launch {
                    var eventItems = getItemList("events")
                    binding.progressBarEvents.visibility = View.GONE
                    populateCards(binding.LinearLayoutEventsContainer, eventItems, Card.Event)
                }
                binding.buttonEvents.setCompoundDrawablesWithIntrinsicBounds(R.drawable.top_arrow, 0, 0, 0)
                binding.LinearLayoutEventsContainer.visibility = View.VISIBLE
                if (binding.LinearLayoutEventsContainer.size >= 2){
                    binding.progressBarEvents.visibility = View.GONE
                }
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
                lifecycleScope.launch {
                    var sightItems = getItemList("sights")
                    binding.progressBarSights.visibility = View.GONE
                    populateCards(binding.LinearLayoutSightsContainer, sightItems, Card.Sight)
                }
                binding.buttonSights.setCompoundDrawablesWithIntrinsicBounds(R.drawable.top_arrow, 0, 0, 0)
                binding.LinearLayoutSightsContainer.visibility = View.VISIBLE
                if (binding.LinearLayoutSightsContainer.size >= 2){
                    binding.progressBarSights.visibility = View.GONE
                }
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
                lifecycleScope.launch {
                    var competitionItems = getItemList("competitions")
                    binding.progressBarCompetitions.visibility = View.GONE
                    populateCards(binding.LinearLayoutCompetitionsContainer, competitionItems, Card.Competition)
                }
                binding.buttonLocalCompetitions.setCompoundDrawablesWithIntrinsicBounds(R.drawable.top_arrow, 0, 0, 0)
                binding.LinearLayoutCompetitionsContainer.visibility = View.VISIBLE
                if (binding.LinearLayoutCompetitionsContainer.size >= 2){
                    binding.progressBarCompetitions.visibility = View.GONE
                }
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
        WindowUtils.setNavigationBarColor(requireActivity(), R.color.white)
        WindowUtils.setStatusBarColor(requireActivity(), R.color.mainGreen)
    }


    private fun setupSightPoint(mapView: MapView, cords: String){
        val listCords = cords.split(",")
        val sightPoint = Point(listCords[0].toDouble(), listCords[1].toDouble())

        val map = mapView.mapWindow.map
        map.move(CameraPosition(sightPoint, 17.25f, 0f, 0f))
        val markersCollection = map.mapObjects.addCollection()

        fun addBeautifulPlacemark(point: Point, context: Context) {
            markersCollection.addPlacemark().apply {
                geometry = point
                setIcon(ImageProvider.fromResource(context, R.drawable.place))
                val iconStyle = IconStyle().apply {
                    anchor = PointF(0.5f, 1.0f)
                    scale = 0.04f
                    zIndex = 10f
                }
                setIconStyle(iconStyle)
            }
        }
        //addBeautifulPlacemark(sightPoint, MAIN2)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        /*if (::mapView.isInitialized) {
            mapView.onStop()*/
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        MapKitFactory.getInstance().onStop()
        super.onDestroyView()
    }

    fun showSnackbar(view: View, text: String) {
        Snackbar.make(view, text, 1000)
            .setAction("К записям") {
                (activity as? TransitionActivity)?.goToAccount()
            }.setBackgroundTint(resources.getColor(R.color.black, null)).setTextColor(resources.getColor(R.color.white, null)).setActionTextColor(resources.getColor(R.color.neutralBlue, null))
            .show()
    }
}