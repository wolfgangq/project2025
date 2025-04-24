package com.tradition.mobilevtkproject.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.User
import com.tradition.mobilevtkproject.data.repository.impl.FirebaseUserRepository
import com.tradition.mobilevtkproject.databinding.FragmentThirdBinding
import com.tradition.mobilevtkproject.utils.TextFormattingUtils
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
        binding.progressBar.progress = 75

        binding.continueButton.setOnClickListener{
            val email = binding.editTextEmail.text.toString().trim()
            val currentUser = arguments?.getSerializable("info") as User
            when{
                email == "" -> Toast.makeText(MAIN, "Введите адрес электронной почты", Toast.LENGTH_SHORT).show()
                !TextFormattingUtils.isEmailValid(email) -> Toast.makeText(MAIN, "Введите правильный адрес почты", Toast.LENGTH_SHORT).show()

                else -> {
                    lifecycleScope.launch {
                        if(FirebaseUserRepository().userWithThisEmailExists(email)){
                            Toast.makeText(MAIN, "Такой пользователь уже зарегестрирован", Toast.LENGTH_LONG).show()
                        }
                        else{
                            currentUser.email = email
                            bundle.putSerializable("info", currentUser)
                            (activity as? MainActivity)?.goFragment(null, FourthFragmentPass(), bundle)
                        }
                    }
                }
            }
        }
        binding.imageButton.setOnClickListener{
            (activity as? MainActivity)?.onBackPressed()
        }
    }
    }