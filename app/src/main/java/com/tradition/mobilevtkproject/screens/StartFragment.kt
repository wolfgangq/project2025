package com.tradition.mobilevtkproject.screens

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.MainActivity.Companion.isInternetAvailable
import com.tradition.mobilevtkproject.MainActivity.Companion.setDarkStatusBar
import com.tradition.mobilevtkproject.MainActivity.Companion.toDefaultColors
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentStartBinding
import kotlinx.coroutines.delay

@Suppress("DEPRECATION")
class StartFragment : Fragment() {

    lateinit var binding: FragmentStartBinding
    val bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonToAuth.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                /*
                val builder = AlertDialog.Builder(MAIN)
                builder.setTitle("Информация")
                    .setMessage("Выберите одно из следующих действий:")

                builder.setPositiveButton("Ок") { dialog, which ->
                }

                val alertDialog = builder.create()
                alertDialog.show()

                 */


                binding.buttonToAuth.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        binding.buttonToReg.setOnClickListener{
            if(isInternetAvailable(MAIN)){
                MAIN.navController.navigate(R.id.action_startFragment_to_firstFragment)
            }
            else{
                val builder = AlertDialog.Builder(MAIN)
                builder.setTitle("Сеть")
                    .setMessage("Интернет недоступен")

                builder.setPositiveButton("Ок") { dialog, which ->
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }

        binding.buttonToAuth.setOnClickListener{
            if(isInternetAvailable(MAIN)){
                MAIN.navController.navigate(R.id.action_startFragment_to_authFragment)
            }
            else{
                val builder = AlertDialog.Builder(MAIN)
                builder.setTitle("Сеть")
                    .setMessage("Интернет недоступен")

                builder.setPositiveButton("Ок") { dialog, which ->
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }

        /*binding.buttonAsGuest.setOnClickListener{
            bundle.putInt("UserId", -1)
            MAIN.navController.navigate(R.id.action_startFragment_to_mainFragment, bundle)
        }*/

        binding.imageButtonInfo.setOnClickListener{
            MAIN.navController.navigate(R.id.action_startFragment_to_infoFragment)
        }

        }
    fun turnButtons(bool: Boolean){
        binding.buttonToAuth.isClickable = bool
        binding.buttonToReg.isClickable = bool
        binding.imageButtonInfo.isClickable = bool
    }
    fun turnScreen(bool: Boolean){
        when(bool){
            true -> binding.textViewDisable.visibility = View.INVISIBLE
            false -> binding.textViewDisable.visibility = View.VISIBLE
        }
    }
    override fun onStart() {
        super.onStart()
        setDarkStatusBar(requireActivity())
        toDefaultColors(requireActivity())
        turnButtons(false)
        val auth = Firebase.auth
        var user = auth.currentUser
        if (user == null) {
            turnButtons(true)
            return
        }
        user.reload().addOnCompleteListener { task ->
            turnButtons(true)
            if (task.isSuccessful) {
                user = auth.currentUser
                if (user != null) {
                    MAIN.navController.navigate(R.id.action_startFragment_to_mainFragment)
                    /*val intent = Intent(MAIN, TransitionActivity::class.java)
                    startActivity(intent)*/
                    Toast.makeText(MAIN, "Вы вошли как ${user?.email}", Toast.LENGTH_LONG).show()
                }
            } else {
                if (isInternetAvailable(MAIN)) {
                    Toast.makeText(
                        MAIN,
                        "Аккаунт ${user?.email} был заморожен",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    val builder = AlertDialog.Builder(MAIN)
                    builder.setTitle("Сеть")
                        .setMessage("Интернет недоступен")

                    builder.setPositiveButton("Ок") { dialog, which ->
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }
    }