package com.example.pacrypto.data.worker

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pacrypto.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SubWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        startForegroundService("In da work")
        delay(5000L)
        startForegroundService("Work finished")
        return Result.success()
    }

    private suspend fun startForegroundService(str: String) {
        val notification = NotificationCompat.Builder(context, "sub_channel")
            .setSmallIcon(R.drawable.ic_delete)
            .setContentText(str)
            .setContentTitle("Title 123456")

        val notificationManager = getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(Random.nextInt(), notification.build())
    }
}