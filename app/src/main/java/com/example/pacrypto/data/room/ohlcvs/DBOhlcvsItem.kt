package com.example.pacrypto.data.room.ohlcvs

/**
 * Represenation of ohlcv in DB
 * */
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
) {
    fun asDoubleList(): List<Double> {
        return listOf(
            price_open,
            price_high,
            price_low,
            price_close,
            volume_traded,
            trades_count.toDouble()
        )
    }
}