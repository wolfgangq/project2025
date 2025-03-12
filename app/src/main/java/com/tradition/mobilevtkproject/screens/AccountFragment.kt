package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.setLightStatusBar
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.TransitionActivity.Companion.setColors
import com.tradition.mobilevtkproject.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    lateinit var binding: FragmentAccountBinding
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    var bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageButtonSettings.setOnClickListener{
            (activity as? TransitionActivity)?.goFragment("Account", SettingsFragment(), null)
        }
        binding.buttonLogOut.setOnClickListener{
            auth.signOut()
            val intent = Intent(MAIN2, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            MAIN2.startActivity(intent)
            Toast.makeText(MAIN2, "Вы вышли из аккаунта", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.navigationBarColor = Color.WHITE
        setColors(requireActivity(), "mainGreen")
    }
}