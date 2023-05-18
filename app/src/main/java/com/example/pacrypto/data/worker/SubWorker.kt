package com.example.pacrypto.data.worker

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pacrypto.R
import com.example.pacrypto.api.CoinApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltWorker
class SubWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val api: CoinApi
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val asset_id = inputData.getString("ticker")
        val asset = withContext(Dispatchers.IO){
            api.getExactAsset(asset_id!!)[0]
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