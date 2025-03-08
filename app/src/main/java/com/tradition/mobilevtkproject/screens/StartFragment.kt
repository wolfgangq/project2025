package com.tradition.mobilevtkproject.screens

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.commit
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.isInternetAvailable
import com.tradition.mobilevtkproject.MainActivity.Companion.setDarkStatusBar
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth
import com.tradition.mobilevtkproject.MainActivity.Companion.toDefaultColors
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.databinding.FragmentStartBinding

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

        /*binding.buttonToAuth.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
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
        })*/

        binding.buttonToReg.setOnClickListener{
            if(isInternetAvailable(MAIN)){
                (activity as? MainActivity)?.goFragment(null, FirstFragmentAge(), null)
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
                (activity as? MainActivity)?.goFragment(null, AuthFragment(), null)
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
            (activity as? MainActivity)?.goFragment(null, InfoFragment(), null)
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
                    successAuth(user!!.email!!)
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
}