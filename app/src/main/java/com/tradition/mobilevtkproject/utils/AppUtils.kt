package com.tradition.mobilevtkproject.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object AppUtils {
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

}