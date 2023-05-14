package com.example.pacrypto.util

import com.example.pacrypto.data.SearchItem
import com.example.pacrypto.data.api_data.ApiAsset
import com.example.pacrypto.data.api_data.ApiListRates
import com.example.pacrypto.data.room.assets.DBAsset
import com.example.pacrypto.data.room.rates.DBListRates
import com.example.pacrypto.data.room.rates.Rate
import kotlin.math.abs

fun List<ApiAsset>.asDBType(): List<DBAsset> {
    val dBList = mutableListOf<DBAsset>()
    this.forEach {
        dBList.add(
            DBAsset(
                asset_id = it.asset_id,
                data_end = it.data_end,
                data_start = it.data_start,
                data_trade_end = it.data_trade_end,
                data_trade_start = it.data_trade_start,
                name = it.name,
                price_usd = it.price_usd,
                type_is_crypto = it.type_is_crypto,
                data_symbols_count = it.data_symbols_count,
            )
        )
    }
    return dBList
}

fun ApiListRates.asDBType(rate: String, type: String): DBListRates {
    return DBListRates(
        type = rate + "_" + type,
        asset_id_base = this.asset_id_base,
        rates = this.rates
    )
}

fun ArrayList<Rate>.findRateFor(assetId: String): Rate? {
    this.forEach {
        if (it.asset_id_quote.equals(assetId)) {
            return it
        }
    }
    return null
}

fun MutableList<SearchItem>.addAssets(data: List<DBAsset>) {
    this.clear()
    data.forEach {
        this.add(
            SearchItem(
                ticker = it.asset_id,
                name = it.name
            )
        )
    }
}

fun MutableList<SearchItem>.addRates(rates: List<Rate>) {
    this.forEach {
        val rate = (rates as ArrayList<Rate>).findRateFor(it.ticker)
        it.rateCurrent = rate?.rate
        it.timeUpdate = rate?.time
    }
}

fun MutableList<SearchItem>.calcPercents(ratesUSDAct: List<Rate>, ratesUSDPrv: List<Rate>) {
    this.forEach {
        val rate1 = (ratesUSDAct as ArrayList<Rate>).findRateFor(it.ticker)
        val rate2 = (ratesUSDPrv as ArrayList<Rate>).findRateFor(it.ticker)
        if (rate1 == null || rate2 == null) {
            it.percents = null
        } else {
            val percents = ((1.0 / rate1.rate) / (1.0 / rate2.rate)) * 100.0 - 100.0
            if (percents >= 0.0) {
                it.percents = "+" + String.format("%.2f", abs(percents)) + "%"
            } else {
                it.percents = "-" + String.format("%.2f", abs(percents)) + "%"
            }
        }
    }
}