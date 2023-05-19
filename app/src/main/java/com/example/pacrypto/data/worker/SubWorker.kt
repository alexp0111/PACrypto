package com.example.pacrypto.data.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.pacrypto.R
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.util.Sub
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@HiltWorker
class SubWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val api: CoinApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val assetId = inputData.getString("ticker")
        val weekdays = inputData.getIntArray("week_days") ?: Sub.weekDays.toIntArray()

        if (Calendar.getInstance()
                .get(Calendar.DAY_OF_WEEK) !in weekdays.asList()
        ) return Result.success()

        val asset = withContext(Dispatchers.IO) {
            api.getExactAsset(assetId!!)[0]
        }
        startForegroundService(asset.asset_id, asset.name, asset.price_usd)
        return Result.success()
    }

    private suspend fun startForegroundService(ticker: String, name: String, value: Double) {
        val notification = NotificationCompat.Builder(context, "sub_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText("$name price is: ${String.format("%.2f", value)}$")
            .setContentTitle(ticker)

        val notificationManager = getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(Random.nextInt(), notification.build())
    }
}