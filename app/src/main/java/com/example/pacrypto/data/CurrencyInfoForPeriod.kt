package com.example.pacrypto.data

data class CurrencyInfoForPeriod(
    val startTime: String,
    val endTime: String,
    val firstTradeTime: String,
    val lastTradeTime: String,
    val openPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val closePrice: Double,
    val volumeTraded: Double,
    val tradesCount: Long,
) {
    override fun toString(): String {
        return "CurrencyInfoForPeriod(startTime='$startTime', endTime='$endTime', firstTradeTime='$firstTradeTime', lastTradeTime='$lastTradeTime', openPrice=$openPrice, highPrice=$highPrice, lowPrice=$lowPrice, closePrice=$closePrice, volumeTraded=$volumeTraded, tradesCount=$tradesCount)"
    }
}