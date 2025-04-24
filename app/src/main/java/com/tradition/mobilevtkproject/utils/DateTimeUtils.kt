package com.tradition.mobilevtkproject.utils

import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    fun getCurrentMoscowTimeApi26(): String {
        val moscowZoneId = ZoneId.of("Europe/Moscow")
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z")
        return ZonedDateTime.now(moscowZoneId).format(formatter)
    }

    fun getMoscowTime(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getCurrentMoscowTimeApi26()
        } else {
            Log.w("DateTimeUtils", "API < 26, using fallback")
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                .format(Date()) + " MSK"
        }
    }
}