package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.data.repository.impl.FirebaseUserRepository
import com.tradition.mobilevtkproject.databinding.FragmentShopBinding
import kotlinx.coroutines.launch

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
            var currentUser = FirebaseUserRepository().getUserInfo(id)
            binding.textViewBalance.text = "Ваш баланс: ${currentUser?.get("balance")} зернышек"
        }
    }

    override fun onResume() {
        super.onResume()
        WindowUtils.setNavigationBarColor(requireActivity(), R.color.white)
        WindowUtils.setStatusBarColor(requireActivity(), R.color.mainGreen)
    }
}