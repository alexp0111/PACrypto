package com.example.pacrypto.util

import android.content.Context
import com.example.pacrypto.data.api_data.ApiOhlcv
import com.example.pacrypto.data.room.ohlcvs.DBOhlcvs
import com.example.pacrypto.data.room.ohlcvs.DBOhlcvsItem
import java.util.*

fun List<List<ApiOhlcv>>.asDBType(type: String): DBOhlcvs {
    val newList = mutableListOf<MutableList<DBOhlcvsItem>>()
    this.forEach { extList ->
        val tmpList = mutableListOf<DBOhlcvsItem>()
        extList.forEach {
            tmpList.add(it.asDBType())
        }
        newList.add(tmpList)
    }

    return DBOhlcvs(type, newList)
}

private fun ApiOhlcv.asDBType(): DBOhlcvsItem {
    return DBOhlcvsItem(
        price_close = this.price_close,
        price_high = this.price_high,
        price_low = this.price_low,
        price_open = this.price_open,
        time_close = this.time_close,
        time_open = this.time_open,
        time_period_end = this.time_period_end,
        time_period_start = this.time_period_start,
        trades_count = this.trades_count,
        volume_traded = this.volume_traded
    )
}

fun List<DBOhlcvsItem>.asPairedList(): List<Pair<String, Float>> {
    val list = mutableListOf<Pair<String, Float>>()
    this.forEach {
        list.add(Pair(it.time_open, it.price_open.toFloat()))
    }
    return list
}

fun Int.toCalendarConstant(): Int {
    return when (this) {
        0 -> Calendar.MONDAY
        1 -> Calendar.TUESDAY
        2 -> Calendar.WEDNESDAY
        3 -> Calendar.THURSDAY
        4 -> Calendar.FRIDAY
        5 -> Calendar.SATURDAY
        6 -> Calendar.SUNDAY
        else -> Calendar.MONDAY
    }
}

fun String.toIndex(context: Context): Int {
    return when (this) {
        DatePickerValues(context).DAY -> 0
        DatePickerValues(context).WEEK-> 1
        DatePickerValues(context).MONTH-> 2
        DatePickerValues(context).QUARTER-> 3
        DatePickerValues(context).YEAR-> 4
        DatePickerValues(context).ALL -> 5
        else -> 0
    }
}
