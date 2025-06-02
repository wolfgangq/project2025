package com.tradition.mobilevtkproject.data

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.tradition.mobilevtkproject.BuildConfig

object RC {
    private val remoteConfig by lazy { Firebase.remoteConfig }
    private const val TAG = "RemoteConfig"

    private val STARTUP_MIN_FETCH_INTERVAL_IN_SEC: Long = if (BuildConfig.DEBUG) 0 else 43200    // Авто фетч каждые 12 часов на релизе
    private const val REALTIME_MIN_FETCH_GAP_IN_MILLISEC: Long = 10000 //Ограничение (в мс) на минимальный принудительный вызов
                                                                       // fetch при детекте изменений в realtime listener'е
    private var lastRealtimeFetch: Long = 0

    fun init() {
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = STARTUP_MIN_FETCH_INTERVAL_IN_SEC
                fetchTimeoutInSeconds = 10
            }
        )

        //Версии 999, если во время проверки обновлений нет интернета для блокировки
        remoteConfig.setDefaultsAsync(
            mapOf(
                "version_minimum_supported" to "999.999.999",
                "version_latest" to "999.999.999",

                "link_telegram_browser" to "https://t.me/soopium",
                "link_telegram_client" to "tg://resolve?domain=soopium",

                "link_google_drive_repository" to "https://drive.google.com/drive/folders/10YuxH1BKjsqZ6Zt0G18PXvQlZw7N7HlJ?usp=sharing",
                "user_update_source" to "https://drive.google.com/drive/folders/10YuxH1BKjsqZ6Zt0G18PXvQlZw7N7HlJ?usp=sharing"
            )
        )

        // Realtime-listener
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(update: ConfigUpdate) {
                if (update.updatedKeys.isNotEmpty()) {
                    val now = System.currentTimeMillis()
                    if (now - lastRealtimeFetch > REALTIME_MIN_FETCH_GAP_IN_MILLISEC) {
                        lastRealtimeFetch = now
                        fetchAndActivate("Realtime update ⟵ ${update.updatedKeys}")
                    } else {
                        Log.d(TAG, "Skip fetch – called too soon after previous")
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "RemoteConfig realtime error: $error")
            }
        })

        fetchAndActivate("App start")
    }

    private fun fetchAndActivate(source: String) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "[$source] Fetch+activate OK")
                } else {
                    Log.w(TAG, "[$source] Fetch FAILED", task.exception)
                }
            }
    }
}