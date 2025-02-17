package com.example.physicsproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.physicsproject.databinding.ActivityMainBinding
import com.example.physicsproject.screens.MainFragmentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), MainFragmentMap.OnDataPass {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        MAIN = this

        var db = AppDatabase.getInstance(MAIN)

        var startUser = User(email = "admin@gmail.com", accessLevel = Level.Admin, pass = "12345678", name = "Сергей", surname = "Русанов", age = 16, balance = 1000)
        var startUser2 = User(email = "test@gmail.com", pass = "12345678", name = "Test", surname = "Testov", age = 18)
        lifecycleScope.launch {
            val user = db.getDao().getUserByEmail("admin@gmail.com")
            if(user == null){
                withContext(Dispatchers.IO) {
                    db.getDao().insertItem(startUser)
                }
            }
            val user2 = db.getDao().getUserByEmail("test@gmail.com")
            if(user2 == null){
                withContext(Dispatchers.IO) {
                    db.getDao().insertItem(startUser2)
                }
            }
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val dest = navController.currentDestination?.id

        if (dest == R.id.startFragment || (dest == R.id.mainFragment && id != -1)) {
            super.onBackPressedDispatcher.onBackPressed()
        } else {
            navController.popBackStack()
        }
    }

    companion object {
        fun isEmailValid(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        fun isPassValid(pass: String): Boolean {
            return (pass.length >= 8)
        }
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
                    0, // Убираем светлые элементы
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else
                act.window?.decorView?.systemUiVisibility =
                    act.window?.decorView?.systemUiVisibility?.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())!!
        }
    }


    override fun onDataPass(data: Int?) {
        id = data!!
    }


}