package com.tradition.mobilevtkproject.screens

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.getUserInfo
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.databinding.FragmentAuthBinding
import com.tradition.mobilevtkproject.MainActivity.Companion.isPassValid
import com.tradition.mobilevtkproject.MainActivity.Companion.isEmailValid
import com.tradition.mobilevtkproject.MainActivity.Companion.setColors
import com.tradition.mobilevtkproject.MainActivity.Companion.setLightStatusBar
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth
import com.tradition.mobilevtkproject.MainActivity.Companion.toDefaultColors
import com.tradition.mobilevtkproject.TransitionActivity

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
            if(isEmailValid(enteremail) && isPassValid(enterpass)) {
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
            if (isEmailValid(email)){
                bundle.putString("Email", email)
            }
            (activity as? MainActivity)?.goFragment(null, ForgotPasswordFragment(), bundle)
        }
            /*if(isEmailValid(enteremail) && isPassValid(enterpass)){
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
        }*/

        binding.imageButton.setOnClickListener{
            (activity as? MainActivity)?.onBackPressed()
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

