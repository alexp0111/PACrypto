package com.example.pacrypto.util

import com.example.pacrypto.data.SearchItem
import com.example.pacrypto.data.api_data.ApiAsset
import com.example.pacrypto.data.api_data.ApiListRates
import com.example.pacrypto.data.room.assets.DBAsset
import com.example.pacrypto.data.room.rates.DBListRates
import com.example.pacrypto.data.room.rates.Rate

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

fun ApiListRates.asDBType(): DBListRates {
    return DBListRates(
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