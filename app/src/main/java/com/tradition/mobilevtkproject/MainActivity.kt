package com.tradition.mobilevtkproject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.databinding.ActivityMainBinding
import kotlinx.coroutines.tasks.await
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.firebase.auth.ktx.auth
import com.tradition.mobilevtkproject.screens.StartFragment

@Suppress("OVERRIDE_DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        MAIN = this

    }

    fun goFragment(stackName: String?, fragment: Fragment, bundle: Bundle?){
        supportFragmentManager.commit{
            fragment.arguments = bundle
            setReorderingAllowed(true)
            replace(R.id.fragmentContainerView1, fragment)
            addToBackStack(stackName)
        }
    }

    var f = 0
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView1)
        if (currentFragment !is StartFragment) {
            supportFragmentManager.popBackStack()
        }
        else {
            val toast = Toast.makeText(MAIN, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT)
            f += 1
            if (f == 2) {
                f = 0
                toast.cancel()
                finish()
                //super.onBackPressedDispatcher.onBackPressed()
            } else if (f == 1) {
                toast.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    toast.cancel()
                }, 1700)
                Handler(Looper.getMainLooper()).postDelayed({
                    f = 0
                }, 2000)
            }
        }
    }

    companion object {
        fun successAuth(email: String){
            val intent = Intent(MAIN, TransitionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            MAIN.startActivity(intent)
            Toast.makeText(MAIN, "Вы вошли как $email", Toast.LENGTH_LONG).show()
        }
        suspend fun getUserInfo(id: String): Map<String?, Any?>? {
            val db = Firebase.firestore

            return try {
                val snapshot = db.collection("users").whereEqualTo("authId", id).get().await()
                val userInfo = mutableMapOf<String?, Any?>()
                for (doc in snapshot.documents) {
                    userInfo.putAll(doc.data!!)
                }
                userInfo
            } catch (e: Exception) {
                Log.w("Firestore", "Error getting documents.", e)
                null
            }
        }
        suspend fun userWithThisEmailExists(email: String): Boolean {
            val db = Firebase.firestore
            return try {
                val querySnapshot = db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .await()
                querySnapshot.isEmpty.not()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
        fun isEmailValid(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        fun isPassValid(pass: String): Boolean {
            return (pass.length >= 8)
        }
        @SuppressLint("DiscouragedApi")
        @Suppress("DEPRECATION")
        fun setColors(act: FragmentActivity, colorName: String){
            act.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            act.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val colorResId = act.resources.getIdentifier(colorName, "color", act.packageName)

            if (colorResId != 0) {
                val color = ContextCompat.getColor(act, colorResId)
                act.window.statusBarColor = color
                act.window.navigationBarColor = color
            } else {
                Log.e("setColors", "Color resource not found: $colorName")
            }
        }
        fun toDefaultColors(act: FragmentActivity){
            act.window.statusBarColor = Color.TRANSPARENT
            act.window.navigationBarColor = Color.TRANSPARENT
        }
        fun setDarkStatusBar(act: FragmentActivity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = act.window?.insetsController
                controller?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else
                act.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        fun setLightStatusBar(act: FragmentActivity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = act.window?.insetsController
                controller?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else
                act.window?.decorView?.systemUiVisibility =
                    act.window?.decorView?.systemUiVisibility?.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())!!
        }
    }
}