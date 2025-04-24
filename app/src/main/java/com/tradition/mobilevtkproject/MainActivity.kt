package com.tradition.mobilevtkproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.databinding.ActivityMainBinding
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

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
    }
}