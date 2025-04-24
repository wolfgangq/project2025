package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.ApplicationItem
import com.tradition.mobilevtkproject.Card
import com.tradition.mobilevtkproject.FirestoreRegionInitializer
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.data.RegionDataBolguri
import com.tradition.mobilevtkproject.data.repository.impl.FirebaseUserRepository
import com.tradition.mobilevtkproject.databinding.FragmentAccountBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AccountFragment : Fragment() {

    lateinit var binding: FragmentAccountBinding
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser!!.uid
    var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        lifecycleScope.launch {
            var currentUser = FirebaseUserRepository().getUserInfo(userId)
            if (currentUser?.get("accessLevel") == "Creator") {
                binding.buttonAdminRecreate.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launch {
            var excursionApplications = findApplications("excursionApplications")
            populateCards(binding.linearLayoutExcursions, excursionApplications, Card.Excursion)
            var competitiveApplications = findApplications("competitiveApplications")
            populateCards(binding.linearLayoutCompetitions, competitiveApplications, Card.Competition)
        }

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonSettings.setOnClickListener{
            (activity as? TransitionActivity)?.goFragment("Account", SettingsFragment(), null)
        }
        binding.buttonLogOut.setOnClickListener{
            val builder = AlertDialog.Builder(MAIN2)
            builder.setTitle("Аккаунт")
                .setMessage("Вы уверены, что хотите выйти из аккаунта?")
            builder.setNegativeButton("Назад") { dialog, which ->

            }.setPositiveButton("Выйти") { dialog, which ->
                auth.signOut()
                val intent = Intent(MAIN2, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                MAIN2.startActivity(intent)
                Toast.makeText(MAIN2, "Вы вышли из аккаунта", Toast.LENGTH_LONG).show()
            }.show()
        }

        binding.buttonAdminRecreate.setOnClickListener{
            lifecycleScope.launch {
                var currentUser = FirebaseUserRepository().getUserInfo(userId)
                if (currentUser?.get("accessLevel") == "Creator") {
                    Toast.makeText(MAIN2, "Информация муниципалитетов пересоздана", Toast.LENGTH_SHORT).show()
                    FirestoreRegionInitializer().initializeRegion("Болгуры", RegionDataBolguri.SHORT_DESCRIPTION, RegionDataBolguri.HISTORY, RegionDataBolguri.EXCURSIONS_LIST,
                        RegionDataBolguri.EVENTS_LIST, RegionDataBolguri.SIGHTS_LIST, RegionDataBolguri.COMPETITIONS_LIST)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        WindowUtils.setNavigationBarColor(requireActivity(), R.color.white)
        WindowUtils.setStatusBarColor(requireActivity(), R.color.mainGreen)
    }

    suspend fun findApplications(collection: String): MutableList<ApplicationItem> {
        var list = mutableListOf<ApplicationItem>()
        try {
            val snapshot = db.collection(collection).whereEqualTo("userId", auth.currentUser!!.uid).get().await()
            if (!snapshot.isEmpty) {
                for (application in snapshot.documents) {
                    val excursionData = application.data
                    val cardTitle = excursionData?.get("cardName").toString()
                    val applicationDate = excursionData?.get("applicationDate").toString()
                    val url = excursionData?.get("sentUrl")
                    if (url != null){
                        val curImageUrl = url.toString()
                        list.add(ApplicationItem(cardTitle, applicationDate, curImageUrl))
                    }
                    else{
                        list.add(ApplicationItem(cardTitle, applicationDate, null))
                    }
                }
                list.sortBy { it.title }
            } else {
                Log.d("Firestore", "No applications found for collection $collection")
            }
        }
        catch (e: Exception) {
            Log.w("Firestore", "Error updating document.", e)
        }
        return list
    }

    @SuppressLint("MissingInflatedId")
    private fun populateCards(container: LinearLayout, someList: MutableList<ApplicationItem>, objectType: Card) {
        val inflater = LayoutInflater.from(MAIN2)
        container.removeAllViews()


        for (item in someList) {
            val view = inflater.inflate(R.layout.application_item_layout, container, false)
            view.findViewById<TextView>(R.id.textViewSubjectName).text = item.title.substringBefore("(")
            view.findViewById<TextView>(R.id.textViewDate).text = item.date.substringBefore(" ")
            //val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
            val deleteButton: Button = view.findViewById(R.id.buttonDeleteApplication)
            val density = requireContext().resources.displayMetrics.density
            deleteButton.viewTreeObserver.addOnGlobalLayoutListener {
                deleteButton.layoutParams.height = deleteButton.width
                deleteButton.requestLayout()
            }
            val textViewUrl: TextView = view.findViewById<TextView>(R.id.textViewUrl)

            if (item.imageUrl != null){
                textViewUrl.text = item.imageUrl
            }
            else{
                textViewUrl.visibility = View.GONE
            }
            container.addView(view)

            val divider = MaterialDivider(MAIN2)
            val layoutParams = ViewGroup.MarginLayoutParams(view.layoutParams.width, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins((10*density).toInt(), 0, (10*density).toInt(), 0)
            divider.layoutParams = layoutParams
            divider.dividerColor = Color.BLACK
            if (item != someList.last()){
                container.addView(divider)
            }
            deleteButton.setOnClickListener { onButtonClick(item, objectType, container, someList) }
        }

    }

    private fun onButtonClick(item: ApplicationItem, objectType: Card, container: LinearLayout, someList: MutableList<ApplicationItem>) {
        when (objectType){
            Card.Excursion -> deleteApplication(item, "excursionApplications", "Запись удалена!", objectType, container, someList)
            Card.Competition -> deleteApplication(item, "competitiveApplications", "Заявка удалена!", objectType, container, someList)
            else -> {}
        }
    }

    private fun deleteApplication(item: ApplicationItem, collection: String, onDeleteText: String, objectType: Card, container: LinearLayout, someList: MutableList<ApplicationItem>){
        val builder = AlertDialog.Builder(MAIN2)
        builder.setTitle("Экскурсия")
            .setMessage("Вы уверены, что хотите удалить конкурсную заявку?")
        builder.setNegativeButton("Назад") { dialog, which ->

        }
        builder.setPositiveButton("Да") { dialog, which ->
            lifecycleScope.launch {
                val snapshot = db.collection(collection)
                    .whereEqualTo("cardName", item.title).whereEqualTo("userId", userId).get().await()
                if (!snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    document.reference.delete().addOnSuccessListener{
                        someList.remove(item)
                        populateCards(container, someList, objectType)
                        Snackbar.make(requireView(), onDeleteText, 1000)
                            /*.setAction("Отменить") {
                                lifecycleScope.launch {
                                    when (objectType){
                                        Application.Excursion -> {
                                            val application = hashMapOf(
                                                "userId" to auth.currentUser!!.uid,
                                                "cardName" to item.title,
                                                "applicationDate" to item.date
                                            )
                                            db.collection(collection).add(application).addOnSuccessListener{
                                                container.addView(view)
                                            }
                                        }
                                        Application.Competition -> {
                                            val application = hashMapOf(
                                                "userId" to auth.currentUser!!.uid,
                                                "cardName" to item.title,
                                                "applicationDate" to item.date,
                                                "sentUrl" to item.imageUrl
                                            )
                                            db.collection(collection).add(application).addOnSuccessListener{
                                                container.addView(view)
                                            }
                                        }
                                    }
                                }
                            }*/.setBackgroundTint(resources.getColor(R.color.black, null)).setTextColor(resources.getColor(R.color.neutralBlue, null)).setActionTextColor(resources.getColor(R.color.discard, null))
                            .show()
                    }
                }
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

}