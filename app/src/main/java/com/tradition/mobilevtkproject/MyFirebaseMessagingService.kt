package com.tradition.mobilevtkproject

import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message from: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "New token: $token")
    }


    private fun showNotification(title: String?, message: String?) {
        val builder = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.mipmap.ic_launcher) // свой иконку добавь
            .setContentTitle(title ?: "Уведомление")
            .setContentText(message ?: "")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }
}