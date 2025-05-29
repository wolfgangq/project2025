package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth
import com.tradition.mobilevtkproject.databinding.FragmentStartBinding
import com.tradition.mobilevtkproject.utils.AppUtils
import com.tradition.mobilevtkproject.utils.NetworkUtils
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class StartFragment : Fragment() {

    lateinit var binding: FragmentStartBinding
    val bundle = Bundle()
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonToReg.setOnClickListener{
            if(NetworkUtils.isInternetAvailable(MAIN)){
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
            if(NetworkUtils.isInternetAvailable(MAIN)){
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


    override fun onStart() {
        super.onStart()
        WindowUtils.setDarkStatusBarIcons(requireActivity())
        WindowUtils.resetStatusBarToDefault(requireActivity())
        WindowUtils.resetNavigationBarToDefault(requireActivity())
        turnButtons(false)
        if (NetworkUtils.isInternetAvailable(requireContext()) == true){
            lifecycleScope.launch {
                val newestVersion = AppUtils.getNewestVersion()
                if (newestVersion != null) {
                    AppUtils.checkForUpdates(requireContext(), {AppUtils.showUpdateDialog(requireContext(), newestVersion)})
                }
            }
        }
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
                if (NetworkUtils.isInternetAvailable(MAIN)) {
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

    override fun onResume() {
        super.onResume()
        WindowUtils.setDarkNavigationBarIcons(requireActivity())
    }
}