package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.tradition.mobilevtkproject.Item
import com.tradition.mobilevtkproject.MAIN2
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.getUserInfo
import com.tradition.mobilevtkproject.databinding.FragmentMainBinding
import kotlinx.coroutines.launch
import com.tradition.mobilevtkproject.TransitionActivity.Companion.setColors
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity

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

        val auth = FirebaseAuth.getInstance()
        var regionId = ""

        val items = arrayListOf<Item>()
        items.add(Item("Воткинск", "Город Воткинск"))
        items.add(Item("Большая Кивара", "Село Большая Кивара"))
        items.add(Item("Первомайское", "Село Первомайское"))
        items.add(Item("Верхняя Талица", "Село Верхняя Талица"))
        items.add(Item("Светлое", "Село Светлое"))
        items.add(Item("Кукуи", "Село Кукуи"))
        items.add(Item("Июльское", "Село Июльское"))
        items.add(Item("Болгуры", "Село Болгуры"))
        items.add(Item("Кварса", "Село Кварса"))
        items.add(Item("Гавриловка", "Деревня Гавриловка"))
        items.add(Item("Перевозное", "Село Перевозное"))
        items.add(Item("Новый", "Поселок Новый"))
        items.add(Item("Камское", "Село Камское"))



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
                .setMessage("Еще не реализовано")

            builder.setPositiveButton("Ок") { dialog, which ->
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }


        var k = 0
        /*binding.textViewInfo.addTextChangedListener(object : TextWatcher {
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
            regionId = "Воткинск"
            binding.textViewInfo.text = items.find{it.name == "Воткинск"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonKukui.setOnClickListener{
            regionId = "Кукуи"
            binding.textViewInfo.text = items.find{it.name == "Кукуи"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonNoviy.setOnClickListener{
            regionId = "Новый"
            binding.textViewInfo.text = items.find{it.name == "Новый"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonBolguri.setOnClickListener{
            regionId = "Болгуры"
            binding.textViewInfo.text = items.find{it.name == "Болгуры"}?.description.toString()
            binding.button.visibility = View.VISIBLE
        }
        binding.imageButtonBolshayaKivara.setOnClickListener{
            regionId = "Большая Кивара"
            binding.textViewInfo.text = items.find{it.name == "Большая Кивара"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonGavrilovka.setOnClickListener{
            regionId = "Гавриловка"
            binding.textViewInfo.text = items.find{it.name == "Гавриловка"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonSvetloe.setOnClickListener{
            regionId = "Светлое"
            binding.textViewInfo.text = items.find{it.name == "Светлое"}?.description.toString()
            binding.button.visibility = View.VISIBLE
        }
        binding.imageButtonIulskoe.setOnClickListener{
            regionId = "Июльское"
            binding.textViewInfo.text = items.find{it.name == "Июльское"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonKamskoe.setOnClickListener{
            regionId = "Камское"
            binding.textViewInfo.text = items.find{it.name == "Камское"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonKvarsa.setOnClickListener{
            regionId = "Кварса"
            binding.textViewInfo.text = items.find{it.name == "Кварса"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonPerevoznoe.setOnClickListener{
            regionId = "Перевозное"
            binding.textViewInfo.text = items.find{it.name == "Перевозное"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonPervomaiskoe.setOnClickListener{
            regionId = "Первомайское"
            binding.textViewInfo.text = items.find{it.name == "Первомайское"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }
        binding.imageButtonVerhnyayaTalitsa.setOnClickListener{
            regionId = "Верхняя Талица"
            binding.textViewInfo.text = items.find{it.name == "Верхняя Талица"}?.description.toString()
            binding.button.visibility = View.INVISIBLE
        }

        binding.button.setOnClickListener{
            bundle.putString("RegionName", regionId)
            (activity as? TransitionActivity)?.goFragment("Map", RegionFragment(), bundle)
        }

    }

    override fun onResume() {
        super.onResume()
        setColors(requireActivity(), "mainGreen")
    }

}