package com.tradition.mobilevtkproject.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tradition.mobilevtkproject.MAIN
import kotlinx.coroutines.tasks.await

object AppUtils {
    fun getCurrentVersion(context: Context): String? {
        val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val installedVersion: String? = packageInfo.versionName
        return installedVersion
    }
    suspend fun getNewestVersion(): String? {
        val db: FirebaseFirestore = Firebase.firestore
        var newestAvailableVersion: String? = null
        db.collection("CURRENT_VERSION").get().addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                newestAvailableVersion = documents.first().get("versionNumber")?.toString()
            }
        }.await()
        return newestAvailableVersion
    }
    suspend fun checkForUpdates(context: Context, doIfNotNewest: () -> Unit) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val lastCheckTime = sharedPreferences.getLong("lastCheckTime", 0L)
        val currentTime = System.currentTimeMillis()
        val installedVersion = getCurrentVersion(context)
        try {
            val newestAvailableVersion = getNewestVersion()!!
            if (currentTime - lastCheckTime > 24*3600*1000) { // Раз в день
                when (compareVersions(installedVersion.toString(), newestAvailableVersion)) {
                    VersionOrder.LOWER -> {doIfNotNewest()}
                    else -> {}
                }
            }
        }
        catch(e: Exception) {}
    }

    enum class VersionOrder { GREATER, EQUAL, LOWER }

    /**
     * Семантическое версионирование
     *
     * GREATER — curVer > newestVer
     * EQUAL   — curVer == newestVer
     * LOWER   — curVer < newestVer
     **/
    fun compareVersions(curVer: String, newestVer: String): VersionOrder {

        // X.Y.Z[-stage.[W]]
        // curVer: "1.4.0-beta.1" > newestVer: "1.4.0-alpha" => GREATER
        data class Version(
            val major: Int,
            val minor: Int,
            val patch: Int,
            val stage: String?,   // null | "alpha" | "beta"
            val stageNum: Int
        )

        fun parse(v: String): Version {
            val (core, pre) = v.split("-", limit = 2).let {
                it[0] to it.getOrNull(1)
            }

            val (maj, min, pat) = core.split(".").map { it.toInt() }

            return if (pre == null) {
                Version(maj, min, pat, null, 0)
            } else {
                val m = Regex("^(alpha|beta)(?:\\.(\\d+))?$").matchEntire(pre)
                    ?: throw IllegalArgumentException("Неподдерживаемый суффикс: $pre")

                val stage = m.groupValues[1]
                val stageNum = m.groupValues[2].toIntOrNull() ?: 0

                Version(maj, min, pat, stage, stageNum)
            }
        }

        val a = parse(curVer)
        val b = parse(newestVer)

        // 1: major/minor/patch
        listOf(a.major to b.major, a.minor to b.minor, a.patch to b.patch).forEach { (x, y) ->
            if (x != y) return if (x > y) VersionOrder.GREATER else VersionOrder.LOWER
        }

        // 2: prerelease часть: release > beta > alpha
        if (a.stage == null && b.stage != null) return VersionOrder.GREATER
        if (a.stage != null && b.stage == null) return VersionOrder.LOWER

        if (a.stage != b.stage) {
            return when {
                a.stage == "beta" && b.stage == "alpha" -> VersionOrder.GREATER
                a.stage == "alpha" && b.stage == "beta" -> VersionOrder.LOWER
                else -> VersionOrder.EQUAL
            }
        }

        // 3: одинаковые стадии — сравниваем числа
        return when {
            a.stageNum > b.stageNum -> VersionOrder.GREATER
            a.stageNum < b.stageNum -> VersionOrder.LOWER
            else -> VersionOrder.EQUAL
        }
    }

    fun showUpdateDialog(context: Context, newestAvailableVersion: String?) {
        val builder = AlertDialog.Builder(MAIN)
        builder.setTitle("Информация")
            .setMessage("Уважаемый пользователь!\nУстановленная версия приложения: ${getCurrentVersion(context)}\nНовейшая версия: $newestAvailableVersion\nПожалуйста, обновитесь до новейшей версии")

        builder.setPositiveButton("Обновить") { dialog, which ->
            val intent = Intent(Intent.ACTION_VIEW,
                "https://drive.google.com/drive/folders/10YuxH1BKjsqZ6Zt0G18PXvQlZw7N7HlJ?usp=sharing".toUri())
            context.startActivity(intent)
        }
        builder.setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.show()
    }
}