package com.tradition.mobilevtkproject

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.databinding.ActivityMainBinding
import com.tradition.mobilevtkproject.screens.MainFragmentMap
import kotlinx.coroutines.tasks.await

@Suppress("OVERRIDE_DEPRECATION")
class MainActivity : AppCompatActivity(), MainFragmentMap.OnDataPass {

    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        MAIN = this

        //var db = AppDatabase.getInstance(MAIN)
        /*val user = hashMapOf(
            "email" to "admin@gmail.com",
            "accessLevel" to Level.Admin,
            "pass" to "12345678",
            "name" to "Сергей",
            "surname" to "Русанов",
            "age" to 16,
            "balance" to 1000
        )
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                //Toast.makeText(MAIN, "Нет интернета", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Error adding document", e)
            }*/

        /*var startUser = User(email = "admin@gmail.com", accessLevel = Level.Admin, pass = "12345678", name = "Сергей", surname = "Русанов", age = 16, balance = 1000)
        var startUser2 = User(email = "test@gmail.com", pass = "12345678", name = "Test", surname = "Testov", age = 18)
        lifecycleScope.launch {
            val user = db.getDao().getUserByEmail("admin@gmail.com")
            if(user == null){
                withContext(Dispatchers.IO) {
                    db.getDao().insertItem(startUser)
                }
            }
            val user2 = db.getDao().getUserByEmail("test@gmail.com")
            if(user2 == null){
                withContext(Dispatchers.IO) {
                    db.getDao().insertItem(startUser2)
                }
            }
        }*/
    }


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val dest = navController.currentDestination?.id

        if (dest == R.id.startFragment || (dest == R.id.mainFragment)) {
            super.onBackPressedDispatcher.onBackPressed()
        } else {
            navController.popBackStack()
        }
    }

    companion object {
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
        suspend fun updateUserField(id: String, fieldName: String, newValue: Any) {
            val db = Firebase.firestore

            try {
                val snapshot = db.collection("users").whereEqualTo("authId", id).get().await()
                if (!snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    document.reference.update(fieldName, newValue).await()
                    Log.d("Firestore", "Field updated successfully.")
                } else {
                    Log.w("Firestore", "No matching documents found.")
                }
            } catch (e: Exception) {
                Log.w("Firestore", "Error updating document.", e)
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
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
        fun isEmailValid(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        fun isPassValid(pass: String): Boolean {
            return (pass.length >= 8)
        }
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


    override fun onDataPass(data: Int?) {
        id = data!!
    }


}