package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tradition.mobilevtkproject.BuildConfig
import com.tradition.mobilevtkproject.MAIN
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.MainActivity.Companion.successAuth
import com.tradition.mobilevtkproject.databinding.FragmentStartBinding
import com.tradition.mobilevtkproject.utils.AppUtils
import com.tradition.mobilevtkproject.utils.NetworkUtils

@Suppress("DEPRECATION")
class StartFragment : Fragment() {

    lateinit var binding: FragmentStartBinding
    val bundle = Bundle()
    val db = Firebase.firestore
    val rc = Firebase.remoteConfig
    val auth = Firebase.auth
    private var activeDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonToReg.setOnClickListener{
            if(NetworkUtils.isInternetAvailable(MAIN)){
                (activity as? MainActivity)?.goFragment(null, FirstFragmentAge(), null)
            }
            else{
                val builder = AlertDialog.Builder(MAIN)
                builder.setTitle("Сеть")
                    .setMessage("Интернет недоступен")

                builder.setPositiveButton("Ок") { dialog, which ->
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }

        binding.buttonToAuth.setOnClickListener{
            if(NetworkUtils.isInternetAvailable(MAIN)){
                autoLogin()
                (activity as? MainActivity)?.goFragment(null, AuthFragment(), null)
            }
            else{
                val builder = AlertDialog.Builder(MAIN)
                builder.setTitle("Сеть")
                    .setMessage("Интернет недоступен")

                builder.setPositiveButton("Ок") { dialog, which ->
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }

        /*binding.buttonAsGuest.setOnClickListener{
            bundle.putInt("UserId", -1)
            MAIN.navController.navigate(R.id.action_startFragment_to_mainFragment, bundle)
        }*/

        binding.imageButtonInfo.setOnClickListener{
            (activity as? MainActivity)?.goFragment(null, InfoFragment(), null)
        }

        }
    fun turnButtons(bool: Boolean){
        binding.buttonToAuth.isClickable = bool
        binding.buttonToReg.isClickable = bool
        binding.imageButtonInfo.isClickable = bool
    }


    override fun onStart() {
        super.onStart()
        WindowUtils.setDarkStatusBarIcons(requireActivity())
        WindowUtils.resetStatusBarToDefault(requireActivity())
        WindowUtils.resetNavigationBarToDefault(requireActivity())
        turnButtons(false)
    }

    fun autoLogin() {
        var user = auth.currentUser
        if (user == null) {
            turnButtons(true)
            return
        }
        user.reload().addOnCompleteListener { task ->
            turnButtons(true)
            if (task.isSuccessful) {
                user = auth.currentUser
                if (user != null) {
                    successAuth(user!!.email!!)
                }
                else {
                    turnButtons(true)
                }
            } else {
                if (NetworkUtils.isInternetAvailable(MAIN)) {
                    Toast.makeText(
                        MAIN,
                        "Аккаунт ${user?.email} был заморожен",
                        Toast.LENGTH_SHORT
                    ).show()
                } else{
                    val builder = AlertDialog.Builder(MAIN)
                    builder.setTitle("Сеть")
                        .setMessage("Интернет недоступен")

                    builder.setPositiveButton("Ок") { dialog, which ->
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }
            }
        }
    }

    private fun forceFetchThen() {
        rc.fetch(0)
            .continueWithTask { rc.activate() }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkConditions()
                } else {
                    forceDataDialog()
                }
            }
    }

    private fun forceUpdateDialog(minVer: String) {
        val dlg = AlertDialog.Builder(requireContext())
            .setTitle("Версия устарела")
            .setMessage("Версия приложения ${BuildConfig.VERSION_NAME} больше не поддерживается. " +
                    "Обновите приложение до версии $minVer или выше, чтобы продолжить работу.")
            .setCancelable(false)
            .setPositiveButton("Обновить") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW,
                    rc.getString("user_update_source").toUri())
                requireContext().startActivity(intent)
                forceUpdateDialog(minVer)
            }
            .setNegativeButton("Выйти") { _, _ ->
                requireActivity().finish()
            }
                .create()

        activeDialog?.dismiss()
        activeDialog = dlg
        activeDialog!!.show()
    }
    private fun softUpdateDialog(context: Context, newestAvailableVersion: String?) {
        val builder = AlertDialog.Builder(MAIN)
        builder.setTitle("Информация")
            .setMessage("Уважаемый пользователь!\nДоступна новая версия $newestAvailableVersion.\nПожалуйста, обновитесь до самой последней версии!")

        builder.setPositiveButton("Обновить") { dialog, which ->
            val intent = Intent(Intent.ACTION_VIEW,
                rc.getString("user_update_source").toUri())
            context.startActivity(intent)
        }
        builder.setCancelable(false)

        activeDialog?.dismiss()
        activeDialog = builder.create()
        activeDialog!!.show()
    }
    private fun forceNetworkDialog() {
        val dlg = AlertDialog.Builder(requireContext())
            .setTitle("Сеть")
            .setMessage("Наблюдаются проблемы с подключением")
            .setCancelable(false)
            .setPositiveButton("Повторить соединение") { _, _ ->
                checkConditions()
            }
            .setNegativeButton("Выйти") { _, _ ->
                requireActivity().finish()
            }
            .create()

        activeDialog?.dismiss()
        activeDialog = dlg
        activeDialog!!.show()
    }
    private fun forceDataDialog() {
        val dlg = AlertDialog.Builder(requireContext())
            .setTitle("Ошибка")
            .setMessage("Не удалось получить данные об актуальной версии приложения")
            .setCancelable(false)
            .setPositiveButton("Повторить попытку") { _, _ ->
                checkConditions()
            }
            .setNegativeButton("Выйти") { _, _ ->
                requireActivity().finish()
            }
            .create()

        activeDialog?.dismiss()
        activeDialog = dlg
        activeDialog!!.show()
    }

    private fun checkConditions(){
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            forceNetworkDialog(); return
        }

        val installed = BuildConfig.VERSION_NAME
        val minAllowed = rc.getString("version_minimum_supported")
        val latest = rc.getString("version_latest")

        // Получение версий
        if (minAllowed.isBlank() || minAllowed == "999.999.999" || latest.isBlank() || latest == "999.999.999") {
            forceFetchThen()
            return
        }

        // Проверка поддержки версии
        if (AppUtils.compareVersions(installed, minAllowed) == AppUtils.VersionOrder.LOWER) {
            forceUpdateDialog(minAllowed); return
        }

        // Обновление до последней версии по желанию
        val sharedPrefs = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val lastSoftShown = sharedPrefs.getLong("lastSoftCheck", 0L)
        val now = System.currentTimeMillis()
        if (now - lastSoftShown > 6*3600*1000 &&
            AppUtils.compareVersions(installed, latest) == AppUtils.VersionOrder.LOWER) {

            softUpdateDialog(requireContext(), latest)
            sharedPrefs.edit().putLong("lastSoftCheck", now).apply()
            return
        }

        autoLogin()
    }

    override fun onResume() {
        super.onResume()
        checkConditions()
        WindowUtils.setDarkNavigationBarIcons(requireActivity())
    }
}