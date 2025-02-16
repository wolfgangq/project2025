package com.example.physicsproject.screens

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.physicsproject.AppDatabase
import com.example.physicsproject.Item
import com.example.physicsproject.Level
import com.example.physicsproject.MAIN
import com.example.physicsproject.databinding.FragmentMainBinding
import kotlinx.coroutines.launch
import com.example.physicsproject.MainActivity.Companion.setColors
import com.example.physicsproject.R

class MainFragmentMap : Fragment() {

    lateinit var binding: FragmentMainBinding

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
                val drawable = ContextCompat.getDrawable(MAIN, R.drawable.arrow) as BitmapDrawable?

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

        var db = AppDatabase.getInstance(MAIN)
        var id = arguments?.getInt("UserId")
        sendDataToActivity(id)

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

        var k = 0
        binding.textViewInfo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                k += 1
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Этот метод вызывается при изменении текста
                if (s != null && s.isNotEmpty()) {
                    if(k == 2){
                        binding.textViewInfo.visibility = View.VISIBLE
                        binding.button.visibility = View.VISIBLE
                        binding.textViewInfo.removeTextChangedListener(this) // Удаляем слушатель после первого срабатывания
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Этот метод вызывается после изменения текста
            }
        })

        binding.imageButtonVtk.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Воткинск"}?.description.toString()
        }
        binding.imageButtonKukui.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Кукуи"}?.description.toString()
        }
        binding.imageButtonNoviy.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Новый"}?.description.toString()
        }
        binding.imageButtonBolguri.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Болгуры"}?.description.toString()
        }
        binding.imageButtonBolshayaKivara.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Большая Кивара"}?.description.toString()
        }
        binding.imageButtonGavrilovka.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Гавриловка"}?.description.toString()
        }
        binding.imageButtonSvetloe.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Светлое"}?.description.toString()
        }
        binding.imageButtonIulskoe.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Июльское"}?.description.toString()
        }
        binding.imageButtonKamskoe.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Камское"}?.description.toString()
        }
        binding.imageButtonKvarsa.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Кварса"}?.description.toString()
        }
        binding.imageButtonPerevoznoe.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Перевозное"}?.description.toString()
        }
        binding.imageButtonPervomaiskoe.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Первомайское"}?.description.toString()
        }
        binding.imageButtonVerhnyayaTalitsa.setOnClickListener{
            binding.textViewInfo.text = items.find{it.name == "Верхняя Талица"}?.description.toString()
        }

        binding.imageButtonToOut.setOnClickListener{
            id = -1
            MAIN.navController.navigate(R.id.action_mainFragment_to_startFragment)
        }
        binding.buttonToOut.setOnClickListener{
            id = -1
            MAIN.navController.navigate(R.id.action_mainFragment_to_startFragment)
        }

        if(id != -1){
            binding.buttonToOut.text = "Выйти"
            binding.imageButtonToOut.setImageResource(R.drawable.logout)
            lifecycleScope.launch {
                val curUser = db.getDao().getUserById(id)
                binding.textViewInfo.text = "${curUser?.id} \n ${curUser?.email} \n ${curUser?.accessLevel} \n ${curUser?.pass} \n ${curUser?.name} \n ${curUser?.surname} \n ${curUser?.age}"
                if(curUser?.accessLevel == Level.Admin || curUser?.accessLevel == Level.TopAdmin){
                    binding.buttonForAdmins.visibility = View.VISIBLE
                    binding.buttonForAdmins.setOnClickListener{
                        binding.textViewInfo.text = "User is admin"
                    }
                }
            }
        }
        else{
            binding.textViewInfo.text = "Тест git123"
        }
    }

    override fun onResume() {
        super.onResume()
        setColors(requireActivity(), "mainGreen")
    }

    interface OnDataPass {
        fun onDataPass(data: Int?)
    }

    private lateinit var dataPasser: OnDataPass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    private fun sendDataToActivity(id: Int?) {
        dataPasser.onDataPass(id)
    }

}