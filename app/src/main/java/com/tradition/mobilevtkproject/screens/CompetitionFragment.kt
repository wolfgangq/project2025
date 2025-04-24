package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentCompetitionBinding
import com.tradition.mobilevtkproject.utils.DateTimeUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CompetitionFragment : Fragment() {

    lateinit var binding: FragmentCompetitionBinding
    val db = Firebase.firestore
    val auth = Firebase.auth
    val curId = auth.currentUser!!.uid


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompetitionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    val bundle = Bundle()

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //@Suppress("DEPRECATION")
        //val cardType = arguments?.getSerializable("CardType") as Card
        val cardName = arguments?.getString("CardName")!!.trim()

        binding.buttonSendUrl.setOnClickListener {
            hideKeyboard(view)
            if (binding.editTextUrl.text.toString().trim() != "") {
                val myDate = DateTimeUtils.getMoscowTime()
                db.collection("competitiveApplications")
                    .whereEqualTo("userId", auth.currentUser!!.uid)
                    .whereEqualTo("cardName", cardName).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            val application = hashMapOf(
                                "userId" to curId,
                                "cardName" to cardName,
                                "applicationDate" to myDate,
                                "sentUrl" to binding.editTextUrl.text.toString()
                            )
                            db.collection("competitiveApplications")
                                .add(application)
                                .addOnSuccessListener {
                                    Snackbar.make(requireView(), "Запись сохранена!", 2000)
                                        .setAction("Отменить") {
                                            lifecycleScope.launch {
                                                val snapshot =
                                                    db.collection("competitiveApplications")
                                                        .whereEqualTo("userId", auth.currentUser!!.uid)
                                                        .whereEqualTo("cardName", cardName).get().await()
                                                if (!snapshot.isEmpty) {
                                                    val document = snapshot.documents[0]
                                                    document.reference.delete()
                                                }
                                            }
                                        }.setBackgroundTint(resources.getColor(R.color.black, null)).setTextColor(resources.getColor(R.color.greenSuccess, null)).setActionTextColor(resources.getColor(R.color.discard, null))
                                        .show()
                                    Log.d(TAG, "Competitive application for ${cardName }added ")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        } else {
                            Log.d(TAG, "Competitive application for $cardName already exists.")
                            Snackbar.make(view, "Вы уже отправили заявку", 1000)
                                .setAction("К записям") {
                                    (activity as? TransitionActivity)?.goToAccount()
                                }.setBackgroundTint(resources.getColor(R.color.black, null)).setTextColor(resources.getColor(R.color.white, null)).setActionTextColor(resources.getColor(R.color.neutralBlue, null))
                                .show()
                        }
                    }
                    .addOnFailureListener { e ->
                        //!
                        Log.w(TAG, "Error getting documents: ", e)
                    }
            }
            else{
                Toast.makeText(MAIN2, "Введите корректную ссылку", Toast.LENGTH_SHORT).show()
            }
        }
        binding.imageButton.setOnClickListener{
            (activity as? TransitionActivity)?.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        WindowUtils.setStatusBarColor(requireActivity(), R.color.desert)
    }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



}