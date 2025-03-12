package com.tradition.mobilevtkproject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.tabs.TabLayout
import com.tradition.mobilevtkproject.databinding.ActivityTransitionBinding
import com.tradition.mobilevtkproject.screens.AccountFragment
import com.tradition.mobilevtkproject.screens.MainFragmentMap
import com.tradition.mobilevtkproject.screens.ShopFragment


@Suppress("OVERRIDE_DEPRECATION")
class TransitionActivity : AppCompatActivity() {

    lateinit var binding: ActivityTransitionBinding
    var prevStack = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransitionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        MAIN2 = this

        val builder = AlertDialog.Builder(MAIN2)
        builder.setTitle("Информация")
            .setMessage("Уважаемый пользователь! Данное приложение находится на стадии активной разработки.\nНа " +
                    "данный момент доступны лишь территориальные образование Болгуры.\nОтнеситесь к этому с пониманием!")

        builder.setPositiveButton("Ок(3)") { dialog, which ->
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

        val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        button.isEnabled = false
        var countdown = 3

        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                if (countdown > 0) {
                    button.text = "ОК($countdown)"
                    countdown--
                    handler.postDelayed(this, 1000)
                } else {
                    button.text = "ОК"
                    button.isEnabled = true
                    alertDialog.setCancelable(true)
                }
            }
        }

// Запускаем обратный отсчет
        handler.post(runnable)

        binding.tabLayout.getTabAt(1)?.select()

        binding.tabLayout.getTabAt(0)?.icon!!.alpha = 70
        binding.tabLayout.getTabAt(1)?.icon!!.alpha = 250
        binding.tabLayout.getTabAt(2)?.icon!!.alpha = 70

        val tab1 = "Shop"
        val tab2 = "Map"
        val tab3 = "Account"

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                tab.icon!!.alpha = 250
                when (position) {
                    0 -> {
                        goLastFragment(tab1, ShopFragment())
                        }
                    1 -> {
                        goLastFragment(tab2, MainFragmentMap())
                        tab.setIcon(R.drawable.map_icon)
                    }
                    2 -> {
                        goLastFragment(tab3, AccountFragment())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val position = tab.position
                tab.icon!!.alpha = 70
                when (position) {
                    0 -> {
                        prevStack = tab1
                        supportFragmentManager.commit{
                            supportFragmentManager.saveBackStack(tab1)
                        }
                    }
                    1 -> {
                        prevStack = tab2
                        tab.setIcon(R.drawable.unselected_map)
                        tab.icon!!.alpha = 70
                        supportFragmentManager.commit{
                            supportFragmentManager.saveBackStack(tab2)
                        }
                    }
                    2 -> {
                        prevStack = tab3
                        supportFragmentManager.commit{
                            supportFragmentManager.saveBackStack(tab3)
                        }
                    }
                }
                //change icon's alpha
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
                val position = tab.position
                when (position) {
                    0 -> {
                        resetStack("Shop", ShopFragment())
                    }
                    1 -> {
                        resetStack("Map", MainFragmentMap())
                    }
                    2 -> {
                        resetStack("Account", AccountFragment())
                    }
                }
            }


            fun goLastFragment(stackName: String, fragment: Fragment){
                supportFragmentManager.commit{
                    setReorderingAllowed(true)
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                        fragment).commit()
                    supportFragmentManager.restoreBackStack(stackName)
                }
            }
            fun resetStack(stackName: String, fragment: Fragment){
                supportFragmentManager.commit{
                    supportFragmentManager.popBackStack(stackName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    setReorderingAllowed(true)
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                        fragment).commit()
                }
            }
        })
    }

    var f = 0
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        if (currentFragment !is ShopFragment && currentFragment !is MainFragmentMap && currentFragment !is AccountFragment) {
            supportFragmentManager.popBackStack()
        }
        else {
            val toast = Toast.makeText(MAIN, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT)
            f += 1
            if (f == 2) {
                f = 0
                toast.cancel()
                super.onBackPressedDispatcher.onBackPressed()
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

    fun goFragment(stackName: String, fragment: Fragment, bundle: Bundle?, bool: Boolean? = true){
        supportFragmentManager.commit{
            if (bool == true){
                setCustomAnimations(
                    0,//R.anim.slide_in, // enter
                    0,//R.anim.fade_out, // exit
                    R.anim.fade_in, // popEnter
                    R.anim.slide_out // popExit
                )
            }
            fragment.arguments = bundle
            setReorderingAllowed(true)
            replace(R.id.fragmentContainerView, fragment)
            addToBackStack(stackName)
        }
    }

    companion object{
        @SuppressLint("DiscouragedApi")
        @Suppress("DEPRECATION")
        fun setColors(act: FragmentActivity, colorName: String){
            act.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            act.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val colorResId = act.resources.getIdentifier(colorName, "color", act.packageName)

            if (colorResId != 0) {
                val color = ContextCompat.getColor(act, colorResId)
                act.window.statusBarColor = color
            } else {
                Log.e("setColors", "Color resource not found: $colorName")
            }
        }
    }

}