package com.tradition.mobilevtkproject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.databinding.ActivityTransitionBinding
import com.tradition.mobilevtkproject.screens.AccountFragment
import com.tradition.mobilevtkproject.screens.MainFragmentMap
import com.tradition.mobilevtkproject.screens.ShopFragment
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@Suppress("OVERRIDE_DEPRECATION")
class TransitionActivity : AppCompatActivity() {

    lateinit var binding: ActivityTransitionBinding
    var prevStack = ""
    val auth = Firebase.auth
    var id = auth.currentUser?.uid.toString()

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
        if (isFirstLaunch()) {
            showFirstDialog()
        }

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
                finish()
                //super.onBackPressedDispatcher.onBackPressed()
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

    private fun isFirstLaunch(): Boolean {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
            return true
        }
        return false
    }

    private fun showFirstDialog(){
        val builder = AlertDialog.Builder(MAIN2)
        builder.setTitle("Информация")
            .setMessage("Уважаемый пользователь! Данное приложение находится на стадии активной разработки.\nНа " +
                    "данный момент доступно лишь территориальное образование Болгуры.\nОтнеситесь к этому с пониманием!")

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
            @SuppressLint("SetTextI18n")
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

        handler.post(runnable)
    }

    private fun goLastFragment(stackName: String, fragment: Fragment){
        supportFragmentManager.commit{
            setReorderingAllowed(true)
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                fragment).commit()
            supportFragmentManager.restoreBackStack(stackName)
        }
    }

    fun goToAccount(){
        binding.tabLayout.getTabAt(2)?.select()
        resetStack("Account", AccountFragment())
    }

    private fun resetStack(stackName: String, fragment: Fragment){
        supportFragmentManager.commit{
            supportFragmentManager.popBackStack(stackName, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            setReorderingAllowed(true)
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                fragment).commit()
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

        fun createRegions(){
            val descBolguri = "Деревня находится в восточной части республики, в подзоне южной тайги, у реки Позимь, на расстоянии примерно 14 километ" +
                    "ров (по прямой) юго-западнее города Воткинска, административного центра района. Рядом проходит автодорога Ижевск — Воткинск"
            val historyBolguri = "Справка: Деревня Болгуры (по словамъ однихъ крестьянъ деревня получила свое названіе отъ «бугровъ» — холмовъ, которыми изобилуетъ эта мѣстн" +
                    "ость, а по словамъ другихъ—отъ народа болгаръ, жившаго въ прежнее время при устьѣ р. Камы) расположена при рѣчкѣ Болгаринкѣ, въ 60 вер. отъ города" +
                    " Сарапула, въ 12 вер. отъ волостнаго правленія и ближайшаго училища и въ 18 вер. отъ приходской церкви (въ Воткинскомъ заводѣ). Населяютъ деревню р" +
                    "усскіе, сельскіе обыватели и б. государственные крестьяне (сторонніе), православные и старообрядцы. Основана деревня болѣе 100 лѣтъ тому назадъ пере" +
                    "селенцами изъ д. Пустой Кварсы. Земля раздѣлена по ревизскимъ душамъ. Кромѣ надѣльной земли у крестьянъ имѣются 36,4 дес. захватной земли—исключител" +
                    "ьно сѣнокоса. Въ деревнѣ насчитывается 11 вѣялокъ и имѣются двѣ водяныхъ мукомольныхъ мельницы, въ общемъ владѣніи съ крестьянами другихъ деревень." + "\n" +
                    "Существуют две версии происхождения названия Болгуры:\n" +
                    "1)Людям долго обдумывать и придумывать название деревни не пришлось. От слов «большие угоры», бугры была названа деревня Болгуры. \n" +
                    "2)Люди издавна пытались устанавливать торговые отношения друг с другом. Самый лучший путь, дававший возможность торговать, был водный. И возможно плывя по Каме, Сиве, булгарские купцы могли заехать на нашу реку и дать ей название Болгуринка, отсюда пошло название Болгуры.\n" +
                    "\n" +
                    "Согласно переписи 1790 года в деревне Болгуры насчитывалось 6 хозяйств и проживало 80 жителей, считается, что первые поселенцы пришли на эти земли около 1760 года.\n" +
                    "Некоторое время деревня была приписана к Воткинску, Воткинскому заводу, в пользу которого жители отрабатывали несколько дней в году.\n"
            val excursionsListBolguri = listOf(
                UniversalRegionItem("Базовая экскурсия в деревне Болгуры", "Экскурсия по народным местам деревни Болгуры. Подойдет даже для тех, кто не знаком с культурой Удмуртской Республики!"),
            )
            val eventsListBolguri = mutableListOf<UniversalRegionItem>()

            val sightsListBolguri = listOf(
                UniversalRegionItem("Погибшим в Великой Отечественной войне", "",
                    "https://drive.usercontent.google.com/download?id=1VexEbLIVAEx07LJ-rDMiYUZ5uTghnkZg&export=view"),
                UniversalRegionItem("Памятник Жертвам белого террора",
                    "В 1977 году в деревне Болгуры был возведён памятник «Жертвам белого террора». Он находится на улице Центральная, между домами 63 и 65",
                    "https://drive.usercontent.google.com/download?id=1yCKhsm6jyiNf4kIIV4Mvt2NhwzhLYN-O&export=view"),
                UniversalRegionItem("Музей живых ремёсел и оленья ферма Добрянка", "«Добрянка» — музей живых ремёсел, объект сельского туризма. Комплекс, расположенный вблизи железнодорожной станции «Болгуры», демонстрирует традиционный быт и ремесленное мастерство русского народа. На территории также располагается оленья ферма и другие достопримечательности.)\n[56.941237, 53.771450]",
                    "https://drive.usercontent.google.com/download?id=1m3JRO-DrAU7BFYpj1a2DwEO6HIWPq6AK&export=view"),
                UniversalRegionItem("Питомник орехоплодных культур и растений экзотов", "[56.958503, 53.787957]",
                    "https://drive.usercontent.google.com/download?id=1ivJEqm08oZLjtJSzZF0eyQuf5oae7y9Z&export=view"),
                UniversalRegionItem("Пруд Сушилка", "Пруд «Сушилка» расположен в деревне Болгуры, используется для хозяйственно-питьевого и технического водоснабжения, а также является местом отдыха и рыболовства"),
                UniversalRegionItem("Холм Белая Глинка", "Одна из главных природных достопримечательностей деревни Болгуры. Высота горы — более 60 метров. Половина покрыта лесом, на вершине растёт рябина. Другая половина поросла травой и открывает панораму на деревню и близлежащие поля и леса\n[56.963533, 53.762140]",
                ),
                UniversalRegionItem("Гора Пупыш", "Одна из главных природных достопримечательностей деревни Болгуры. Высота горы — более 60 метров. Половина покрыта лесом, на вершине растёт рябина. Другая половина поросла травой и открывает панораму на деревню и близлежащие поля и леса\n[56.963533, 53.762140]",
                    "https://drive.usercontent.google.com/download?id=1qS0T_Cwr0kj_CcFBR9Z8EgcE1uSIPygs&export=view"),
                UniversalRegionItem("Обводненный карьер", "Бывший карьер, где когда-то на склоне холма добывали глину и увозили на стройки Воткинска. Обнажение получилось неглубоким, но большим по площади. Карьер пытались вернуть природе, засадили елями и соснами. Однако из-за бедности почвы саженцы не растут"),
                UniversalRegionItem("«Бабушкины ивы»", "«Бабушкины ивы» — это ивы, которым более 100 лет, они растут в деревне Болгуры. В народе их прозвали «Бабушки ивы».Чтобы обхватить одно такое дерево,понадобится компания из 5 человек\n[56.963533, 53.762140]",
                    "https://drive.usercontent.google.com/download?id=10upWL7l40Q1r2IVxBpdcT48VBFVpWa_Q&export=view"),
                UniversalRegionItem("Болгуринский СДК", "Учреждение находится по адресу: Удмуртская Республика, Воткинский район, д. Болгуры, ул. Школьная, д. 1е.\nВ Доме культуры проводятся занятия любительских объединений для всех возрастных групп, организуются выставки, конкурсы и экскурсии, творческие встречи с интересными людьми, экологические акции, праздничные вечера\n[56.963469, 53.764412]",
                    "https://drive.usercontent.google.com/download?id=1Xo-xiIUcGYtp07aIGxLsHexT-GBcCJT9&export=view"),
                UniversalRegionItem("Деревообрабатывающее предприятие", ""),
            )
            val competitionsListBolguri = listOf(
                UniversalRegionItem("Царь горы (Д.Болгуры [Холм Пупыш])", "Заберитесь на вершину холма Пупыш и сфотографируйтесь"),
                UniversalRegionItem("Олень? (Д.Болгуры [Музей живых ремёсел и оленья ферма Добрянка])", "Найдите оленя на ферме и сфотографируйтесь с ним"),
                UniversalRegionItem("Обними, если сможешь! (Д.Болгуры [Бабушкины Ивы])", "Обнимите иву полностью (можно не в одиночку), а затем сфотографируйтесь"),
            )

            addRegion("Болгуры", descBolguri, historyBolguri, excursionsListBolguri,
                eventsListBolguri, sightsListBolguri, competitionsListBolguri)
        }
        fun addRegion(regionName: String, descReg: String, historyReg: String, excursionList: List<UniversalRegionItem>, eventList: List<UniversalRegionItem>, sightList: List<UniversalRegionItem>, competitionList: List<UniversalRegionItem>){
            val db = Firebase.firestore
            db.collection("regions")
                .whereEqualTo("regionName", regionName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        val region = hashMapOf(
                            "regionName" to regionName,
                            "regionDescription" to descReg,
                            "regionHistory" to historyReg
                        )
                        db.collection("regions")
                            .add(region)
                            .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "DocumentSnapshot added with regionName: $regionName")
                                val fullList = hashMapOf(
                                    "excursions" to excursionList,
                                    "events" to eventList,
                                    "sights" to sightList,
                                    "competitions" to competitionList
                                )
                                for(collection in fullList.keys){
                                    for(item in fullList.get(collection)!!){
                                        val excursion = hashMapOf(
                                            "itemName" to item.title,
                                            "itemDescription" to item.description,
                                            "itemImageUrl" to item.imageUrl,
                                            "itemFullDescription" to item.fullDescription
                                        )
                                        documentReference.collection(collection)
                                            .add(excursion)
                                    }
                                }
                                /*for(item in excursionList){
                                    val excursion = hashMapOf(
                                        "itemName" to item.title,
                                        "itemDescription" to item.description,
                                        "itemImageUrl" to item.imageUrl,
                                        "itemFullDescription" to item.fullDescription
                                    )
                                    documentReference.collection("excursions")
                                        .add(excursion)
                                }
                                for(item in eventList){
                                    val event = hashMapOf(
                                        "itemName" to item.title,
                                        "itemDescription" to item.description,
                                        "itemImageUrl" to item.imageUrl,
                                        "itemFullDescription" to item.fullDescription
                                    )
                                    documentReference.collection("events")
                                        .add(event)
                                }
                                for(item in sightList){
                                    val sight = hashMapOf(
                                        "itemName" to item.title,
                                        "itemDescription" to item.description,
                                        "itemImageUrl" to item.imageUrl,
                                        "itemFullDescription" to item.fullDescription
                                    )
                                    documentReference.collection("sights")
                                        .add(sight)
                                }
                                for(item in competitionList){
                                    val competition = hashMapOf(
                                        "itemName" to item.title,
                                        "itemDescription" to item.description,
                                        "itemImageUrl" to item.imageUrl,
                                        "itemFullDescription" to item.fullDescription
                                    )
                                    documentReference.collection("competitions")
                                        .add(competition)
                                }*/
                            }
                    }
                    else {
                        Log.d(TAG, "Region with name $regionName already exists.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error getting documents: ", e)
                }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getTime(): String? {
            val moscowZoneId = ZoneId.of("Europe/Moscow")

            val moscowTime = ZonedDateTime.now(moscowZoneId)

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z")
            val formattedTime = moscowTime.format(formatter)
            return formattedTime
        }
    }

}