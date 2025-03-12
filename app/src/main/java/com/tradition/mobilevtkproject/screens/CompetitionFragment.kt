package com.tradition.mobilevtkproject.screens

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.Card
import com.tradition.mobilevtkproject.Level
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
            val db = Firebase.firestore
            db.collection("competitiveApplications")
                .whereEqualTo("userId", curId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val application = hashMapOf(
                            "authId" to idd,
                            "email" to currentUser.email,
                            "accessLevel" to Level.RegularUser,
                            "name" to currentUser.name,
                            "surname" to currentUser.surname,
                            "age" to currentUser.age,
                            "balance" to 0
                        )
                        db.collection("users")
                            .add(user)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot added with ID: ${currentUser.email}")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    } else {
                        Log.d(TAG, "User with email ${currentUser.email} already exists.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error getting documents: ", e)
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