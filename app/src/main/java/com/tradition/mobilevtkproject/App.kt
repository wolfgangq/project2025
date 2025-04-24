package com.tradition.mobilevtkproject
import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.initialize
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("96677da0-d380-4008-980c-0d6023f04acd")
        MapKitFactory.initialize(this)

        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        Firebase.initialize(this)
    }
}