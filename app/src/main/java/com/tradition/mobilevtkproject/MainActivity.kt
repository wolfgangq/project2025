package com.tradition.mobilevtkproject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.databinding.ActivityMainBinding
import kotlinx.coroutines.tasks.await
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.tradition.mobilevtkproject.screens.StartFragment

@Suppress("OVERRIDE_DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        MAIN = this

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



        /*val descSvetloe = "[Описание Кукуи]"
        val historySvetloe = "[История Кукуи]"
        val sightsListSvetloe = mutableListOf<UniversalRegionItem>()*/

        addRegion("Болгуры", descBolguri, historyBolguri, excursionsListBolguri,
            eventsListBolguri, sightsListBolguri, competitionsListBolguri)
        //addRegion("Светлое", descSvetloe, historySvetloe, sightsListSvetloe)
    }

//main start second
// |     |     |
//shop main account(settings)
    fun fireAlert(){
    val builder = AlertDialog.Builder(MAIN2)
    builder.setTitle("")
        .setMessage("Еще не реализовано")

    builder.setPositiveButton("Ок") { dialog, which ->
    }
    val alertDialog = builder.create()
    alertDialog.show()
    }

    fun goFragment(stackName: String?, fragment: Fragment, bundle: Bundle?){
        supportFragmentManager.commit{
            fragment.arguments = bundle
            setReorderingAllowed(true)
            replace(R.id.fragmentContainerView1, fragment)
            addToBackStack(stackName)
        }
    }

    var f = 0
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        var currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView1)
        if (currentFragment !is StartFragment) {
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

    companion object {
        fun successAuth(email: String){
            val intent = Intent(MAIN, TransitionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            MAIN.startActivity(intent)
            Toast.makeText(MAIN, "Вы вошли как $email", Toast.LENGTH_LONG).show()
        }
        suspend fun getUserInfo(id: String): Map<String?, Any?>? {
            val db = Firebase.firestore

            return try {
                val snapshot = db.collection("users").whereEqualTo("authId", id).get().await()
                val userInfo = mutableMapOf<String?, Any?>()
                for (doc in snapshot.documents) {
                    userInfo.putAll(doc.data!!)
                }
                userInfo
            } catch (e: Exception) {
                Log.w("Firestore", "Error getting documents.", e)
                null
            }
        }
        suspend fun userWithThisEmailExists(email: String): Boolean {
            val db = Firebase.firestore
            return try {
                val querySnapshot = db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .await()
                querySnapshot.isEmpty.not()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        fun addRegion(regionName: String, descReg: String, historyReg: String, excursionList: List<UniversalRegionItem>, eventList: List<UniversalRegionItem>,
                      sightList: List<UniversalRegionItem>, competitionList: List<UniversalRegionItem>){
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
                                for(item in excursionList){
                                    val excursion = hashMapOf(
                                        "excursionName" to item.title,
                                        "excursionDescription" to item.description,
                                        "excursionImageUrl" to item.imageUrl,
                                        "excursionFullDesc" to item.fullDescription
                                    )
                                    documentReference.collection("excursions")
                                        .add(excursion)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Sight added successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding sight", e)
                                        }
                                }
                                for(item in eventList){
                                    val event = hashMapOf(
                                        "eventName" to item.title,
                                        "eventDescription" to item.description,
                                        "eventImageUrl" to item.imageUrl,
                                        "eventFullDesc" to item.fullDescription
                                    )
                                    documentReference.collection("events")
                                        .add(event)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Sight added successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding sight", e)
                                        }
                                }
                                for(item in sightList){
                                    val sight = hashMapOf(
                                        "sightName" to item.title,
                                        "sightDescription" to item.description,
                                        "sightImageUrl" to item.imageUrl,
                                        "sightFullDesc" to item.fullDescription
                                    )
                                    documentReference.collection("sights")
                                        .add(sight)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Sight added successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding sight", e)
                                        }
                                }
                                for(item in competitionList){
                                    val competition = hashMapOf(
                                        "competitionName" to item.title,
                                        "competitionDescription" to item.description,
                                        "competitionImageUrl" to item.imageUrl,
                                        "competitionFullDesc" to item.fullDescription
                                    )
                                    documentReference.collection("competitions")
                                        .add(competition)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Sight added successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error adding sight", e)
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
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
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
        fun isEmailValid(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        fun isPassValid(pass: String): Boolean {
            return (pass.length >= 8)
        }
        @SuppressLint("DiscouragedApi")
        @Suppress("DEPRECATION")
        fun setColors(act: FragmentActivity, colorName: String){
            act.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            act.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            val colorResId = act.resources.getIdentifier(colorName, "color", act.packageName)

            if (colorResId != 0) {
                val color = ContextCompat.getColor(act, colorResId)
                act.window.statusBarColor = color
                act.window.navigationBarColor = color
            } else {
                Log.e("setColors", "Color resource not found: $colorName")
            }
        }
        fun toDefaultColors(act: FragmentActivity){
            act.window.statusBarColor = Color.TRANSPARENT
            act.window.navigationBarColor = Color.TRANSPARENT
        }
        fun setDarkStatusBar(act: FragmentActivity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = act.window?.insetsController
                controller?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else
                act.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        fun setLightStatusBar(act: FragmentActivity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = act.window?.insetsController
                controller?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else
                act.window?.decorView?.systemUiVisibility =
                    act.window?.decorView?.systemUiVisibility?.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())!!
        }
    }




}