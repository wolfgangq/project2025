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
        val sightsListBolguri = listOf(
            SightItem("Card 1", "Description 1", "https://ic.pics.livejournal.com/begemusja/12301520/1175308/1175308_original.jpg"),
            SightItem("Card 2", "Description 2", "https://mobileproject-410e3.web.app/usadba.jpg"),
            SightItem("Card 3", "Description 3", "https://drive.usercontent.google.com/download?id=1P1DNpurxJ5kb4wyeXs5npeK4fs0nks7_&export=view"),
            SightItem("Card 4", "Description 4", "https://ic.pics.livejournal.com/begemusja/12301520/1175308/1175308_original.jpg"),
            SightItem("Card 5", "Description 5", "https://cdn.culture.ru/images/dd356c8e-8357-53fd-b5a5-53f0ebbd41b9"),
        )

        val descKukui = "[Описание Кукуи]"
        val historyKukui = "[История Кукуи]"
        val sightsListKukui = mutableListOf<SightItem>()

        addRegion("Болгуры", descBolguri, historyBolguri, sightsListBolguri)
        addRegion("Кукуи", descKukui, historyKukui, sightsListKukui)
    }

//main start second
// |     |     |
//shop main account(settings)
    fun fireAlert(){
    val builder = AlertDialog.Builder(MAIN)
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
        fun addRegion(regionName: String, descReg: String, historyReg: String, sightList: List<SightItem>){
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
                                for(item in sightList){
                                    val sight = hashMapOf(
                                        "sightName" to item.title,
                                        "sightDescription" to item.description,
                                        "sightImageUrl" to item.imageUrl
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