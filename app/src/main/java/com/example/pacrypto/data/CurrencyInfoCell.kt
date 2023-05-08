package com.example.pacrypto.data

data class CurrencyInfoCell(
    val startTime: String,
    val endTime: String,
    val firstTradeTime: String,
    val lastTradeTime: String,
    val openRate: Double,
    val highRate: Double,
    val lowRate: Double,
    val closeRate: Double,
) {
    override fun toString(): String {
        return "CurrencyInfoCell(startTime='$startTime', endTime='$endTime', firstTradeTime='$firstTradeTime', lastTradeTime='$lastTradeTime', openRate=$openRate, highRate=$highRate, lowRate=$lowRate, closeRate=$closeRate)"
    }
}