package com.tradition.mobilevtkproject.screens

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.databinding.FragmentForgotPasswordBinding
import com.tradition.mobilevtkproject.MainActivity.Companion.isPassValid
import com.tradition.mobilevtkproject.MainActivity.Companion.isEmailValid
import com.tradition.mobilevtkproject.MainActivity.Companion.setColors
import com.tradition.mobilevtkproject.MainActivity.Companion.setLightStatusBar
import com.tradition.mobilevtkproject.MainActivity.Companion.toDefaultColors

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
        val PrevEmail = arguments?.getString("Email")
        binding.editTextEmail.setText(PrevEmail)

        binding.buttonSendEmail.setOnClickListener{
            val gotEmail = binding.editTextEmail.text.toString()
            if (isEmailValid(gotEmail)){
                Firebase.auth.sendPasswordResetEmail(gotEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Email sent.")
                            val builder = AlertDialog.Builder(MAIN)
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
            //MAIN.navController.popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        setLightStatusBar(requireActivity())
        toDefaultColors(requireActivity()) // MAIN
    }

    override fun onResume() {
        super.onResume()
        setColors(requireActivity(), "darkGreen")
    }
}

