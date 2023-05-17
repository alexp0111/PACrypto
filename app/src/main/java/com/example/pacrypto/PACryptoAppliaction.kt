package com.example.pacrypto

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PACryptoAppliaction: Application() {
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "sub_channel",
            "Оповещение о курсе",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}