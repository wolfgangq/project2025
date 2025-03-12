package com.tradition.mobilevtkproject.screens

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.Card
import com.tradition.mobilevtkproject.Level
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth
import com.tradition.mobilevtkproject.TransitionActivity.Companion.setColors
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentCompetitionBinding

class CompetitionFragment : Fragment() {

    lateinit var binding: FragmentCompetitionBinding
    val auth = Firebase.auth
    val curId = auth.currentUser!!.uid
    val cardType = arguments?.getSerializable("cardType")
    val cardName = arguments?.getString("cardName")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompetitionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    val bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.buttonSendUrl.setOnClickListener {
            if (binding.editTextUrl.text.toString().trim() != "") {
                //val localDateTime: java.time.LocalDateTime = java.time.LocalDateTime.now()
                val db = Firebase.firestore
                db.collection("competitive")
                    .whereEqualTo("cardName", cardName)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val application = hashMapOf(
                                "userId" to curId,
                                "cardName" to cardName,
                                //"applicationDate" to 123,
                                "sentUrl" to binding.editTextUrl.text.toString()
                            )
                            db.collection("competitive")
                                .add(application)
                                .addOnSuccessListener {
                                    Toast.makeText(MAIN2, "Запись сохранена!", Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, "DocumentSnapshot added with ID: ")
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(MAIN2, "1", Toast.LENGTH_SHORT).show()
                                    Log.w(TAG, "Error adding document", e)
                                }
                        } else {
                            Log.d(TAG, "User already exists.")
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(MAIN2, "22", Toast.LENGTH_SHORT).show()
                        Log.w(TAG, "Error getting documents: ", e)
                    }
            }
            else{
                Toast.makeText(MAIN2, "Введите корректную ссылку", Toast.LENGTH_SHORT).show()
            }
        }*/
        binding.imageButton.setOnClickListener{
            (activity as? TransitionActivity)?.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        setColors(requireActivity(), "desert")
    }



}