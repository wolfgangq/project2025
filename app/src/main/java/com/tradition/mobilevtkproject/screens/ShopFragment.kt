package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.databinding.FragmentShopBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.tradition.mobilevtkproject.MainActivity.Companion.getUserInfo
import com.tradition.mobilevtkproject.MainActivity.Companion.setLightStatusBar
import com.tradition.mobilevtkproject.TransitionActivity.Companion.setColors

class ShopFragment : Fragment() {

    lateinit var binding: FragmentShopBinding
    val db = Firebase.firestore
    var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            val auth = FirebaseAuth.getInstance()
            var id = auth.currentUser?.uid.toString()
            var currentUser = getUserInfo(id)
            binding.textViewBalance.text = "Ваш баланс: ${currentUser?.get("balance")} зернышек"
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.navigationBarColor = Color.WHITE
        setColors(requireActivity(), "mainGreen")
    }
}