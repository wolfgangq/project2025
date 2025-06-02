package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.UniversalRegionItem
import com.tradition.mobilevtkproject.adapter.ItemCompetitionAdapter
import com.tradition.mobilevtkproject.adapter.ItemEventAdapter
import com.tradition.mobilevtkproject.adapter.ItemExcursionAdapter
import com.tradition.mobilevtkproject.adapter.ItemSightAdapter
import com.tradition.mobilevtkproject.data.repository.impl.ActivitiesRepositoryImpl
import com.tradition.mobilevtkproject.databinding.FragmentRegionActivitiesBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.pow

class RegionActivitiesFragment : Fragment() {
    lateinit var binding: FragmentRegionActivitiesBinding

    lateinit var regionName: String
    val auth = Firebase.auth
    val db = Firebase.firestore
    var excursionItems: List<UniversalRegionItem> = emptyList()
    var eventItems: List<UniversalRegionItem> = emptyList()
    var sightItems: List<UniversalRegionItem> = emptyList()
    var competitionItems: List<UniversalRegionItem> = emptyList()
    val adapterExcursionItems = ItemExcursionAdapter(::onViewClick, ::actionExcursionBook)
    val adapterEventItems = ItemEventAdapter(::onViewClick, {})
    val adapterSightItems = ItemSightAdapter(::onViewClick, {})
    val adapterCompetitionItems = ItemCompetitionAdapter(::onViewClick, ::actionCompetitionToSend)



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        regionName = arguments?.getString("RegionName").toString()
        binding = FragmentRegionActivitiesBinding.inflate(layoutInflater, container, false)

        binding.recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewItems.setRecycledViewPool(RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(0,3)   // одновременно не более 3 карт в пуле
        })

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            tab?.view?.isClickable = false
            tab?.view?.isFocusable = false
        }
        lifecycleScope.launch {
            try {
                val snapshot = db.collection("regions").whereEqualTo("regionName", regionName).get().await()
                if (!snapshot.isEmpty) {
                    excursionItems = ActivitiesRepositoryImpl().getItemList(regionName, "excursions")
                    binding.recyclerViewItems.adapter = adapterExcursionItems
                    adapterExcursionItems.submitList(excursionItems)
                    eventItems = ActivitiesRepositoryImpl().getItemList(regionName, "events")
                    sightItems = ActivitiesRepositoryImpl().getItemList(regionName, "sights")
                    competitionItems = ActivitiesRepositoryImpl().getItemList(regionName, "competitions")

                    setupTabs()
                }
            }
            catch (e: Exception) {
                Log.w("Firestore", "Error updating document.", e)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }


    private fun setupTabs() {
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            tab?.view?.isClickable = true
            tab?.view?.isFocusable = true
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    when (position) {
                        0 -> {
                            binding.recyclerViewItems.adapter = adapterExcursionItems
                            adapterExcursionItems.submitList(excursionItems)
                        }
                        1 -> {
                            binding.recyclerViewItems.adapter = adapterEventItems
                            adapterEventItems.submitList(eventItems)
                        }
                        2 -> {
                            binding.recyclerViewItems.adapter = adapterSightItems
                            adapterSightItems.submitList(sightItems)
                        }
                        3 -> {
                            binding.recyclerViewItems.adapter = adapterCompetitionItems
                            adapterCompetitionItems.submitList(competitionItems)
                        }
                    }
                }
                if (binding.recyclerViewItems.adapter!!.itemCount == 0) {showEmptyState(true)}
                else {showEmptyState(false)}
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }
        })
    }

    private fun actionExcursionBook(excursion: UniversalRegionItem) {
        val cardName = excursion.title

        db.collection("excursionApplications")
            .whereEqualTo("userId", auth.currentUser?.uid)
            .whereEqualTo("cardName", cardName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val dialog = AlertDialog.Builder(requireContext())
                        .setTitle("Экскурсия")
                        .setMessage("Вы уверены, что хотите записаться на экскурсию «${excursion.title}»?")
                        .setNegativeButton("Назад", null)
                        .setPositiveButton("Да") { _, _ ->
                            val myDate = com.tradition.mobilevtkproject.utils.DateTimeUtils.getMoscowTime()
                            val application = hashMapOf(
                                "userId" to auth.currentUser?.uid,
                                "cardName" to cardName,
                                "applicationDate" to myDate
                            )

                            db.collection("excursionApplications")
                                .add(application)
                                .addOnSuccessListener {
                                    Snackbar.make(binding.root, "Запись сохранена!", Snackbar.LENGTH_LONG)
                                        .setAction("Отменить") {
                                            lifecycleScope.launch {
                                                val snapshot = db.collection("excursionApplications")
                                                    .whereEqualTo("userId", auth.currentUser?.uid)
                                                    .whereEqualTo("cardName", cardName)
                                                    .get()
                                                    .await()

                                                if (!snapshot.isEmpty) {
                                                    snapshot.documents[0].reference.delete()
                                                }
                                            }
                                        }
                                        .setBackgroundTint(resources.getColor(R.color.black, null))
                                        .setTextColor(resources.getColor(R.color.greenSuccess, null))
                                        .setActionTextColor(resources.getColor(R.color.discard, null))
                                        .show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error adding document", e)
                                    showError("Ошибка при записи на экскурсию")
                                }
                        }
                        .create()
                    dialog.show()
                } else {
                    showSnackbar("Вы уже записаны на эту экскурсию")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error checking applications", e)
                showError("Ошибка при проверке записей")
            }
    }

    private fun actionCompetitionToSend(competition: UniversalRegionItem) {
        val bundle = Bundle()
        bundle.putString("CardName", competition.title)
        db.collection("competitiveApplications").whereEqualTo("userId", auth.currentUser!!.uid)
            .whereEqualTo("cardName", competition.title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    (activity as? TransitionActivity)?.goFragment("Map", CompetitionFragment(), bundle)
                } else {
                    Log.d(TAG, "Competitive application on ${competition.title} already exists.")
                    showSnackbar("Вы уже отправили заявку")
                }
            }
            .addOnFailureListener { e ->
                //!
                Log.w(TAG, "Error getting documents: ", e)
            }
    }

    private fun onViewClick(item: UniversalRegionItem) {
        val toast = Toast.makeText(MAIN2, item.title, Toast.LENGTH_SHORT)
        toast.show()
        Handler(Looper.getMainLooper()).postDelayed({
            toast.cancel()
        }, 1000)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("К записям") {
                (activity as? TransitionActivity)?.goToAccount()
            }
            .setBackgroundTint(resources.getColor(R.color.black, null))
            .setTextColor(resources.getColor(R.color.white, null))
            .setActionTextColor(resources.getColor(R.color.neutralBlue, null))
            .show()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmptyState(show: Boolean) {
        binding.emptyView.root.visibility = if (show) View.VISIBLE else View.GONE
        binding.recyclerViewItems.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        WindowUtils.setNavigationBarColor(requireActivity(), R.color.white)
        WindowUtils.setStatusBarColor(requireActivity(), R.color.mainGreen)
    }

    override fun onStart() {
        super.onStart()
        val mapKit = MapKitFactory.getInstance()
        mapKit.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        MapKitFactory.getInstance().onStop()
        super.onDestroyView()
    }

    companion object {
        fun setupSightPoint(mapView: MapView, cords: String){
            val listCords = cords.split(",")
            val sightPoint = Point(listCords[0].toDouble(), listCords[1].toDouble())

            val map = mapView.mapWindow.map
            map.move(CameraPosition(sightPoint, 17.25f, 0f, 0f))
        }

        @SuppressLint("ClickableViewAccessibility")
        fun doButtonActionWithVibrate(button: Button, view: View, onAction: () -> Unit) {
            val scaleDownValue = 0.97f
            val scaleUpValue = 1f
            val animationDuration = 150L
            val vibrator = MAIN2.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

            button.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.tag = true
                        view.animate()
                            .scaleX(scaleDownValue)
                            .scaleY(scaleDownValue)
                            .setDuration(animationDuration)
                            .withStartAction {
                                if (Build.VERSION.SDK_INT >= 26) {
                                    vibrator?.vibrate(
                                        VibrationEffect.createOneShot(
                                            50,
                                            VibrationEffect.DEFAULT_AMPLITUDE
                                        )
                                    )
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
                                    onAction()
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

        fun loadImageWithRetry(imageView: ImageView, imageUrl: String?, progressBar: ProgressBar, attempt: Int = 1, MAX_ATTEMPTS: Int = 10) {
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
                            target: com.bumptech.glide.request.target.Target<Drawable?>,
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
                            target: com.bumptech.glide.request.target.Target<Drawable?>,
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
    }
}