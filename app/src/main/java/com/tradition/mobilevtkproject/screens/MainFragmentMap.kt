package com.tradition.mobilevtkproject.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import com.tradition.mobilevtkproject.AppDatabase
import com.tradition.mobilevtkproject.Item
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.databinding.FragmentMainBinding
import kotlinx.coroutines.launch
import com.tradition.mobilevtkproject.MainActivity.Companion.setColors
import com.tradition.mobilevtkproject.R

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
        var regionId = ""
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


        /*
        val popupView = layoutInflater.inflate(R.layout.popup_menu, null)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        popupWindow.isFocusable = true

        popupView.findViewById<TextView>(R.id.action_one).setOnClickListener {
            Toast.makeText(MAIN, "Action One clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.action_two).setOnClickListener {
            Toast.makeText(MAIN, "Action Two clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
        binding.imageButtonAccount.setOnClickListener{
            popupWindow.showAsDropDown(binding.imageButtonAccount)
        }
        */

        val popupMenu2 = PopupMenu(MAIN, binding.imageButtonAccount)
        popupMenu2.inflate(R.menu.popupmenu)
        popupMenu2.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item1 -> {
                    lifecycleScope.launch {
                        val curUser = db.getDao().getUserById(id)
                        bundle.putSerializable("info", curUser)
                        MAIN.navController.navigate(R.id.action_mainFragment_to_accountFragment, bundle)
                    }
                }
                R.id.item2 -> {
                    lifecycleScope.launch {
                        val curUser = db.getDao().getUserById(id)

                        val builder = AlertDialog.Builder(MAIN)
                        builder.setTitle("Баланс")
                            .setMessage("Ваш текущий баланс: ${curUser?.balance} зернышек")

                        builder.setPositiveButton("Ок") { dialog, which ->
                        }
                        val alertDialog = builder.create()
                        alertDialog.show()
                    }
                }
                R.id.item3 -> {
                    id = -1
                    MAIN.navController.navigate(R.id.action_mainFragment_to_startFragment)
                }
            }
            false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu2.setForceShowIcon(true)
        }

        binding.imageButtonAccount.setOnClickListener {
            popupMenu2.show()
        }


        var k = 0
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
        })

        binding.imageButtonVtk.setOnClickListener{
            regionId = "Воткинск"
            binding.textViewInfo.text = items.find{it.name == "Воткинск"}?.description.toString()
        }
        binding.imageButtonKukui.setOnClickListener{
            regionId = "Кукуи"
            binding.textViewInfo.text = items.find{it.name == "Кукуи"}?.description.toString()
        }
        binding.imageButtonNoviy.setOnClickListener{
            regionId = "Новый"
            binding.textViewInfo.text = items.find{it.name == "Новый"}?.description.toString()
        }
        binding.imageButtonBolguri.setOnClickListener{
            regionId = "Болгуры"
            binding.textViewInfo.text = items.find{it.name == "Болгуры"}?.description.toString()
        }
        binding.imageButtonBolshayaKivara.setOnClickListener{
            regionId = "Большая Кивара"
            binding.textViewInfo.text = items.find{it.name == "Большая Кивара"}?.description.toString()
        }
        binding.imageButtonGavrilovka.setOnClickListener{
            regionId = "Гавриловка"
            binding.textViewInfo.text = items.find{it.name == "Гавриловка"}?.description.toString()
        }
        binding.imageButtonSvetloe.setOnClickListener{
            regionId = "Светлое"
            binding.textViewInfo.text = items.find{it.name == "Светлое"}?.description.toString()
        }
        binding.imageButtonIulskoe.setOnClickListener{
            regionId = "Июльское"
            binding.textViewInfo.text = items.find{it.name == "Июльское"}?.description.toString()
        }
        binding.imageButtonKamskoe.setOnClickListener{
            regionId = "Камское"
            binding.textViewInfo.text = items.find{it.name == "Камское"}?.description.toString()
        }
        binding.imageButtonKvarsa.setOnClickListener{
            regionId = "Кварса"
            binding.textViewInfo.text = items.find{it.name == "Кварса"}?.description.toString()
        }
        binding.imageButtonPerevoznoe.setOnClickListener{
            regionId = "Перевозное"
            binding.textViewInfo.text = items.find{it.name == "Перевозное"}?.description.toString()
        }
        binding.imageButtonPervomaiskoe.setOnClickListener{
            regionId = "Первомайское"
            binding.textViewInfo.text = items.find{it.name == "Первомайское"}?.description.toString()
        }
        binding.imageButtonVerhnyayaTalitsa.setOnClickListener{
            regionId = "Верхняя Талица"
            binding.textViewInfo.text = items.find{it.name == "Верхняя Талица"}?.description.toString()
        }

        binding.button.setOnClickListener{
            bundle.putInt("UserId", id!!)
            bundle.putString("RegionName", regionId)
            MAIN.navController.navigate(R.id.action_mainFragment_to_regionFragment, bundle)
        }

        /*
        if(id != -1){
            binding.imageButtonToOut.setImageResource(R.drawable.logout)
            lifecycleScope.launch {
                val curUser = db.getDao().getUserById(id)
                binding.textViewInfo.text = "${curUser?.id} \n ${curUser?.email} \n ${curUser?.accessLevel} \n ${curUser?.pass} \n ${curUser?.name} \n ${curUser?.surname} \n ${curUser?.age}"
            }
        }
        else{
            binding.textViewInfo.text = "Не авторизован"
        }
         */
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