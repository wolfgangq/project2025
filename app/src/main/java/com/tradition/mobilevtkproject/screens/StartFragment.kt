package com.tradition.mobilevtkproject.screens

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.isInternetAvailable
import com.tradition.mobilevtkproject.MainActivity.Companion.setDarkStatusBar
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth
import com.tradition.mobilevtkproject.MainActivity.Companion.toDefaultColors
import com.tradition.mobilevtkproject.databinding.FragmentStartBinding

@Suppress("DEPRECATION")
class StartFragment : Fragment() {

    lateinit var binding: FragmentStartBinding
    val bundle = Bundle()
    val db = Firebase.firestore
    val installedVersion: Float by lazy {
        val packageInfo: PackageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        packageInfo.versionName!!.toFloat()
    }

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

    fun checkForUpdates(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val lastCheckTime = sharedPreferences.getLong("lastCheckTime", 0L)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastCheckTime > 24*3600*1000) { // Раз в день
            sharedPreferences.edit().putLong("lastCheckTime", currentTime).apply()
            db.collection("CURRENT_VERSION").get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val newestAvailableVersion = documents.first().get("versionNumber").toString().toFloat()
                    if (installedVersion < newestAvailableVersion) {
                        showUpdateDialog(newestAvailableVersion)
                    }
                }
            }
        }
    }

    fun showUpdateDialog(newestAvailableVersion: Float) {
        val builder = AlertDialog.Builder(MAIN)
        builder.setTitle("Информация")
            .setMessage("Уважаемый пользователь!\nУстановленная версия приложения: $installedVersion\nНовейшая версия: $newestAvailableVersion\nПожалуйста, обновитесь до новейшей версии")

        builder.setPositiveButton("Обновить") { dialog, which ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/drive/folders/10YuxH1BKjsqZ6Zt0G18PXvQlZw7N7HlJ?usp=sharing"))
            startActivity(intent)
        }
        builder.setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.show()
    }
    override fun onStart() {
        super.onStart()
        setDarkStatusBar(requireActivity())
        toDefaultColors(requireActivity())
        checkForUpdates(requireContext())
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