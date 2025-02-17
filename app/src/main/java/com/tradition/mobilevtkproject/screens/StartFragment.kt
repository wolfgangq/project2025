package com.tradition.mobilevtkproject.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity.Companion.setDarkStatusBar
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
            MAIN.navController.navigate(R.id.action_startFragment_to_firstFragment)
        }

        binding.buttonToAuth.setOnClickListener{
            MAIN.navController.navigate(R.id.action_startFragment_to_authFragment)
        }

        binding.buttonAsGuest.setOnClickListener{
            bundle.putInt("UserId", -1)
            MAIN.navController.navigate(R.id.action_startFragment_to_mainFragment, bundle)
        }

        binding.imageButtonInfo.setOnClickListener{
            MAIN.navController.navigate(R.id.action_startFragment_to_infoFragment)
        }

        }
    override fun onStart() {
        super.onStart()
        setDarkStatusBar(requireActivity())
        toDefaultColors(requireActivity())
    }

    override fun onStop() {
        super.onStop()
    }
    }