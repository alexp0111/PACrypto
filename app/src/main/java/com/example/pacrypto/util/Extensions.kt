package com.example.pacrypto.util

import com.example.pacrypto.data.api_data.ApiOhlcv
import com.example.pacrypto.data.room.ohlcvs.DBOhlcvs
import com.example.pacrypto.data.room.ohlcvs.DBOhlcvsItem

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
