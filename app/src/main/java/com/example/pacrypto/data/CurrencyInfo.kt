package com.example.pacrypto.data

data class CurrencyInfo(
    val name: String,
    val ticker: String,
    val startDate: String,
    val endDate: String,
    val priceUsd: Double,
    val data: List<List<CurrencyInfoCell>>?, // 6 periods, each of which have amount of infoCells
) {
    override fun toString(): String {
        return "CurrencyInfo(name='$name', ticker='$ticker', startDate='$startDate', endDate='$endDate', data=$data)"
    }
}