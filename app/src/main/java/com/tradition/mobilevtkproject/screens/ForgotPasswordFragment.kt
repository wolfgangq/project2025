package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentForgotPasswordBinding
import com.tradition.mobilevtkproject.utils.TextFormattingUtils

class ForgotPasswordFragment : Fragment() {
    lateinit var binding: FragmentForgotPasswordBinding
    lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prevEmail = arguments?.getString("Email")
        binding.editTextEmail.setText(prevEmail)

        binding.buttonSendEmail.setOnClickListener{
            val gotEmail = binding.editTextEmail.text.toString()
            if (TextFormattingUtils.isEmailValid(gotEmail)){
                Firebase.auth.sendPasswordResetEmail(gotEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Email sent.")
                            val builder = AlertDialog.Builder(requireActivity())
                            builder.setTitle("Восстановление пароля")
                                .setMessage("Письмо было отправлено на почту")

                            builder.setPositiveButton("Ок") { dialog, which ->
                            }
                            val alertDialog = builder.create()
                            alertDialog.show()
                        }
                        else{

                        }
                    }
            }
            else if(gotEmail == ""){
                Toast.makeText(MAIN, "Введите почту", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(MAIN, "Введите реальную почту", Toast.LENGTH_SHORT).show()
            }
        }
        binding.imageButton.setOnClickListener{
            if (requireActivity() is MainActivity){
                (activity as? MainActivity)?.onBackPressed()
            }
            else if (requireActivity() is TransitionActivity){
                (activity as? TransitionActivity)?.onBackPressed()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        WindowUtils.setLightStatusBarIcons(requireActivity())
        WindowUtils.resetStatusBarToDefault(requireActivity())
        WindowUtils.resetNavigationBarToDefault(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        WindowUtils.setStatusBarColor(requireActivity(), R.color.darkGreen)
        if (requireActivity() is TransitionActivity){
            WindowUtils.setNavigationBarColor(requireActivity(), R.color.white)
        }
        else{
            WindowUtils.setNavigationBarColor(requireActivity(), R.color.darkGreen)
        }
    }
}

