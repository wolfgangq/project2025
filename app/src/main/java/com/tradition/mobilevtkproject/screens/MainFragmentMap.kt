package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentMainBinding

class MainFragmentMap : Fragment() {

    lateinit var binding: FragmentMainBinding
    val bundle = Bundle()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var regionName = ""

        val regions = mapOf(
            "Воткинск" to "Город Воткинск",
            "Большая Кивара" to "Село Большая Кивара",
            "Первомайское" to "Село Первомайское",
            "Верхняя Талица" to "Село Верхняя Талица",
            "Светлое" to "Село Светлое",
            "Кукуи" to "Село Кукуи",
            "Июльское" to "Село Июльское",
            "Болгуры" to "Деревня Болгуры",
            "Кварса" to "Село Кварса",
            "Гавриловка" to "Деревня Гавриловка",
            "Перевозное" to "Село Перевозное",
            "Новый" to "Поселок Новый",
            "Камское" to "Село Камское"
        )


        binding.imageButtonInfo2.setOnClickListener{
            (activity as? TransitionActivity)?.goFragment("Map", InfoFragment(), null)
        }

        binding.buttonCompetitions.setOnClickListener{
            val builder = AlertDialog.Builder(MAIN2)
            builder.setTitle("")
                .setMessage("Находится в разработке")

            builder.setPositiveButton("Ок") { dialog, which ->
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }


        binding.imageButtonVtk.setOnClickListener{
            regionName = "Воткинск"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonKukui.setOnClickListener{
            regionName = "Кукуи"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonNoviy.setOnClickListener{
            regionName = "Новый"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonBolguri.setOnClickListener{
            regionName = "Болгуры"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.VISIBLE
        }
        binding.imageButtonBolshayaKivara.setOnClickListener{
            regionName = "Большая Кивара"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonGavrilovka.setOnClickListener{
            regionName = "Гавриловка"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonSvetloe.setOnClickListener{
            regionName = "Светлое"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonIulskoe.setOnClickListener{
            regionName = "Июльское"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonKamskoe.setOnClickListener{
            regionName = "Камское"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonKvarsa.setOnClickListener{
            regionName = "Кварса"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonPerevoznoe.setOnClickListener{
            regionName = "Перевозное"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonPervomaiskoe.setOnClickListener{
            regionName = "Первомайское"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonVerhnyayaTalitsa.setOnClickListener{
            regionName = "Верхняя Талица"
            binding.textViewInfo.text = regions[regionName]
            binding.button.visibility = View.INVISIBLE
        }

        binding.button.setOnClickListener{
            bundle.putString("RegionName", regionName)
            (activity as? TransitionActivity)?.goFragment("Map", RegionFragment(), bundle)
        }

    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        WindowUtils.setLightStatusBarIcons(requireActivity())
        WindowUtils.setDarkNavigationBarIcons(requireActivity())
        WindowUtils.setStatusBarColor(requireActivity(), R.color.mainGreen)
        WindowUtils.setNavigationBarColor(requireActivity(), R.color.white)
    }

}