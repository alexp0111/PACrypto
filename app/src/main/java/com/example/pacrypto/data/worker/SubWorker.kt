package com.example.pacrypto.data.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pacrypto.R
import com.example.pacrypto.api.CoinApi
import com.example.pacrypto.util.Rates
import com.example.pacrypto.util.Sub
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.random.Random

/**
 * Worker that used to generate notifications
 *
 * First of worker create the request to api
 * and then provides the result of to notification channel
 *
 * */
@HiltWorker
class SubWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val api: CoinApi,
    private val calendar: Calendar
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val assetId = inputData.getString("ticker")
        val weekdays = inputData.getIntArray("week_days") ?: Sub.weekDays.toIntArray()

        if (calendar.get(Calendar.DAY_OF_WEEK) !in weekdays.asList()
        ) return Result.success()

        val asset = withContext(Dispatchers.IO) {
            api.getExactAsset(assetId!!)[0]
        }
        startForegroundService(asset.asset_id, asset.name, asset.price_usd)
        return Result.success()
    }

    private fun startForegroundService(ticker: String, name: String, value: Double) {
        val notification = NotificationCompat.Builder(context, Sub.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(buildString {
                append(name)
                append(Sub(context).MESSAGE)
                append(String.format("%.2f", value))
                append(Rates.USD_MARKER)
            })
            .setContentTitle(ticker)

        val notificationManager = getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(Random.nextInt(), notification.build())
    }
}