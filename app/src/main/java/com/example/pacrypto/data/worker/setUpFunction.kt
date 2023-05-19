package com.example.pacrypto.data.worker

import android.content.Context
import androidx.work.*
import com.example.pacrypto.util.Sub
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

fun setUpSubscription(
    settedCalendar: Calendar,
    item: SubItem,
    context: Context,
    weekDays: List<Int> = Sub.weekDays
): UUID {

    val calendar = Calendar.getInstance()
    val curTimeInMinutes =
        (calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)).toLong()
    val goalTimeInMinutes =
        (settedCalendar.get(Calendar.HOUR_OF_DAY) * 60 + settedCalendar.get(Calendar.MINUTE)).toLong()

    val delay = if (curTimeInMinutes < goalTimeInMinutes) {
        Duration.ofMinutes(goalTimeInMinutes - curTimeInMinutes)
    } else {
        Duration.ofMinutes(1440 - curTimeInMinutes + goalTimeInMinutes)
    }

    val getRequest = PeriodicWorkRequestBuilder<SubWorker>(Duration.ofHours(24L))
        .setInputData(
            Data.Builder().putString("ticker", item.ticker)
                .putIntArray("week_days", weekDays.toIntArray()).build()
        )
        .setInitialDelay(delay)
        .setScheduleRequestedAt(24L, TimeUnit.HOURS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(
                    NetworkType.CONNECTED
                )
                .build()
        ).build()

    val workManager = WorkManager.getInstance(context)
    workManager.cancelWorkById(item.uuid)
    workManager.enqueue(getRequest)

    return getRequest.id
}