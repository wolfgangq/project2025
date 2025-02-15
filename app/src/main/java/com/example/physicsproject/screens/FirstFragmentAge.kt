package com.example.physicsproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.physicsproject.MAIN
import com.example.physicsproject.MainActivity.Companion.setColors
import com.example.physicsproject.MainActivity.Companion.setLightStatusBar
import com.example.physicsproject.R
import com.example.physicsproject.User
import com.example.physicsproject.databinding.FragmentFirstBinding

class FirstFragmentAge : Fragment() {

    lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    val bundle = Bundle()
    val currentUser = User()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.setProgress(25)
        binding.continueButton.setOnClickListener{
            val age = binding.editTextAge.text.toString().trim()
            if(age == ""){
                Toast.makeText(MAIN, "Введите возраст", Toast.LENGTH_SHORT).show()

            }
            else if(age.toDouble() < 4 || age.toDouble() > 130){
                Toast.makeText(MAIN, "Введите реальный возраст", Toast.LENGTH_LONG).show()
            }
            else{
                currentUser.age = age.toInt()
                bundle.putSerializable("info", currentUser)
                MAIN.navController.navigate(R.id.action_firstFragment_to_secondFragment, bundle)
            }


        }
        binding.imageButton.setOnClickListener{
            MAIN.navController.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        setLightStatusBar(requireActivity())
        setColors(requireActivity(), "darkGreen")
    }



}