package com.tradition.mobilevtkproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.tradition.mobilevtkproject.databinding.FragmentSelectedGlobalCompetitionBinding

class SelectedGlobalCompetitionFragment : Fragment() {

    lateinit var binding: FragmentSelectedGlobalCompetitionBinding
    val auth = FirebaseAuth.getInstance()
    var user = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectedGlobalCompetitionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        }
    override fun onStop() {
        super.onStop()

    }
}

