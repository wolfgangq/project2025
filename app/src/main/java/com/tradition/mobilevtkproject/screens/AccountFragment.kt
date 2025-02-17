package com.tradition.mobilevtkproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.User
import com.tradition.mobilevtkproject.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageButtonBack.setOnClickListener {
            MAIN.navController.popBackStack()
        }
        val currentUser = arguments?.getSerializable("info") as User
        binding.editTextName2.setText(currentUser.name)
        binding.editTextSurname2.setText(currentUser.surname)
        binding.editTextEmail2.setText(currentUser.email)
        binding.editTextAge2.setText(currentUser.age.toString())
        binding.textViewBalance.text = "Ваш баланс: ${currentUser.balance} зернышек"
    }
    }