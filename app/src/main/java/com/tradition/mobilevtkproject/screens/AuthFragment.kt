package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.databinding.FragmentAuthBinding
import com.tradition.mobilevtkproject.utils.TextFormattingUtils

class AuthFragment : Fragment() {
    lateinit var binding: FragmentAuthBinding
    lateinit var auth: FirebaseAuth


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

        binding.buttonSignIn.setOnClickListener {
            auth = Firebase.auth
            val enteremail = binding.editTextEmailAddress.text.toString()
            val enterpass = binding.editTextTextPassword.text.toString()
            if(TextFormattingUtils.isEmailValid(enteremail) && TextFormattingUtils.isPassValid(enterpass)) {
                auth.signInWithEmailAndPassword(enteremail, enterpass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            successAuth(enteremail)
                        }
                    }.addOnFailureListener { exception ->
                    Toast.makeText(MAIN, "Не удалось войти", Toast.LENGTH_LONG)
                        .show()
                }
            }
            else{
                Toast.makeText(MAIN, "Неправильный формат данных", Toast.LENGTH_SHORT).show()
            }
        }
        binding.textViewForgotPass.setOnClickListener{
            var email = binding.editTextEmailAddress.text.toString()
            if (TextFormattingUtils.isEmailValid(email)){
                bundle.putString("Email", email)
            }
            (activity as? MainActivity)?.goFragment(null, ForgotPasswordFragment(), bundle)
        }

        binding.imageButton.setOnClickListener{
            (activity as? MainActivity)?.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        WindowUtils.setLightStatusBarIcons(requireActivity())
        WindowUtils.resetStatusBarToDefault(requireActivity())
        WindowUtils.resetNavigationBarToDefault(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        WindowUtils.setLightNavigationBarIcons(requireActivity())
        WindowUtils.setStatusBarColor(requireActivity(), R.color.darkGreen)
        WindowUtils.setNavigationBarColor(requireActivity(), R.color.darkGreen)
    }
}

