package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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

        /*binding.imageButtonToOut.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val drawable = ContextCompat.getDrawable(MAIN2, R.drawable.arrow) as BitmapDrawable?

                if (drawable != null) {
                    val density = resources.displayMetrics.density
                    val params = binding.imageButtonToOut.width

                    val width = (params/density).toInt()
                    val height = (params/density).toInt()

                    val resizedDrawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(drawable.bitmap, width, height, true))

                    binding.buttonToOut.setCompoundDrawablesWithIntrinsicBounds(resizedDrawable, null, null, null)
                }
            }
        })*/
        //setColors(requireActivity())

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

        /*val popupMenu = PopupMenu(requireActivity(), binding.imageButtonAccount)
        popupMenu.inflate(R.menu.popupmenu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    (activity as? TransitionActivity)?.goFragment("Map", SettingsFragment(), null)
                }
                R.id.checkBalance -> {
                    lifecycleScope.launch {
                        val auth = FirebaseAuth.getInstance()
                        var id = auth.currentUser?.uid
                        var currentUser = getUserInfo(id.toString())
                        val builder = AlertDialog.Builder(MAIN2)
                        builder.setTitle("Баланс")
                            .setMessage("Ваш текущий баланс: ${currentUser!!["balance"]} зернышек")

                        builder.setPositiveButton("Ок") { dialog, which ->
                        }
                        val alertDialog = builder.create()
                        alertDialog.show()
                    }
                }
                R.id.logOut -> {
                    auth.signOut()
                    val intent = Intent(MAIN2, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    MAIN2.startActivity(intent)
                    Toast.makeText(MAIN2, "Вы вышли из аккаунта", Toast.LENGTH_LONG).show()
                }
            }
            false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }

        binding.imageButtonAccount.setOnClickListener {
            popupMenu.show()
        }*/
        binding.buttonCompetitions.setOnClickListener{
            val builder = AlertDialog.Builder(MAIN2)
            builder.setTitle("")
                .setMessage("Находится в разработке")

            builder.setPositiveButton("Ок") { dialog, which ->
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }


        /*var k = 0
        binding.textViewInfo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                k += 1
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.isNotEmpty()) {
                    if(k != 0){
                        binding.textViewInfo.visibility = View.VISIBLE
                        binding.button.visibility = View.VISIBLE
                        binding.textViewInfo.removeTextChangedListener(this)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })*/

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

    override fun onResume() {
        super.onResume()
        WindowUtils.setLightStatusBarIcons(requireActivity())
        WindowUtils.setDarkNavigationBarIcons(requireActivity())
        WindowUtils.setStatusBarColor(requireActivity(), R.color.mainGreen)
        WindowUtils.setNavigationBarColor(requireActivity(), R.color.white)
    }

}