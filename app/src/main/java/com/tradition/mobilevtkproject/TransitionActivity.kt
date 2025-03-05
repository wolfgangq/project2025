package com.tradition.mobilevtkproject

import android.annotation.SuppressLint
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

//main start second
// |     |     |
//shop main settings

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

        binding.tabLayout.getTabAt(1)?.select()

        val tab1 = "Shop"
        val tab2 = "Map"
        val tab3 = "Account"

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                when (position) {
                    0 -> {
                        goLastFragment(tab1, ShopFragment())
                        }
                    1 -> {
                        goLastFragment(tab2, MainFragmentMap())
                    }
                    2 -> {
                        goLastFragment(tab3, AccountFragment())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val position = tab.position
                when (position) {
                    0 -> {
                        prevStack = tab1
                        supportFragmentManager.commit{
                            supportFragmentManager.saveBackStack(tab1)
                        }
                    }
                    1 -> {
                        prevStack = tab2
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

    fun goFragment(stackName: String, fragment: Fragment, bundle: Bundle?){
        supportFragmentManager.commit{
            fragment.arguments = bundle
            setReorderingAllowed(true)
            replace(R.id.fragmentContainerView, fragment)
            addToBackStack(stackName)
        }
    }

    companion object{
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