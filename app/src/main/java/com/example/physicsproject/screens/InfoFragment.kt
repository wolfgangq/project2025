package com.example.physicsproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.physicsproject.MAIN
import com.example.physicsproject.MainActivity.Companion.setColors
import com.example.physicsproject.MainActivity.Companion.setLightStatusBar
import com.example.physicsproject.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageButtonBack.setOnClickListener{
            MAIN.navController.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        val act = requireActivity()
        setColors(act, "mainGreen")
        setLightStatusBar(act)
    }

}