package com.example.physicsproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.physicsproject.MAIN
import com.example.physicsproject.R
import com.example.physicsproject.User
import com.example.physicsproject.databinding.FragmentSecondBinding

@Suppress("DEPRECATION")
class SecondFragmentName : Fragment() {

    lateinit var binding: FragmentSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    val bundle = Bundle()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.setProgress(50)

        binding.continueButton.setOnClickListener{
            val name = binding.editTextName.text.toString().trim()
            val surname = binding.editTextSurname.text.toString().trim()
            val currentUser = arguments?.getSerializable("info") as User
            if(name == "" || surname == "" ){
                Toast.makeText(MAIN, "Введите данные", Toast.LENGTH_SHORT).show()

            }
            else{
                currentUser.name = name
                currentUser.surname = surname
                bundle.putSerializable("info", currentUser)
                MAIN.navController.navigate(R.id.action_secondFragment_to_thirdFragment, bundle)
            }


        }
        binding.imageButton.setOnClickListener{
            MAIN.navController.popBackStack()
        }
    }

}