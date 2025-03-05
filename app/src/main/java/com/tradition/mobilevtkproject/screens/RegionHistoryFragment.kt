package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentRegionHistoryBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegionHistoryFragment : Fragment() {

    lateinit var binding: FragmentRegionHistoryBinding
    val db = Firebase.firestore
    var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegionHistoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val regionName = arguments?.getString("RegionName")
        var regionHistory = ""
        binding.textViewEventTitle.text = "История [$regionName]"
        lifecycleScope.launch {
            val snapshot = db.collection("regions").whereEqualTo("regionName", regionName).get().await()
            if (!snapshot.isEmpty) {
                val document = snapshot.documents[0]
                regionHistory = document.get("regionHistory", String::class.java)!!
                binding.textViewHistoryInformation.text = regionHistory
            }
        }
        binding.imageButtonBack.setOnClickListener {
            (activity as? TransitionActivity)?.onBackPressed()
        }
        }
    }