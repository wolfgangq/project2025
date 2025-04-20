package com.tradition.mobilevtkproject.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.setColors
import com.tradition.mobilevtkproject.MainActivity.Companion.setLightStatusBar
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = requireActivity()
        binding.imageButtonBack.setOnClickListener{
            if (act is MainActivity) {
                (activity as? MainActivity)?.onBackPressed()
            }
            else{
                (activity as? TransitionActivity)?.onBackPressed()
            }
        }
        binding.buttonTelegram.setOnClickListener{
            try {
                // Клиент Telegram
                val intent = Intent(Intent.ACTION_VIEW, "tg://resolve?domain=soopium".toUri())
                startActivity(intent)
            } catch (e: Exception) {
                // Браузер
                val fallbackIntent = Intent(Intent.ACTION_VIEW, "https://t.me/soopium".toUri())
                startActivity(fallbackIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val act = requireActivity()
        if (act is TransitionActivity){
            val colorResId = act.resources.getIdentifier("lightBlue", "color", act.packageName)
            val color = ContextCompat.getColor(act, colorResId)
            val colorResId2 = act.resources.getIdentifier("white", "color", act.packageName)
            val color2 = ContextCompat.getColor(act, colorResId2)
            act.window.statusBarColor = color
            act.window.navigationBarColor = color2
        }
        else{
            setColors(act, "lightBlue")
        }
        setLightStatusBar(act)
    }

}