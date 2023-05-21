package com.example.pacrypto

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.pacrypto.util.Sub
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Entrance point
 *
 * Used to init notification channel & workManager
 * */
@HiltAndroidApp
class PACryptoAppliaction : Application() {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            Sub.CHANNEL_ID,
            Sub(applicationContext).CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(hiltWorkerFactory).build()
        )
    }
}