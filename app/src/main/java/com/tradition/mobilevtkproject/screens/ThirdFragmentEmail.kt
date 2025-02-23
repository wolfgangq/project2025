package com.tradition.mobilevtkproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity.Companion.getUserInfo
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.databinding.FragmentThirdBinding
import com.tradition.mobilevtkproject.MainActivity.Companion.isEmailValid
import com.tradition.mobilevtkproject.MainActivity.Companion.userWithThisEmailExists
import com.tradition.mobilevtkproject.User
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ThirdFragmentEmail : Fragment() {

    lateinit var binding: FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    val bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.setProgress(75)

        binding.continueButton.setOnClickListener{
            val email = binding.editTextEmail.text.toString().trim()
            val currentUser = arguments?.getSerializable("info") as User
            when{
                email == "" -> Toast.makeText(MAIN, "Введите адрес электронной почты", Toast.LENGTH_SHORT).show()
                !isEmailValid(email) -> Toast.makeText(MAIN, "Введите правильный адрес почты", Toast.LENGTH_SHORT).show()

                else -> {
                    lifecycleScope.launch {
                        if(userWithThisEmailExists(email)){
                            Toast.makeText(MAIN, "Такой пользователь уже зарегестрирован", Toast.LENGTH_LONG).show()
                        }
                        else{
                            currentUser.email = email
                            bundle.putSerializable("info", currentUser)
                            MAIN.navController.navigate(R.id.action_thirdFragment_to_fourthFragment, bundle)
                        }
                    }
                }
            }
        }
        binding.imageButton.setOnClickListener{
            MAIN.navController.popBackStack()
        }
    }
    }