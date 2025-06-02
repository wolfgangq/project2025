package com.tradition.mobilevtkproject.screens

import WindowUtils
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.tradition.mobilevtkproject.BuildConfig
import com.tradition.mobilevtkproject.MainActivity
import com.tradition.mobilevtkproject.R
import com.tradition.mobilevtkproject.TransitionActivity
import com.tradition.mobilevtkproject.databinding.FragmentInfoBinding
import com.tradition.mobilevtkproject.utils.AppUtils.VersionOrder
import com.tradition.mobilevtkproject.utils.AppUtils.compareVersions
import kotlinx.coroutines.launch

class InfoFragment : Fragment() {

    lateinit var binding: FragmentInfoBinding
    val remoteConfig = Firebase.remoteConfig

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = requireActivity()
        val curVer = BuildConfig.VERSION_NAME
        binding.textViewVersion.text = "Версия $curVer"
        lifecycleScope.launch {
            try {
                val latestVer = remoteConfig.getString("version_latest")
                if(latestVer != "999.999.999") {
                    when (compareVersions(curVer, latestVer)) {
                        VersionOrder.EQUAL -> {binding.textViewCheckUpdate.text = "Установлена актуальная версия"}
                        VersionOrder.LOWER -> {binding.textViewCheckUpdate.text = "Доступна новая версия $latestVer"}
                        VersionOrder.GREATER -> {binding.textViewCheckUpdate.text = "Вы являетесь участником тестирования"}
                    }
                }
            }
            catch(e: Exception) {}
        }
        binding.imageButtonBack.setOnClickListener{
            if (act is MainActivity) {
                (activity as? MainActivity)?.onBackPressed()
            }
            else{
                (activity as? TransitionActivity)?.onBackPressed()
            }
        }
        binding.buttonTelegram.setOnClickListener{
            try {
                // Клиент Telegram
                val intent = Intent(Intent.ACTION_VIEW, remoteConfig.getString("link_telegram_client").toUri())
                startActivity(intent)
            } catch (e: Exception) {
                // Браузер
                val fallbackIntent = Intent(Intent.ACTION_VIEW, remoteConfig.getString("link_telegram_browser").toUri())
                startActivity(fallbackIntent)
            }
        }
        binding.buttonToDrive.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, remoteConfig.getString("link_google_drive_repository").toUri())
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        WindowUtils.setStatusBarColor(requireActivity(), R.color.lightBlue)
        if (requireActivity() is TransitionActivity){
            WindowUtils.setNavigationBarColor(requireActivity(), R.color.white)
        }
        else{
            WindowUtils.setNavigationBarColor(requireActivity(), R.color.lightBlue)
        }
        WindowUtils.setLightStatusBarIcons(requireActivity())
        WindowUtils.setDarkNavigationBarIcons(requireActivity())
    }

}