package com.tradition.mobilevtkproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import com.tradition.mobilevtkproject.databinding.ActivityTransitionBinding
import kotlin.properties.Delegates
import com.tradition.mobilevtkproject.R

//main start second
// |     |     |
//shop main settings

@Suppress("OVERRIDE_DEPRECATION")
class TransitionActivity : AppCompatActivity() {

    lateinit var binding: ActivityTransitionBinding
    lateinit var navController: NavController
    var previousDest by Delegates.notNull<Int>()
    var List0Dest = mutableListOf<Int>(
        R.id.shopFragment
    )
    var List1Dest = mutableListOf<Int>(
        R.id.mainFragment
    )
    var List2Dest = mutableListOf<Int>(
        R.id.accountFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransitionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transition)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        MAIN2 = this

        previousDest = navController.currentDestination?.id!!

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Toast.makeText(MAIN2, List1Dest.size.toString(), Toast.LENGTH_SHORT).show()
            val dest = navController.currentDestination?.id
            if (dest == R.id.shopFragment || dest == R.id.mainFragment || dest == R.id.accountFragment) {
                val position = binding.tabLayout3.selectedTabPosition
                when (position) {
                    0 -> {List0Dest = mutableListOf<Int>(
                        R.id.shopFragment
                    )
                    }
                    1 -> {List1Dest = mutableListOf<Int>(
                        R.id.mainFragment
                    )
                    }
                    2 -> {List2Dest = mutableListOf<Int>(
                        R.id.accountFragment
                    )
                    }
                }
            }
            when (binding.tabLayout3.selectedTabPosition) {
                0 -> {
                    if (previousDest != R.id.mainFragment && previousDest != R.id.accountFragment) {
                        List0Dest.add(dest!!)
                    }
                }
                1 -> {
                    if (previousDest != R.id.shopFragment && previousDest != R.id.accountFragment) {
                        List1Dest.add(dest!!)
                    }
                }
                2 -> {
                    if (previousDest != R.id.shopFragment && previousDest != R.id.mainFragment) {
                        List2Dest.add(dest!!)
                    }
                }
            }
            previousDest = dest!!
        }

        binding.tabLayout3.getTabAt(1)?.select()

        binding.tabLayout3.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Toast.makeText(MAIN2, List1Dest.size.toString(), Toast.LENGTH_SHORT).show()
                val position = tab.position
                val dest = navController.currentDestination?.id
                when (position) {
                    0 -> navController.navigate(List0Dest.last())
                    1 -> navController.navigate(List1Dest.last())
                    2 -> navController.navigate(List2Dest.last())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                //change icon's alpha
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                val position = tab.position
                when (position) {
                    0 -> {List0Dest = mutableListOf<Int>(
                        R.id.shopFragment
                    )
                        navController.navigate(List0Dest.last())
                    }
                    1 -> {List1Dest = mutableListOf<Int>(
                        R.id.mainFragment
                    )
                        navController.navigate(List1Dest.last())
                    }
                    2 -> {List2Dest = mutableListOf<Int>(
                        R.id.accountFragment
                    )
                        navController.navigate(List2Dest.last())
                    }
                }
            }
        })

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val dest = navController.currentDestination?.id
        if (dest != R.id.shopFragment && dest != R.id.mainFragment && dest != R.id.accountFragment) {
            val position = binding.tabLayout3.selectedTabPosition
            when (position) {
                0 -> {
                    navController.navigate(List0Dest.last())
                    List0Dest.removeAt(List0Dest.lastIndex)
                }
                1 -> {
                    navController.navigate(List1Dest[List1Dest.size-2])
                    List1Dest.removeAt(List1Dest.lastIndex)
                }
                2 -> {
                    navController.navigate(List2Dest.last())
                    List2Dest.removeAt(List2Dest.lastIndex)
                }
            }
        } else {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

}