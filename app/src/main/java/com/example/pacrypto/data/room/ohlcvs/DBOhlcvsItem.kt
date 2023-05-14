package com.example.pacrypto.data.room.ohlcvs

data class DBOhlcvsItem(
    val price_close: Double,
    val price_high: Double,
    val price_low: Double,
    val price_open: Double,
    val time_close: String,
    val time_open: String,
    val time_period_end: String,
    val time_period_start: String,
    val trades_count: Int,
    val volume_traded: Double
)