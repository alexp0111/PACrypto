package com.example.pacrypto.data.worker

import android.content.Context
import androidx.work.*
import com.example.pacrypto.util.Sub
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This function is used for calculating the time of delay for worker
 *  & actually setting up new worker
 *
 *  It also provides worker info about ticker & days of week, in which
 *   it should be active
 *
 *  Important to note, that request builder have constraints such as
 *
 *          .setRequiredNetworkType(
 *              NetworkType.CONNECTED
 *          )
 *
 *  that helps worker to wait internet connection to come available
 * */
fun setUpSubscription(
    setCalendar: Calendar,
    item: SubItem,
    context: Context,
    weekDays: List<Int> = Sub.weekDays
): UUID {

    val calendar = Calendar.getInstance()
    val curTimeInMinutes =
        (calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)).toLong()
    val goalTimeInMinutes =
        (setCalendar.get(Calendar.HOUR_OF_DAY) * 60 + setCalendar.get(Calendar.MINUTE)).toLong()

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