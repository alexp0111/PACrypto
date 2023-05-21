package com.example.pacrypto.data.api_data

/**
 * Contains historical info in OHLCV format (Open, High, Low, Close, Volume)
 * */
data class ApiOhlcv(
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
    override fun toString(): String {
        return "ApiOhlcv(price_close=$price_close, price_high=$price_high, price_low=$price_low, price_open=$price_open, time_close='$time_close', time_open='$time_open', time_period_end='$time_period_end', time_period_start='$time_period_start', trades_count=$trades_count, volume_traded=$volume_traded)"
    }
}