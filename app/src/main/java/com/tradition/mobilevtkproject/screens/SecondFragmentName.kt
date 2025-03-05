package com.tradition.mobilevtkproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.User
import com.tradition.mobilevtkproject.databinding.FragmentSecondBinding

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
        binding.progressBar.progress = 50

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
                (activity as? MainActivity)?.goFragment(null, ThirdFragmentEmail(), bundle)
            }


        }
        binding.imageButton.setOnClickListener{
            (activity as? MainActivity)?.onBackPressed()
        }
    }

}