package com.example.physicsproject.screens

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.physicsproject.AppDatabase
import com.example.physicsproject.MAIN
import com.example.physicsproject.R
import com.example.physicsproject.databinding.FragmentAuthBinding
import com.example.physicsproject.MainActivity.Companion.isPassValid
import com.example.physicsproject.MainActivity.Companion.isEmailValid
import com.example.physicsproject.MainActivity.Companion.setColors
import com.example.physicsproject.MainActivity.Companion.setLightStatusBar
import com.example.physicsproject.MainActivity.Companion.toDefaultColors
import kotlinx.coroutines.launch

class AuthFragment : Fragment() {
    lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    val bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignIn.setOnClickListener{
            val enteremail = binding.editTextTextEmailAddress.text.toString()
            val enterpass = binding.editTextTextPassword.text.toString()
            if(isEmailValid(enteremail) && isPassValid(enterpass)){
                val db = AppDatabase.getInstance(MAIN)
                lifecycleScope.launch {
                    val user = db.getDao().getUserByEmail(enteremail)
                    if(user != null && user.pass == enterpass.trim()){
                        bundle.putInt("UserId", user.id?.toInt() ?: -1)
                        MAIN.navController.navigate(R.id.action_authFragment_to_mainFragment, bundle)
                    }
                    else{
                        Toast.makeText(MAIN, "Неправильные данные", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else{
                Toast.makeText(MAIN, "Неправильные данные", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageButton.setOnClickListener{
            MAIN.navController.popBackStack()
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

