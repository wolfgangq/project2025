package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentRegionBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegionFragment : Fragment() {

    lateinit var binding: FragmentRegionBinding
    val db = Firebase.firestore
    lateinit var regionName: String
    var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        regionName = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (regionName == ""){
            regionName = arguments?.getString("RegionName").toString()
        }
        binding.imageButtonBack.setOnClickListener {
            (activity as? TransitionActivity)?.onBackPressed()
        }
        binding.buttonEvent.setOnClickListener{
            bundle.putString("RegionName", regionName)
            (activity as? TransitionActivity)?.goFragment("Map", RegionEventFragment(), bundle)
        }
        binding.buttonHistory.setOnClickListener{
            bundle.putString("RegionName", regionName)
            (activity as? TransitionActivity)?.goFragment("Map", RegionHistoryFragment(), bundle)
        }
        binding.textViewRegionName.text = regionName
        var desc = ""
        lifecycleScope.launch {
            val snapshot = db.collection("regions").whereEqualTo("regionName", regionName).get().await()
            if (!snapshot.isEmpty) {
                val document = snapshot.documents[0]
                desc = document.get("regionDescription", String::class.java)!!
                binding.textViewInformation.text = desc
            }
        }
    }
}